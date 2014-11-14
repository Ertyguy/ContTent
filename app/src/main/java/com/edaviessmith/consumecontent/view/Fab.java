package com.edaviessmith.consumecontent.view;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;

import com.edaviessmith.consumecontent.R;

public class Fab extends View {

    private static String TAG = "Fab";
    private final TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private final Paint mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap mBitmap;
    private int mColor;
    private boolean mHidden = false;
    float[] hsv = new float[3];

    /**
     * The FAB button's Y position when it is displayed.
     */
    private float mYDisplayed = -1;
    /**
     * The FAB button's Y position when it is hidden.
     */
    private float mYHidden = -1;

    public Fab(Context context) {
        this(context, null);
    }

    public Fab(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }


    public Fab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.fab);
        mColor = a.getColor(R.styleable.fab_colour, Color.WHITE);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setColor(mColor);
        float radius, dx, dy;
        radius = a.getFloat(R.styleable.fab_shadowRadius, 10.0f);
        dx = a.getFloat(R.styleable.fab_shadowDx, 0.0f);
        dy = a.getFloat(R.styleable.fab_shadowDy, 3.5f);
        int color = a.getInteger(R.styleable.fab_shadowColor, Color.argb(100, 0, 0, 0));
        mButtonPaint.setShadowLayer(radius, dx, dy, color);

        Drawable drawable = a.getDrawable(R.styleable.fab_drawable);
        if (null != drawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        WindowManager mWindowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            mYHidden = size.y;
        } else mYHidden = display.getHeight();
    }


    public void setColor(int color) {
        mColor = color;
        mButtonPaint.setColor(mColor);
        invalidate();
    }

    public void setDrawable(Drawable drawable) {
        mBitmap = ((BitmapDrawable) drawable).getBitmap();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6), mButtonPaint);
        if (null != mBitmap) canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2, (getHeight() - mBitmap.getHeight()) / 2, mDrawablePaint);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mYDisplayed == -1)  mYDisplayed = getY();

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int color = mColor;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f;
            color = Color.HSVToColor(hsv);
        }

        mButtonPaint.setColor(color);
        invalidate();
        return super.onTouchEvent(event); //true; //
    }

    public void hide(boolean hide) {
        // If the hidden state is being updated
        if (mHidden != hide) {

            // Store the new hidden state
            mHidden = hide;

            // Animate the FAB to it's new Y position
            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "y", mHidden ? mYHidden : mYDisplayed).setDuration(500);
            animator.setInterpolator(mInterpolator);
            animator.start();
        }
    }

    public void listenTo(AbsListView listView) {
        if (null != listView) {
            //listView.setOnScrollListener(new DirectionScrollListener(this));
        }
    }


}



    /*Context _context;
    Paint mButtonPaint, mDrawablePaint;
    Bitmap mBitmap;
    int mScreenHeight;
    float currentY;
    boolean mHidden = false;
    private Display display;*/

/*
    public Fab(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        _context = context;
        init(context.getResources().getColor(R.color.accent));
        setFabDrawable(context.getResources().getDrawable(R.drawable.ic_add_white_18dp));
    }

    @SuppressLint("NewApi")
    public Fab(Context context) {
        super(context);
        _context = context;
        init(context.getResources().getColor(R.color.accent));
        setFabDrawable(context.getResources().getDrawable(R.drawable.ic_add_white_18dp));
    }
*/

   /* public Fab(Context context) {
        this(context, null);
    }

    public Fab(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }


    public Fab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FloatingActionButton);
        mColor = a.getColor(R.styleable.FloatingActionButton_color, Color.WHITE);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setColor(mColor);
        float radius, dx, dy;
        radius = a.getFloat(R.styleable.FloatingActionButton_shadowRadius, 10.0f);
        dx = a.getFloat(R.styleable.FloatingActionButton_shadowDx, 0.0f);
        dy = a.getFloat(R.styleable.FloatingActionButton_shadowDy, 3.5f);
        int color = a.getInteger(R.styleable.FloatingActionButton_shadowColor, Color.argb(100, 0, 0, 0));
        mButtonPaint.setShadowLayer(radius, dx, dy, color);

        Drawable drawable = a.getDrawable(R.styleable.FloatingActionButton_drawable);
        if (null != drawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        setWillNotDraw(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        WindowManager mWindowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            mYHidden = size.y;
        } else mYHidden = display.getHeight();
    }


    public void setFabColor(int fabColor) {
        init(fabColor);
    }

    public void setFabDrawable(Drawable fabDrawable) {
        Drawable myDrawable = fabDrawable;
        mBitmap = ((BitmapDrawable) myDrawable).getBitmap();
        invalidate();
    }

    @SuppressLint("NewApi")
    public void init(int fabColor) {
        setWillNotDraw(false);
        try {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } catch (NoSuchMethodError e2) {
            // http://stackoverflow.com/questions/16990588/setlayertype-substitute-for-android-2-3-3
            try {
                Method setLayerTypeMethod = this.getClass().getMethod("setLayerType", new Class[] { int.class, Paint.class });
                if (setLayerTypeMethod != null) setLayerTypeMethod.invoke(this, new Object[] { LAYER_TYPE_SOFTWARE, null });
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(fabColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint .setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        invalidate();

        WindowManager mWindowManager = (WindowManager) _context .getSystemService(Context.WINDOW_SERVICE);
        display = mWindowManager.getDefaultDisplay();
        Point size = getSize();
        mScreenHeight = size.y;
    }

    // http://stackoverflow.com/questions/10439033/getsize-not-supported-on-older-android-os-versions-getwidth-getheight-d
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @SuppressWarnings("deprecation")
    protected Point getSize() {
        final Point point = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(point);
        } else {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        return point;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setClickable(true);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6), mButtonPaint);
        canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2, (getHeight() - mBitmap.getHeight()) / 2, mDrawablePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)  setAlpha(1.0f);
        else if (event.getAction() == MotionEvent.ACTION_DOWN) setAlpha(0.6f);

        return super.onTouchEvent(event);
    }


    public void hideFab() {
        try {
            if (mHidden == false) {
                currentY = getY();
                ObjectAnimator mHideAnimation = ObjectAnimator.ofFloat(this, "Y", mScreenHeight);
                mHideAnimation.setInterpolator(new AccelerateInterpolator());
                mHideAnimation.start();
            }
        } catch (Exception e) {
            currentY = getY();
            Animation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, currentY);
            animation.setDuration(1000);
            animation.setFillAfter(true);
            startAnimation(animation);
            setVisibility(View.GONE);
        }
        mHidden = true;
    }

    public void showFab() {
        try {
            if (mHidden == true) {
                ObjectAnimator mShowAnimation = ObjectAnimator.ofFloat(this, "Y", currentY);
                mShowAnimation.setInterpolator(new DecelerateInterpolator());
                mShowAnimation.start();

            }
        } catch (Exception e) {
            setVisibility(View.VISIBLE);
            currentY = getY();
            Animation animation = new TranslateAnimation(0.0f, 0.0f, currentY, 0.0f);
            animation.setDuration(500);
            this.startAnimation(animation);
        }
        mHidden = false;
    }*/
