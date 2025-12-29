package com.example.stepcheck;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class InventoryFragment extends Fragment {
    private Button AddNewShoe;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        AddNewShoe = view.findViewById(R.id.AddNewShoe);

        if (AddNewShoe != null) {
            AddNewShoe.setOnClickListener(v -> {
            addNewShoe();
            });
        }

    }

    public void addNewShoe() {
        Intent intent = new Intent(requireActivity(), add_new_shoe.class);
        startActivity(intent);
        requireActivity().finish();
    }
}