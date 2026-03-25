package com.example.stepcheck;

public interface GeminiCallback {
    public void onSuccess(String result);

    public void onFailure(Throwable error);
}
