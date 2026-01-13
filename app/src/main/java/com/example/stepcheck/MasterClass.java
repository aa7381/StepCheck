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

    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback callback;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> dismissDialog());
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> showDialog());
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        cm.registerDefaultNetworkCallback(callback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cm.unregisterNetworkCallback(callback);
    }

    private void showDialog() {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle("Connection Lost")
                    .setMessage("SpeakUp requires internet connection")
                    .setCancelable(false)
                    .setPositiveButton("Settings",
                            (d, w) -> startActivity(
                                    new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)))
                    .show();
        }
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
