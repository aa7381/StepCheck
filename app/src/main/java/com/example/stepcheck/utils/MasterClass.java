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
 * מסגרת בסיסית לכל המסכים הראשיים של האפליקציה.
 * כולל ניהול BottomNavigationView ובדיקת סטטוס משמרת.
 */
public abstract class MasterClass extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    protected BottomNavigationView bottomNavigationView;
    private NetworkChangeReceiver networkReceiver;
    private ValueEventListener rankRequestListener;
    private ValueEventListener shiftStatusListener;
    private AlertDialog currentRankDialog;
    protected boolean isInShift = false;
    protected String userRank = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkReceiver = new NetworkChangeReceiver(this);
        observeShiftStatus();
    }

    /**
     * פונקציית הבדיקה - מעדכנת את isInShift לפי הנתונים ב-Firebase
     */
    private void observeShiftStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            shiftStatusListener = FBRef.refBase.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Worker worker = snapshot.getValue(Worker.class);
                    if (worker != null) {
                        isInShift = worker.getInShift();
                        userRank = worker.getJob_rank();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    protected void initBottomNavigationView(int bottomNavId) {
        bottomNavigationView = findViewById(bottomNavId);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this);
        }
        setupPermissions();
        handleInitialFragment();
    }

    private void handleInitialFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            loadFragment(new QrCodeMainScreenFragment());
        }
    }

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
                        
                        // Check if Manager
                        if ("Manager".equals(worker.getJob_rank())) {
                            navMenu.findItem(R.id.navigation_inventory).setVisible(true);
                            navMenu.findItem(R.id.navigation_manage_shift).setVisible(true);
                            listenForRankRequests(uid);
                        } else {
                            if (worker.getCanEditInventory()) {
                                navMenu.findItem(R.id.navigation_inventory).setVisible(true);
                            }
                            if (worker.getCan_manage_shift()) {
                                navMenu.findItem(R.id.navigation_manage_shift).setVisible(true);
                                listenForRankRequests(uid);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    private void listenForRankRequests(String managerId) {
        if (rankRequestListener != null) {
            FBRef.FBDB.getReference("RankRequests").child(managerId).removeEventListener(rankRequestListener);
        }
        rankRequestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean seen = snapshot.child("seenByManager").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(seen)) return;
                    String workerName = snapshot.child("workerName").getValue(String.class);
                    String requestedRank = snapshot.child("requestedRank").getValue(String.class);
                    String code = snapshot.child("code").getValue(String.class);

                    if (!isFinishing()) {
                        if (currentRankDialog != null && currentRankDialog.isShowing()) currentRankDialog.dismiss();
                        FBRef.FBDB.getReference("RankRequests").child(managerId).child("seenByManager").setValue(true);
                        currentRankDialog = new AlertDialog.Builder(MasterClass.this)
                                .setTitle("Rank Change Request")
                                .setMessage(workerName + " wants to change rank to " + requestedRank + ".\nCode: " + code)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        FBRef.FBDB.getReference("RankRequests").child(managerId).addValueEventListener(rankRequestListener);
    }

    protected boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (bottomNavigationView == null) return false;
        int id = item.getItemId();

        // חסימת ניווט אם לא במשמרת (למעט דף כניסה למשמרת, הגדרות, ואם המשתמש מנהל)
        boolean isManager = "Manager".equals(userRank);
        if (!isInShift && !isManager && id != R.id.navigation_shift_entry && id != R.id.navigation_settings) {
            new AlertDialog.Builder(this)
                    .setTitle("Access Denied")
                    .setMessage("You must start a shift to access this feature.")
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }

        Fragment fragment = null;
        if (id == R.id.navigation_home) {
            fragment = new QrCodeMainScreenFragment();
        } else if (id == R.id.navigation_inventory) {
            fragment = new InventoryFragment();
        } else if (id == R.id.navigation_settings) {
            fragment = new SettingsFragment();
        } else if(id == R.id.navigation_shift_entry) {
            fragment = new ShiftEntryFragment();
        } else if (id == R.id.navigation_manage_shift) {
            fragment = new employee_management_acticity();
        }
        return loadFragment(fragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (networkReceiver != null) {
            registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (networkReceiver != null) unregisterReceiver(networkReceiver);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            if (rankRequestListener != null) FBRef.FBDB.getReference("RankRequests").child(currentUser.getUid()).removeEventListener(rankRequestListener);
            if (shiftStatusListener != null) FBRef.refBase.child(currentUser.getUid()).removeEventListener(shiftStatusListener);
        }
    }
}
