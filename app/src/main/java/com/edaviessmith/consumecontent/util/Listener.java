package com.edaviessmith.consumecontent.util;

public interface Listener {
    public void onComplete(String value);
    public void onError(String value);
}