package com.example.stepcheck.utils;

/**
 * Interface definition for a callback to be invoked when a Gemini AI request completes.
 * Provides methods to handle the successful response or a failure event.
 */
public interface GeminiCallback {
    /**
     * Called when the AI request is successful.
     * @param result The text result returned by the AI.
     */
    public void onSuccess(String result);

    /**
     * Called when the AI request fails.
     * @param error The exception or error that occurred during the request.
     */
    public void onFailure(Throwable error);
}
