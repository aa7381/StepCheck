package com.example.stepcheck;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
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
    }
}
