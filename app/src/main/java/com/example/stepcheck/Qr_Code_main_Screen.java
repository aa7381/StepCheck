package com.example.stepcheck;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * The main activity of the application after the user logs in.
 * This activity hosts the main UI, including a BottomNavigationView for navigating
 * between different features (Fragments). It is responsible for setting up user-specific
 * permissions and loading the appropriate fragments based on user interaction.
 */
public class Qr_Code_main_Screen extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    private ActivityResultLauncher<ScanOptions> barLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_main_screen);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);



        setupPermissions();

        // Set home as the default selected item, which will load the scanner fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }


    /**
     * Configures the visibility of navigation items based on the current user's role.
     * It fetches the worker's data from Firebase and adjusts the visibility of items
     * like "Inventory" and "Manage Shift" in the BottomNavigationView.
     */
    private void setupPermissions() {
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
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

    /**
     * Helper method to replace the content of the fragment container with a new fragment.
     *
     * @param fragment The fragment to display.
     * @return true if the fragment was loaded successfully, false otherwise.
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
            return true;
        }
        return false;
    }

    /**
     * Called when an item in the bottom navigation menu is selected.
     * This method determines which fragment to load based on the selected menu item.
     *
     * @param item The selected menu item.
     * @return true to display the item as the selected item, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            fragment = new QrCodeMainScreenFragment();
        } else if (itemId == R.id.navigation_shift_entry) {
            // fragment = new ShiftEntryFragment(); // Add this later
        } else if (itemId == R.id.navigation_inventory) {
            fragment = new InventoryFragment();
        } else if (itemId == R.id.navigation_manage_shift) {
            // fragment = new ManageShiftFragment(); // Add this later
        } else if (itemId == R.id.navigation_settings) {
            fragment = new SettingsFragment();
        }
        return loadFragment(fragment);
    }
}
