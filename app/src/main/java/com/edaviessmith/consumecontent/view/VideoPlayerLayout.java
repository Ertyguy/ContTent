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
    private View player_v;

    private float initialX;
    private float initialY;
    boolean interceptTap;
    private int dragRange;
    private int top;
    private float dragOffset;

    private int playerWidth, playerHeight;
    private int playerMinWidth = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 200);
    private int playerMinHeight = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 101);

    int headerWidth, headerHeight;
    //private boolean animating;


    private static final float Y_MIN_VELOCITY = 1300;
    private static final float Y_MIN_DISTANCE = 120;

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
        top = 0;
        //dragOffset = 0;

        postDelayed(new Runnable() {
            @Override
            public void run() {
                //updateDragOffset(1);
                header_v.requestLayout();
                requestLayout();
                Log.d(TAG, "configuration changed");
            }
        }, 50);


        //TODO check landscape then maximize or minimize again
    }



    public void maximize() {

        final float initialOffset = dragOffset;

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
        top = (int) (dragRange *  dragOffset);

        description_v.setAlpha(1 - dragOffset);
        header_v.requestLayout();
        description_v.requestLayout();
        requestLayout();
    }


    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == header_v;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int t, int dx, int dy) {
            top = t;
            dragOffset = ((float) top / dragRange);
            description_v.setAlpha(1 - dragOffset);

            act.toggleVideoControls(false);

            header_v.invalidate();
            header_v.requestLayout();
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xVel, float yVel) {
            super.onViewReleased(releasedChild, xVel, yVel);

            if(dragOffset > 0 && dragOffset < 1) {
                if (yVel < 0 && yVel <= -Y_MIN_VELOCITY) {
                    maximize();
                } else if (yVel > 0 && yVel >= Y_MIN_VELOCITY) {
                    minimize();
                } else {
                    if (dragOffset < 0.5f) {
                        maximize();
                    } else {
                        minimize();
                    }
                }
            }

        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - header_v.getHeight() - header_v.getPaddingBottom();

            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return newTop;
        }

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
                dragSlop = (ev.getY() - initialY) > Y_MIN_DISTANCE || (ev.getY() - initialY) < -Y_MIN_DISTANCE;
                Log.d(TAG, "drag slop: "+(ev.getY() - initialY));
                if (ady > slop && adx > ady) {
                    viewDragHelper.cancel();
                    return false;
                }
            }

        }
        //Minimized or drag slop distance allows intercept
        return ((header_v.getBottom() == player_v.getBottom()) || dragSlop) && (viewDragHelper.shouldInterceptTouchEvent(ev) || interceptTap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        viewDragHelper.processTouchEvent(ev);

        boolean isDragViewHit = isViewHit(header_v, (int) ev.getX(), (int) ev.getY());
        boolean isSecondViewHit = isViewHit(description_v, (int) ev.getX(), (int) ev.getY());

        if (header_v.getBottom() == player_v.getBottom() && ev.getAction() == MotionEvent.ACTION_DOWN) {
            openClick = true;
        }
        if (openClick && ev.getAction() == MotionEvent.ACTION_UP) {
            maximize();
            openClick = false;
            //Log.d(TAG, "maximizing from fake click");
        }


        if (header_v.getTop() == player_v.getTop()) {
            header_v.dispatchTouchEvent(ev);  //Allow view click
        } else{
            header_v.dispatchTouchEvent(MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), MotionEvent.ACTION_CANCEL, ev.getX(), ev.getY(), ev.getMetaState()));
        }
        return isDragViewHit || isSecondViewHit;

    }
    boolean openClick;

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
    protected void onLayout(boolean changed, int left, int t, int right, int bottom) {
        //if(playerWidth == 0 || playerHeight == 0) {


        playerWidth = right - left; //player_v.getMeasuredWidth(); //right - left;//
        playerHeight = (int) Math.floor(playerWidth / (16f / 9f));

        //Log.d(TAG, "configuration layout "+playerWidth +", "+playerHeight);
        //}


        headerWidth = (int) (playerMinWidth + ((playerWidth - playerMinWidth) * (1 - dragOffset)));
        headerHeight = (int) Math.floor(headerWidth / (16f / 9f));
        if(headerHeight > getMeasuredHeight()) headerHeight = getMeasuredHeight(); //Ratio is bigger than screen size


        dragRange = (bottom - t) - headerHeight;

        header_v.getLayoutParams().width = headerWidth;
        header_v.getLayoutParams().height = headerHeight;
        header_v.layout(playerWidth - headerWidth, top, right, top + headerHeight);

        Log.d(TAG, "configuration layout" + " h: "+getMeasuredHeight() + ": "+headerHeight );
        description_v.layout(0, top + headerHeight, right, top + bottom);

        /*if(description_v.getVisibility() == View.VISIBLE) {
            if (header_v.getMeasuredHeight() == playerHeight)
                description_v.setVisibility(View.GONE);

        } else {
            if (header_v.getMeasuredHeight() != playerHeight)
                description_v.setVisibility(View.GONE);
        }*/
    }



}