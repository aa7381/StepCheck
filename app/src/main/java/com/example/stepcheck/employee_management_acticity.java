package com.example.stepcheck;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class employee_management_acticity extends Fragment {

    private ListView lvEmployees;
    private Button btnRefresh;
    private ArrayAdapter<String> adapter;
    private List<String> employeeNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_management_acticity, container, false);

        lvEmployees = view.findViewById(R.id.lvEmployees);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        employeeNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, employeeNames);
        lvEmployees.setAdapter(adapter);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEmployeesInShift();
            }
        });

        loadEmployeesInShift();

        return view;
    }

    private void loadEmployeesInShift() {
        employeeNames.clear();
        adapter.notifyDataSetChanged();
        
        // עוברים ישירות על רשימת העובדים ובודקים את השדה inShift
        FBRef.refBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot workerSnapshot : snapshot.getChildren()) {
                    Worker worker = workerSnapshot.getValue(Worker.class);
                    
                    // בדיקה אם העובד בתוך משמרת (inShift == true)
                    if (worker != null && worker.getInShift()) {
                        if (worker.getUsername() != null) {
                            employeeNames.add(worker.getUsername());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                
                if (employeeNames.isEmpty()) {
                    Toast.makeText(getContext(), "אין עובדים במשמרת כרגע", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
