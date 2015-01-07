package com.edaviessmith.contTent.util;

import android.app.Application;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.EditText;


public class App extends Application {


    public void postFocusText(final EditText editText) {
        editText.postDelayed(new Runnable() {
            public void run() {
                MotionEvent down = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0);
                MotionEvent up =  MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0);
                editText.dispatchTouchEvent(down);
                editText.dispatchTouchEvent(up);
                down.recycle();
                up.recycle();
                editText.selectAll();
                editText.setCursorVisible(true);
            }
        }, 200);
    }

    public void postFocusTextEnd(final EditText editText) {
        editText.postDelayed(new Runnable() {
            public void run() {
                MotionEvent down = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0);
                MotionEvent up =  MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0);
                editText.dispatchTouchEvent(down);
                editText.dispatchTouchEvent(up);
                down.recycle();
                up.recycle();
                editText.selectAll();
                editText.setCursorVisible(true);
                editText.setSelection(editText.getText().length());
            }
        }, 200);
    }
}
