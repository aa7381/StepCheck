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
 */
public class MasterClass extends AppCompatActivity {

    private NetworkChangeReceiver networkReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkReceiver = new NetworkChangeReceiver(this);


    }

    protected void onStart() {
        super.onStart();
        // Registering starts the automatic monitoring
        IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, networkFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Crucial: Unregister to avoid leaking the Activity context
        unregisterReceiver(networkReceiver);
    }
}
