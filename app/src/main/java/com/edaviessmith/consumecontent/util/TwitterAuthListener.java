package com.edaviessmith.consumecontent.util;

public interface TwitterAuthListener {
    public void onComplete(String value);
    public void onError(String value);
}