package com.example.stepcheck;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Size {

    private String[][] sizes;


    public Size(String[][] sizes) {
        this.sizes = sizes;
    }

    public String[][] getSizes() {
        return sizes;
    }

    public void setSizes(String[][] sizes) {
        this.sizes = sizes;
    }

    public Size()
    {
        sizes = new String[0][0];

    }
}
