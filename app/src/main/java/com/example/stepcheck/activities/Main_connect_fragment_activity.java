package com.example.stepcheck.activities;

import android.os.Bundle;

import com.example.stepcheck.utils.MasterClass;
import com.example.stepcheck.R;

/**
 * The main activity of the application after the user logs in.
 * This activity hosts the main navigation of the app using a BottomNavigationView,
 * and displays different fragments based on user selection.
 * It also handles user permissions to show or hide certain navigation items.
 * Inherits from MasterClass to handle network and phone state changes.
 */
public class Main_connect_fragment_activity extends MasterClass {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_connect_fragment_activity);
        initBottomNavigationView(R.id.bottom_navigation);
    }

}
