package com.example.stepcheck.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.stepcheck.utils.ShiftService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A BroadcastReceiver that handles the automatic end-of-day synchronization.
 * It is triggered by an alarm to ensure that all shift data for the completed day
 * is properly processed and saved to the finished shifts record in Firebase.
 */
public class ShiftSyncReceiver extends BroadcastReceiver {
    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast.
     * This method triggers the shift synchronization process for the current date.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ShiftSyncReceiver", "End of day sync triggered automatically");
        
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdfDate.format(calendar.getTime());
        
        // מפעיל את הסנכרון האוטומטי עבור היום שהסתיים - כעת מקבל רק את התאריך
        ShiftService.checkEndOfDayAndSync(currentDate);
    }
}
