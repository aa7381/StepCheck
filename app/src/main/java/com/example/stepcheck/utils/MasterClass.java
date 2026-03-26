package com.example.stepcheck.utils;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.stepcheck.Receiver.NetworkChangeReceiver;
import com.example.stepcheck.R;
import com.example.stepcheck.activities.employee_management_acticity;
import com.example.stepcheck.fragments.InventoryFragment;
import com.example.stepcheck.fragments.QrCodeMainScreenFragment;
import com.example.stepcheck.fragments.SettingsFragment;
import com.example.stepcheck.fragments.ShiftEntryFragment;
import com.example.stepcheck.models.Worker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * מסגרת בסיסית לכל המסכים הראשיים של האפליקציה
 * כולל ניהול BottomNavigationView, טעינת Fragments וניטור רשת.
 */
public abstract class MasterClass extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    protected BottomNavigationView bottomNavigationView;
    private NetworkChangeReceiver networkReceiver;
    private ValueEventListener rankRequestListener;
    private AlertDialog currentRankDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // NetworkReceiver נוצר כאן
        networkReceiver = new NetworkChangeReceiver(this);
    }
    /**
     * כל מסך שמורש צריך לקרוא את הפונקציה הזו אחרי setContentView
     * כדי לאתחל את BottomNavigationView
     */
    protected void initBottomNavigationView(int bottomNavId) {
        bottomNavigationView = findViewById(bottomNavId);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this);
        }
        setupPermissions();
        handleInitialFragment();
    }

    /**
     * מטפל בטעינת Fragment ראשוני
     */
    private void handleInitialFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            loadFragment(new QrCodeMainScreenFragment());
        }
    }

    /**
     * מאפשר לכל משתמש לראות רק את מה שהוא מורשה לו
     */
    protected void setupPermissions() {
        if (bottomNavigationView == null) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FBRef.refBase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Worker worker = snapshot.getValue(Worker.class);
                    if (worker != null) {
                        Menu navMenu = bottomNavigationView.getMenu();
                        if (worker.getCanEditInventory()) {
                            navMenu.findItem(R.id.navigation_inventory).setVisible(true);
                        }
                        if (worker.getCan_manage_shift()) {
                            navMenu.findItem(R.id.navigation_manage_shift).setVisible(true);
                            // If user is a manager, listen for rank requests
                            listenForRankRequests(uid);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    /**
     * Listens for rank change requests sent to this manager.
     */
    private void listenForRankRequests(String managerId) {
        if (rankRequestListener != null) {
            FBRef.FBDB.getReference("RankRequests").child(managerId).removeEventListener(rankRequestListener);
        }

        rankRequestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean seen = snapshot.child("seenByManager").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(seen)) return; // Don't show again if already seen

                    Long timestamp = snapshot.child("timestamp").getValue(Long.class);
                    if (timestamp != null && System.currentTimeMillis() - timestamp > 180000) {
                        FBRef.FBDB.getReference("RankRequests").child(managerId).removeValue();
                        return;
                    }

                    String workerName = snapshot.child("workerName").getValue(String.class);
                    String requestedRank = snapshot.child("requestedRank").getValue(String.class);
                    String code = snapshot.child("code").getValue(String.class);

                    if (!isFinishing()) {
                        if (currentRankDialog != null && currentRankDialog.isShowing()) {
                            currentRankDialog.dismiss();
                        }
                        
                        // Mark as seen immediately so it won't pop up again
                        FBRef.FBDB.getReference("RankRequests").child(managerId).child("seenByManager").setValue(true);

                        currentRankDialog = new AlertDialog.Builder(MasterClass.this)
                                .setTitle("Rank Change Request")
                                .setMessage(workerName + " wants to change rank to " + requestedRank + ".\n\nVerification Code: " + code + "\n\n(Valid for 3 minutes)")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    if (currentRankDialog != null && currentRankDialog.isShowing()) {
                        currentRankDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        FBRef.FBDB.getReference("RankRequests").child(managerId).addValueEventListener(rankRequestListener);
    }

    /**
     * מחליף Fragment
     */
    protected boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**
     * טיפול בבחירה ב-BottomNavigationView
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (bottomNavigationView == null) return false;

        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.navigation_home) {
            fragment = new QrCodeMainScreenFragment();
        } else if (id == R.id.navigation_inventory) {
            fragment = new InventoryFragment();
        } else if (id == R.id.navigation_settings) {
            fragment = new SettingsFragment();
        }
        else if(id == R.id.navigation_shift_entry)
        {
            fragment = new ShiftEntryFragment();
        } else if (id == R.id.navigation_manage_shift) {
            fragment = new employee_management_acticity();
        }

        return loadFragment(fragment);
    }

    /**
     * ניטור רשת
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (networkReceiver != null) {
            IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkReceiver, networkFilter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
        // Remove rank request listener when activity is not visible to prevent multiple dialogs
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && rankRequestListener != null) {
            FBRef.FBDB.getReference("RankRequests").child(currentUser.getUid()).removeEventListener(rankRequestListener);
        }
        if (currentRankDialog != null && currentRankDialog.isShowing()) {
            currentRankDialog.dismiss();
        }
    }
}
