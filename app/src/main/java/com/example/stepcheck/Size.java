package com.example.stepcheck;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A data model class that represents the sizes of a shoe.
 * This class holds a two-dimensional array of strings to store size information.
 */
public class Size {

    private String[][] sizes;


    /**
     * Constructs a new Size object with the given sizes.
     * @param sizes A two-dimensional array of strings representing the sizes.
     */
    public Size(String[][] sizes) {
        this.sizes = sizes;
    }

    /**
     * Returns the sizes.
     * @return A two-dimensional array of strings representing the sizes.
     */
    public String[][] getSizes() {
        return sizes;
    }

    /**
     * Sets the sizes.
     * @param sizes A two-dimensional array of strings representing the sizes.
     */
    public void setSizes(String[][] sizes) {
        this.sizes = sizes;
    }

    /**
     * Constructs a new Size object with an empty sizes array.
     */
    public Size()
    {
        sizes = new String[0][0];

    }
}
