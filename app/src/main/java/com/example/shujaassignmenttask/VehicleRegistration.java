package com.example.shujaassignmenttask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VehicleRegistration extends AppCompatActivity {

    Spinner spinner_vehicle, spinner_manufacturer;
    EditText editTextModel, editTextFuelCapacity;
    Button buttonVehicleRegister;

    String vehicle, manufacturer, userID;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    private ProgressBar progressBar_Circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_registration);

        spinner_vehicle = findViewById(R.id.spinner_vehicle);
        spinner_manufacturer = findViewById(R.id.spinner_manufacturer);
        editTextModel = findViewById(R.id.editTextModel);
        editTextFuelCapacity = findViewById(R.id.editTextFuelCapacity);
        buttonVehicleRegister = findViewById(R.id.buttonVehicleRegister);
        progressBar_Circle = findViewById(R.id.progressBar_Circle);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        spinner_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicle = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_manufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manufacturer = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonVehicleRegister.setOnClickListener(v -> {
            progressBar_Circle.setVisibility(View.VISIBLE);
            String model = editTextModel.getText().toString();
            String fuelCapacity = editTextFuelCapacity.getText().toString();

            //Bundle bundle = new Bundle();
            String nameF = getIntent().getExtras().getString("nameF");
            String nameL = getIntent().getExtras().getString("nameL");
            String email = getIntent().getExtras().getString("email");
            String password1 = getIntent().getExtras().getString("password");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar_Circle.setVisibility(View.INVISIBLE);
                }
            },2000);

            fAuth.createUserWithEmailAndPassword(email,password1).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(VehicleRegistration.this, "User Created", Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference reference = fStore.collection("userDetails").document(userID);
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("first_name", nameF);
                    userInfo.put("last_name", nameL);
                    userInfo.put("email", email);
                    userInfo.put("vehicle", vehicle);
                    userInfo.put("manufacturer", manufacturer);
                    userInfo.put("model", model);
                    userInfo.put("fuelCapacity", fuelCapacity);
                    reference.set(userInfo).addOnSuccessListener(aVoid -> Log.d("TAG", "On success: User Profile is created for " + userID));

                    startActivity(new Intent(getApplicationContext(), HomeNavigate.class));
                    finish();
                }
                else
                    Toast.makeText(VehicleRegistration.this,task.getException().getMessage(), Toast.LENGTH_SHORT);
            });
        });

    }
}