package com.example.stepcheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * A {@link BroadcastReceiver} that listens for changes in network connectivity.
 * It displays a dialog to the user when the internet connection is lost and dismisses it when the connection is restored.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    /**
     * A flag indicating whether the device is currently connected to the internet.
     */
    public static boolean isConnected = false;
    private static AlertDialog networkDialog;
    private Activity activity;

    /**
     * Constructs a new NetworkChangeReceiver.
     * @param activity The activity context used to display the dialog. This is important to tie the dialog's lifecycle to the activity.
     */
    public NetworkChangeReceiver(Activity activity) {
        this.activity = activity;
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     * It checks the current network state and shows or dismisses the connectivity dialog accordingly.
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received, which contains information about the network state change.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = (activeNetwork != null && activeNetwork.isConnected());

        if (!isConnected) {
            showDialog();
        } else {
            dismissDialog();
        }
    }

    /**
     * Shows a non-cancelable dialog to the user indicating that the internet connection has been lost.
     * The dialog provides a button to open the device's Wi-Fi settings.
     * It only shows the dialog if it's not already showing and the associated activity is not finishing.
     */
    private void showDialog() {
        // Ensure we don't create multiple dialogs and the activity is still valid
        if ((networkDialog == null || !networkDialog.isShowing()) && !activity.isFinishing()) {
            networkDialog = new AlertDialog.Builder(activity)
                    .setTitle("Connection Lost")
                    .setMessage("SpeakUp requires internet connection. Please reconnect.")
                    .setCancelable(false)
                    .setPositiveButton("Settings", (dialog, which) -> {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    })
                    .create();
            networkDialog.show();
        }
    }

    /**
     * Dismisses the network connection dialog if it is currently showing.
     * This is called when network connectivity is restored.
     */
    private void dismissDialog() {
        if (networkDialog != null && networkDialog.isShowing()) {
            networkDialog.dismiss();
            networkDialog = null;
        }
    }
}