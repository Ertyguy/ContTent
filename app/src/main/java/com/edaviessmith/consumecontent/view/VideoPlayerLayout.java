package com.edaviessmith.consumecontent.view;

import android.content.Context;
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

import com.edaviessmith.consumecontent.R;
import com.edaviessmith.consumecontent.util.Var;


public class VideoPlayerLayout extends RelativeLayout {

    private final String TAG = "VideoPlayerLayout";
    private final ViewDragHelper viewDragHelper;

    private View header_v;
    private View description_v;
    private View player_v;

    private float initialX;
    private float initialY;
    private int dragRange;
    private int top;
    private float dragOffset;

    private int playerWidth, playerHeight;
    private int playerMinWidth = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 200);
    private int playerMinHeight = Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 101);

    int headerWidth, headerHeight;
    private boolean animating;

    private static final float Y_MIN_VELOCITY = 1300;

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
        viewDragHelper = ViewDragHelper.create(this, 0.001f, new DragHelperCallback());
    }

    public void maximize() {
        //smoothSlideTo(0f);
        expand(header_v);
    }
    public void minimize() {
        smoothSlideTo(1f);
    }
boolean isMinimized;
/*

    public void minimize() {
        RelativeLayout.LayoutParams playerParams = (RelativeLayout.LayoutParams) header_v.getLayoutParams();
        playerParams.width = playerMinWidth;
        playerParams.height = playerMinHeight;
        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams)player_v.getLayoutParams();
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        containerParams.bottomMargin = 5;//getResources().getDimensionPixelSize(R.dimen.player_minimized_margin);
        containerParams.rightMargin = 5;//getResources().getDimensionPixelSize(R.dimen.player_minimized_margin);
        header_v.requestLayout();
        player_v.requestLayout();
        isMinimized = true;
    }

    public void maximize() {
        RelativeLayout.LayoutParams playerParams =  (RelativeLayout.LayoutParams) header_v.getLayoutParams();
        playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        playerParams.height = playerHeight;

        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams)header_v.getLayoutParams();
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
        containerParams.bottomMargin = 0;
        containerParams.rightMargin = 0;
        header_v.requestLayout();
        player_v.requestLayout();
        isMinimized = false;
    }
*/

    public void expand(final View v) {

        final float initialOffset = dragOffset;

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float perc = (interpolatedTime * (1 - initialOffset)) + initialOffset;// Current pos to ending percentage

                Log.d(TAG,"animating "+(playerMinWidth + ((playerWidth - playerMinWidth) * (perc))) );


                headerWidth = (int) ((playerMinWidth + ((playerWidth - playerMinWidth) * (perc))) );
                headerHeight = (int) (headerWidth / (16f / 9f));

                top = (int) (dragRange *  perc);
                dragOffset = ((float) top / dragRange);

                v.requestLayout();

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
                animating = false;
                dragOffset = 0;
                v.clearAnimation();
            }
        });


        a.setDuration(1000);
        v.startAnimation(a);
        animating = true;
        Log.d(TAG,"start animating ");
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }



    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();

        int x = (int) (slideOffset * (getWidth() - playerMinWidth));
        int y = (int) (topBound + slideOffset * dragRange);

        if (viewDragHelper.smoothSlideViewTo(header_v, x, y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
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

            header_v.invalidate();
            header_v.requestLayout();
            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xVel, float yVel) {
            super.onViewReleased(releasedChild, xVel, yVel);

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

            /*int top = getPaddingTop();

            if (yVel > 0 || (yVel == 0 && dragOffset > 0.5f)) {
                top += dragRange;
            }
            viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);*/
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

        if ((action != MotionEvent.ACTION_DOWN)) {
            viewDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean interceptTap = false;

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
                if (ady > slop && adx > ady) {
                    viewDragHelper.cancel();
                    return false;
                }
            }
        }

        return viewDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        viewDragHelper.processTouchEvent(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        boolean isHeaderViewUnder = viewDragHelper.isViewUnder(header_v, (int) x, (int) y);
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                initialX = x;
                initialY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                final float dx = x - initialX;
                final float dy = y - initialY;
                final int slop = viewDragHelper.getTouchSlop();
                if (dx * dx + dy * dy < slop * slop && isHeaderViewUnder) {
                    if (dragOffset >= 0.5f) {
                        minimize();//smoothSlideTo(1f); //Should do nothing
                    } else {
                        maximize();//smoothSlideTo(0f);
                    }
                    return true;
                }
                break;
            }
        }

        return isHeaderViewUnder && isViewHit(header_v, (int) x, (int) y) || isViewHit(description_v, (int) x, (int) y);
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
    protected void onLayout(boolean changed, int left, int t, int right, int bottom) {
        if(playerWidth == 0 && playerHeight == 0) {
            playerWidth = header_v.getMeasuredWidth();
            playerHeight = (int) (header_v.getMeasuredWidth() / (16f / 9f));
        }


        dragRange = getHeight() - header_v.getHeight();


        //header_v.layout(playerWidth - header_v.getMeasuredWidth(), this.top, playerWidth, this.top + header_v.getMeasuredHeight());
        if(!animating) {
            headerWidth = (int) (playerMinWidth + ((playerWidth - playerMinWidth) * (1 - dragOffset)));  //(int) (1 / (1 - dragOffset)) * playerMinWidth;
            headerHeight = (int) (headerWidth / (16f / 9f));
        }
        header_v.getLayoutParams().width = headerWidth;
        header_v.getLayoutParams().height = headerHeight;
        //Log.d(TAG, "layout drag vars: "+Var.getDp(width));
        header_v.layout(playerWidth - headerWidth, top, right, top + headerHeight);

        description_v.layout(0, top + header_v.getMeasuredHeight(), right, top + bottom);
    }



}