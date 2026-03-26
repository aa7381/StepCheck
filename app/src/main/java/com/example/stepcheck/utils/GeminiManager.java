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
 * This class handles the initialization of the Gemini model and provides methods to send prompts with images for analysis.
 */
public class GeminiManager
{
    /**
     * Singleton instance of GeminiManager.
     */
    private static GeminiManager instance;
    
    /**
     * The GenerativeModel instance used to interact with Gemini.
     */
    private GenerativeModel gemini;

    /**
     * Private constructor to initialize the Gemini model using the API key from BuildConfig.
     */
    private GeminiManager() {

        gemini = new GenerativeModel(
                "gemini-2.5-flash",
        BuildConfig.Gemini_API_Key
        );
    }
    
    /**
     * Returns the singleton instance of GeminiManager.
     * @return The GeminiManager instance.
     */
    public static GeminiManager getInstance() {
        if (instance == null) {
            instance = new GeminiManager();
            }
        return instance;
        }

    /**
     * Sends a text prompt along with a list of bitmaps (photos) to the Gemini AI for analysis.
     * Results are returned asynchronously via the provided {@link GeminiCallback}.
     *
     * @param prompt   The text instructions for the AI.
     * @param photos   A list of Bitmap images to be analyzed.
     * @param callback The callback to handle success or failure of the AI request.
     */
    public void sendTextWithPhotosPrompt(String prompt, ArrayList<Bitmap> photos, final GeminiCallback callback) {
        List<Part> parts = new ArrayList<>();
        parts.add(new TextPart(prompt));
        for (Bitmap photo : photos) {
            parts.add(new ImagePart(photo));
        }

        // Convert the List to an array if needed, or use the constructor that accepts a List
        Content content = new Content(parts);

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
                            Log.e(TAG, "Gemini Error: " + ((Result.Failure) result).exception.getMessage());
                            callback.onFailure(((Result.Failure) result).exception);
                        } else {
                            GenerateContentResponse response = (GenerateContentResponse) result;
                            String text = response.getText();
                            if (text != null) {
                                callback.onSuccess(text);
                            } else {
                                callback.onFailure(new Exception("Response text is null"));
                            }
                        }
                    }
                });
    }
}
