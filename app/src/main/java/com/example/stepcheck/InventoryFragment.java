package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refBase2;
import static com.example.stepcheck.FBRef.refBase3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass that displays the inventory screen.
 * This fragment allows the user to search for products, scan QR codes, and add new shoes.
 */
public class InventoryFragment extends Fragment {
    private Button AddNewShoe, btnScanQr;
    private SearchView productSearchView;
    private RecyclerView rvProducts;
    private ProductAdapter adapter;

    String safeKey;


    private ActivityResultLauncher<ScanOptions> barLauncher;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any saved state has been restored in to the view.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AddNewShoe = view.findViewById(R.id.AddNewShoe);
        btnScanQr = view.findViewById(R.id.btnScanQr);
        productSearchView = view.findViewById(R.id.product_search_view);
        rvProducts = view.findViewById(R.id.rvProducts);

        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter();
        rvProducts.setAdapter(adapter);
        rvProducts.setVisibility(View.GONE);

        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(requireActivity(), shoe_data_screen.class);
                intent.putExtra("qr_code_data", product.getId());
                startActivity(intent);
            }
        });


        fetchProducts();

        productSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (rvProducts.getVisibility() == View.GONE) {
                    rvProducts.setVisibility(View.VISIBLE);
                }
                adapter.filter(newText);
                return true;
            }
        });

        productSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvProducts.setVisibility(View.VISIBLE);
                adapter.filter("");
            }
        });

        productSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                rvProducts.setVisibility(View.GONE);
                return false;
            }
        });

        if (AddNewShoe != null) {
            AddNewShoe.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    addNewShoe();
                }
            });
        }

        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result != null && result.getContents() != null) {
                String qr_code_data = result.getContents();
                safeKey = Base64.encodeToString(qr_code_data.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
                check();
            }
        });

        if (btnScanQr != null) {
            btnScanQr.setOnClickListener(v -> {
                scanCode();
            });
        }
    }


    /**
     * Fetches the list of products from the Firebase Realtime Database.
     */
    private void fetchProducts() {
        refBase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String shoeName = snapshot.child("shoe_name").getValue(String.class);
                    if (shoeName != null) {
                        productList.add(new Product(snapshot.getKey(), shoeName));
                    }
                }
                adapter.setData(productList);

                if (rvProducts.getVisibility() == View.VISIBLE) {
                    adapter.filter(productSearchView.getQuery().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Initiates the QR code scanning process.
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    /**
     * Navigates to the screen for adding a new shoe.
     */
    public void addNewShoe() {
        Intent intent = new Intent(requireActivity(), add_new_shoe.class);
        startActivity(intent);
    }


    private void check() {
            try {

                refBase2.child(safeKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {



                            Toast.makeText(getContext(), "Shoe is not exists", Toast.LENGTH_SHORT).show();
                        } else
                        {


                                Intent intent = new Intent(requireActivity(), shoe_data_screen.class);
                                intent.putExtra("qr_code_data", safeKey);
                                startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
            }
        }
    }


