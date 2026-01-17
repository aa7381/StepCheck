package com.example.stepcheck;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * A base activity class that handles common application tasks.
 * This class monitors network connectivity and phone state changes.
 * All other activities in the application should inherit from this class.
 */
public class MasterClass extends AppCompatActivity {

    private NetworkChangeReceiver networkReceiver;



    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkReceiver = new NetworkChangeReceiver(this);


    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    protected void onStart() {
        super.onStart();
        // Registering starts the automatic monitoring
        IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, networkFilter);
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Crucial: Unregister to avoid leaking the Activity context
        unregisterReceiver(networkReceiver);
    }
}
