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
        viewDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
    }

    public void maximize() {
        smoothSlideTo(0f);
    }

    boolean smoothSlideTo(float slideOffset) {
        final int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * dragRange);

        if (viewDragHelper.smoothSlideViewTo(header_v, header_v.getLeft(), y)) {
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

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) header_v.getLayoutParams();
            params.width = (int) (1 / (1 - dragOffset)) * playerMinWidth; //((1 - dragOffset / 2) * playerWidth);
            //params.height = (int) dragOffset * playerHeight; //((1 - dragOffset / 2) * playerHeight);

            Log.d(TAG, "viewPositionChanged: " + params.height + " offset: " + dragOffset + " - " + playerWidth);

            /*
            header_v.setPivotX(header_v.getWidth());
            header_v.setPivotY(header_v.getHeight());
            header_v.setScaleX(1 - dragOffset / 2);
            header_v.setScaleY(1 - dragOffset / 2);
            */
            description_v.setAlpha(1 - dragOffset);

            requestLayout();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top = getPaddingTop();
            if (yvel > 0 || (yvel == 0 && dragOffset > 0.5f)) {
                top += dragRange;
            }
            viewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
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
                    if (dragOffset == 0) {
                        return smoothSlideTo(1f); //Should do nothing
                    } else {
                        smoothSlideTo(0f);
                    }
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
            playerWidth = player_v.getMeasuredWidth();
            playerHeight = (int) (player_v.getMeasuredWidth() / 1.6666f) - 1;
        }


        dragRange = getHeight() - header_v.getHeight();


        //header_v.layout(playerWidth - header_v.getMeasuredWidth(), this.top, playerWidth, this.top + header_v.getMeasuredHeight());
        header_v.layout(playerWidth - header_v.getMeasuredWidth(), top, right, top + header_v.getMeasuredHeight());

        description_v.layout(0, top + header_v.getMeasuredHeight(), right, top + bottom);
    }

}