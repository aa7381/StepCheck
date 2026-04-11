package com.example.stepcheck.Receiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.ref.WeakReference;

/**
 * A {@link BroadcastReceiver} that listens for changes in network connectivity.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    public static boolean isConnected = false;
    private static AlertDialog networkDialog;
    private final WeakReference<Activity> activityRef;

    public NetworkChangeReceiver(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

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

    private void showDialog() {
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;

        if (networkDialog == null || !networkDialog.isShowing()) {
            networkDialog = new AlertDialog.Builder(activity)
                    .setTitle("Connection Lost")
                    .setMessage("StepCheck requires internet connection. Please reconnect.")
                    .setCancelable(false)
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .create();
            networkDialog.show();
        }
    }

    private void dismissDialog() {
        if (networkDialog != null && networkDialog.isShowing()) {
            networkDialog.dismiss();
            networkDialog = null;
        }
    }
}
