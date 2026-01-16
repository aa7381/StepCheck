package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refBase2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class InventoryFragment extends Fragment {
    private Button AddNewShoe ,btnScanQr;

    private String qr_code_data = "";

    private ActivityResultLauncher<ScanOptions> barLauncher;

    private String safeKey ;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        AddNewShoe = view.findViewById(R.id.AddNewShoe);
        btnScanQr = view.findViewById(R.id.btnScanQr);



        if (AddNewShoe != null) {
            AddNewShoe.setOnClickListener(v -> {
            addNewShoe();
            });
        }

        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result != null && result.getContents() != null) {
                qr_code_data = result.getContents();
                safeKey = Base64.encodeToString(qr_code_data.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);

                go_to_shoe_information();

            }
        });

        if (btnScanQr!= null) {
            btnScanQr.setOnClickListener(v -> {
                scanCode();

            });
        }


    }
    private void  go_to_shoe_information()
    {
        try {

            refBase2.child(safeKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        Intent intent = new Intent(requireActivity(), shoe_data_screen.class);
                        intent.putExtra("qr_code_data", safeKey);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                    else
                        Toast.makeText(requireActivity(), "This shoe does not exist", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        } catch (NumberFormatException e) {
        }
    }
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }


    public void addNewShoe() {
        Intent intent = new Intent(requireActivity(), add_new_shoe.class);
        startActivity(intent);
        requireActivity().finish();
    }
}