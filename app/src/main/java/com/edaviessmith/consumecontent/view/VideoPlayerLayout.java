package com.edaviessmith.consumecontent.view;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.edaviessmith.consumecontent.ContentActivity;
import com.edaviessmith.consumecontent.R;
import com.edaviessmith.consumecontent.util.Var;


public class VideoPlayerLayout extends RelativeLayout {

    private final String TAG = "VideoPlayerLayout";
    private final ViewDragHelper viewDragHelper;
    private final DragHelperCallback dragHelperCallback;

    private ContentActivity act;

    private View header_v;
    private View description_v;
    private View shade_v;
    private View player_v;

    private float initialX;
    private float initialY;
    boolean interceptTap;
    private int dragRange, dragXRange;
    private int top, left;
    private float dragOffset, dragXOffset;


    private int playerMinWidth = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 204);
    private int playerMinHeight = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 114);

    private int minimizedMargin = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 2);

    int headerWidth, headerHeight;
    private boolean isMinimized, isDraggingBottom;

    private static final float Y_MIN_VELOCITY = 1300;
    private static final float DRAG_MIN_DISTANCE = 120;
    private static final float CLAMP_DISTANCE = 20;

    public VideoPlayerLayout(Context context) {
        this(context, null);
    }

    public VideoPlayerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onFinishInflate() {
        header_v = findViewById(R.id.header_v);
        description_v = findViewById(R.id.description_v);
        shade_v = findViewById(R.id.shade_v);
        player_v = getRootView();

    }

    public VideoPlayerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dragHelperCallback = new DragHelperCallback();
        viewDragHelper = ViewDragHelper.create(this, 1f, dragHelperCallback);
    }

    public void init(ContentActivity activity) {
        act = activity;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){

        //dragOffset = (header_v.getTop() == player_v.getTop() ? 0: 1);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                header_v.requestLayout();
                requestLayout();
                Log.d(TAG, "configuration changed");
            }
        }, 50);


    }



    public void maximize() {

        final float initialOffset = dragOffset;
        left = 0;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                updateDragOffset(1 - ((interpolatedTime * (initialOffset)) + (1 - initialOffset)));// Current pos to ending percentage
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }

            @Override public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "maximize animation end");
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateDragOffset(0);
                        act.toggleVideoControls(true);
                        isMinimized = true;
                        isDraggingBottom = false;
                    }
                }, 50);
                header_v.clearAnimation();
            }
        });

        a.setDuration(400);
        header_v.startAnimation(a);
    }

    public void minimize() {

        final float initialOffset = dragOffset;
        left = 0;
        act.toggleVideoControls(false);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                updateDragOffset((interpolatedTime * (1 - initialOffset)) + initialOffset);// Current pos to ending percentage
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) { }
            @Override public void onAnimationRepeat(Animation animation) { }

            @Override public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "minimize animation end");
                isMinimized = true;
                isDraggingBottom = false;

                act.updateUIVisibility(); //Remove fullscreen effect
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateDragOffset(1);
                    }
                }, 50);

                header_v.clearAnimation();
            }
        });

        a.setDuration(400);
        header_v.startAnimation(a);
    }

    void updateDragOffset(float offset) {
        dragOffset = offset;
        //dragXOffset = 0;
        top = (int) ((getHeight() - playerMinHeight) *  dragOffset);
        //left = (int) ( getWidth() - ((getWidth() - playerMinWidth) * dragXOffset));

        //description_v.setAlpha(1 - dragOffset);
        shade_v.setAlpha(1 - dragOffset);

        header_v.requestLayout();
        description_v.requestLayout();
        shade_v.requestLayout();
        requestLayout();
    }


    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == header_v;
        }

        @Override
        public void onViewPositionChanged(View changedView, int l, int t, int dx, int dy) {
            top = t;
            /*if(isDraggingBottom) */
            //left = l;

            dragOffset = ((float) top / dragRange);
            //dragXOffset = ((float) left / dragXRange);
            //description_v.setAlpha(1 - dragOffset);

            shade_v.setAlpha(1 - dragOffset);
            act.toggleVideoControls(false);

            //if(dragOffset < 1) isMinimized = false; //Don't think it's needed

            header_v.invalidate();
            header_v.requestLayout();
            shade_v.requestLayout();
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xVel, float yVel) {
            super.onViewReleased(releasedChild, xVel, yVel);

            Log.d(TAG, "onViewReleased" + " off"+dragOffset +" dragging:"+ isDraggingBottom);



            if(/*!isDraggingBottom &&*/ dragOffset > 0 && dragOffset < 1) {
                if (yVel < 0 && yVel <= -Y_MIN_VELOCITY) {
                    maximize();
                } else if (yVel > 0 && yVel >= Y_MIN_VELOCITY) {
                    minimize();
                } else {
                    if (dragOffset < 0.5f) {
                        Log.d(TAG, "maximize animation start");
                        maximize();
                    } else {
                        Log.d(TAG, "minimize animation start");
                        minimize();
                    }
                }
            }
            /*if(isDraggingBottom) {
                left = 0;
                //header_v.requestLayout();
                boolean del =  Math.abs(xVel) >= Y_MIN_VELOCITY || header_v.getLeft() < ((getWidth() - headerWidth) / 2);
                Log.d(TAG, "isDraggingBottom animation trigger "+del + (header_v.getLeft())  +" < "+ ((getWidth() - headerWidth) / 2));

                minimize();
                //isDraggingBottom = false;

            }*/

        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragRange;
        }

       /* @Override
        public int getViewHorizontalDragRange(View child) {
            return getWidth() - playerMinWidth;
        }*/

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int newTop = getHeight() - headerHeight;
            //Log.d(TAG,"clamp Vert: "+ Math.abs(dy) +" - "+ isMinimized);
            if (!isDraggingBottom && ((isMinimized && Math.abs(dy) >= CLAMP_DISTANCE) || (!isMinimized && headerHeight != playerMinHeight ))) {

                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - playerMinHeight;
                //Log.d(TAG,"newTop:  "+newTop +" - "+ Math.min(Math.max(top, topBound), bottomBound));
                newTop = Math.min(Math.max(top, topBound), bottomBound);
                left = 0;

            }

            return newTop;
        }

        /*@Override
        public int clampViewPositionHorizontal(View child, int l, int dx) {
            int posLeft = getWidth() - headerWidth;

            if ((headerHeight == playerMinHeight && Math.abs(dx) >= CLAMP_DISTANCE) || (headerHeight == playerMinHeight && (header_v.getRight() != getRight()))) {
                left -= dx;
                //isDraggingBottom = true;

                Log.d(TAG,"clamp hor: moving "+l);
            }
            return posLeft;


        }*/

    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            interceptTap = false;
            viewDragHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean dragSlop = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                initialX = x;
                initialY = y;
                interceptTap = viewDragHelper.isViewUnder(header_v, (int) x, (int) y);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float adx = Math.abs(x - initialX);
                final float ady = Math.abs(y - initialY);
                final int slop = viewDragHelper.getTouchSlop();
                dragSlop = Math.abs(ev.getY() - initialY) > DRAG_MIN_DISTANCE;// || Math.abs(ev.getX() - initialY) > DRAG_MIN_DISTANCE;; //|| (ev.getY() - initialY) < -DRAG_MIN_DISTANCE;


                //Log.d(TAG, "drag slop: "+(ev.getY() - initialY));
                if (ady > slop && adx > ady) {
                    viewDragHelper.cancel();
                    return false;
                }
            }

        }

        //Minimized or drag slop distance allows intercept
        return (/*!isDraggingBottom && */dragSlop) && (viewDragHelper.shouldInterceptTouchEvent(ev) || interceptTap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        viewDragHelper.processTouchEvent(ev);

        boolean isDragViewHit = isViewHit(header_v, (int) ev.getX(), (int) ev.getY());
        boolean isSecondViewHit = isViewHit(description_v, (int) ev.getX(), (int) ev.getY());


        if (/*!isDraggingBottom &&*/ isMinimized && ev.getAction() == MotionEvent.ACTION_UP) {
            isMinimized = false;
            maximize();
            Log.d(TAG, "maximizing from fake click");

            return true;
        }

        if (header_v.getTop() == player_v.getTop()) {
            header_v.dispatchTouchEvent(ev);  //Allow view click
        } else {
            header_v.dispatchTouchEvent(MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), MotionEvent.ACTION_CANCEL, ev.getX(), ev.getY(), ev.getMetaState()));
        }
        return isDragViewHit || isSecondViewHit;

    }

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return (screenX >= viewLocation[0]) && (screenX < viewLocation[0] + view.getWidth()) && (screenY >= viewLocation[1]) && (screenY < viewLocation[1] + view.getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int right, int bottom) {

        headerWidth = (int) (playerMinWidth + ((right - l - playerMinWidth) * (1 - dragOffset)));
        headerHeight = (int) (playerMinHeight + ((((right - l)/ (16f / 9f)) - playerMinHeight) * (1 - dragOffset)));//Math.floor(headerWidth / (16f / 9f));
        if(headerHeight > getMeasuredHeight()) headerHeight = getMeasuredHeight(); //Ratio is bigger than screen size


        dragRange = ((bottom - t) - playerMinHeight);// - minimizedMargin * 2; // headerHeight
        //dragXRange = ((right - l) - playerMinWidth);

        Log.d(TAG, "onLayout" + " r: "+dragRange +", top:"+top+" - off"+dragOffset);
        Log.d(TAG, "onLayout horizontal: " + " r: "+dragXRange +", left:"+left+" - off"+dragXOffset);
        if(isMinimized && top != dragRange) top = dragRange; //Resize minimized (for configChange)

        Log.d(TAG,"onLayout left "+left);
        header_v.getLayoutParams().width = headerWidth;
        header_v.getLayoutParams().height = headerHeight;
        header_v.setPadding((int)(minimizedMargin * dragOffset), (int)(minimizedMargin * dragOffset), (int)(minimizedMargin * dragOffset), (int)(minimizedMargin * dragOffset));
        header_v.layout(left, top, right - left + headerWidth, top + headerHeight);
        //header_v.layout(right - left - headerWidth, top, right - left, top + headerHeight);
        //header_v.layout(right - left, top, right - left - headerWidth, top + headerHeight);
        //header_v.layout((right - left) - headerWidth , top, right - left , top + headerHeight);
        description_v.layout(0, (int) (top + (headerHeight * (dragOffset + 1))), right, top + bottom);
        shade_v.layout(l, t, right, bottom);
    }

}