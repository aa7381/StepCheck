package com.example.stepcheck.utils;


import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.stepcheck.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.ImagePart;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.TextPart;

import java.util.ArrayList;
import java.util.List;

import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * Singleton manager class for interacting with Google's Gemini AI.
 */
public class GeminiManager
{
    private static GeminiManager instance;
    private GenerativeModel gemini;

    private GeminiManager() {
        gemini = new GenerativeModel(
                "gemini-2.5-flash",
        BuildConfig.Gemini_API_Key
        );
    }
    
    public static GeminiManager getInstance() {
        if (instance == null) {
            instance = new GeminiManager();
            }
        return instance;

        }

    public void sendTextWithPhotosPrompt(String prompt, ArrayList<Bitmap> photos, final GeminiCallback callback) {
        List<Part> parts = new ArrayList<>();
        parts.add(new TextPart(prompt));
        if (photos != null) {
            for (Bitmap photo : photos) {
                if (photo != null) {
                    parts.add(new ImagePart(photo));
                }
            }
        }

        Content content = new Content(parts);

        try {
            gemini.generateContent(new Content[]{content},
                    new Continuation<GenerateContentResponse>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object result) {
                            if (result instanceof Result.Failure) {
                                Throwable exception = ((Result.Failure) result).exception;
                                Log.e(TAG, "Gemini Error: " + (exception != null ? exception.getMessage() : "Unknown error"));
                                callback.onFailure(exception != null ? exception : new Exception("Unknown Gemini error"));
                            } else if (result instanceof GenerateContentResponse) {
                                GenerateContentResponse response = (GenerateContentResponse) result;
                                try {
                                    String text = response.getText();
                                    if (text != null) {
                                        callback.onSuccess(text);
                                    } else {
                                        callback.onFailure(new Exception("Response text is null"));
                                    }
                                } catch (Exception e) {
                                    callback.onFailure(e);
                                }
                            } else {
                                callback.onFailure(new Exception("Unexpected result type from Gemini"));
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error initiating Gemini request", e);
            callback.onFailure(e);
        }
    }
}
