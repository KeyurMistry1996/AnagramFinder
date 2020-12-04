package com.keyurmistry.anagramfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextInputEditText input;
    Button button;
    TextView result;
    ArrayList<String> resultList;
    String resultString = "";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    boolean isDataAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        button = findViewById(R.id.button);
        result = findViewById(R.id.result);

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        //cheaking for data
        cheakValue();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDataAvailable) {
                    if (!input.getText().toString().isEmpty()) {
                        resultString = "";
                        checkAngram(input.getText().toString());
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter text", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    //checking angram text
    private void checkAngram(String string) {
        char[] input = string.toLowerCase().toCharArray();

        reference.child("test").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                resultList = new ArrayList<>();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {

                    char[] valuetoSort = datasnapshot.getValue().toString().toLowerCase().toCharArray();
                    char[] value = datasnapshot.getValue().toString().toLowerCase().toCharArray();

                    if (input.length == value.length) {

                        Arrays.sort(input);
                        Arrays.sort(valuetoSort);

                        if (Arrays.equals(input, valuetoSort)) {

                            resultList.add(value.toString());
                            resultString += new String(value) + "\n";

                        }
                    }
                }

                if (resultList.size() > 0) {
                    resultString = "Congratulation found " + resultList.size() + " match \n" + resultString;
                    result.setText(resultString);
                    result.setTextColor(getResources().getColor(R.color.green));
                } else {
                    result.setText("Sorry,nothing found");
                    result.setTextColor(getResources().getColor(R.color.red));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cheakValue() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // adding data if not available
                if (!snapshot.hasChildren()) {
                    addData();
                } else {
                    isDataAvailable = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                System.out.println(error.getMessage());
            }
        });
    }


    //adding value to firebase

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
            cheakValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}