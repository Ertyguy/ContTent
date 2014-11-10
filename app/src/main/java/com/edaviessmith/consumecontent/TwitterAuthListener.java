package com.edaviessmith.consumecontent;

public interface TwitterAuthListener {
    public void onComplete(String value);
    public void onError(String value);
}