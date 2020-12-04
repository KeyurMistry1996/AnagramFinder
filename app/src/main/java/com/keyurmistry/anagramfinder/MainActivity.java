package com.keyurmistry.anagramfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        //cheaking for data
        cheakValue();

    }

    private void cheakValue() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // adding data if not available
                if (!snapshot.hasChildren()) {
                    addData();
                } else {
                    System.out.println("data available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addData() {
        String[] data = {null, "acme", "came", "acre", "care", "race", "ales", "leas", "seal"};
        try {
            //creating json array from string array
            JSONArray jsonArray = new JSONArray(data);
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {

                if (!jsonArray.get(i).equals(null)) {
                    map.put(String.valueOf(i), String.valueOf(jsonArray.get(i)));
                }
            }

            //adding data to firebase
            reference.child("test").setValue(map);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}