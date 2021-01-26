package com.example.shujaassignmenttask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    EditText editTextEmail,editTextPassword1,editTextPassword2, editTextFName, editTextLName;
    Button buttonSignUp;
    private ProgressBar progressBar_Circle;

    //String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword1 = findViewById(R.id.editTextPassword1);
        editTextPassword2 = findViewById(R.id.editTextPassword2);
        editTextFName = findViewById(R.id.editTextFName);
        editTextLName = findViewById(R.id.editTextLName);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        progressBar_Circle = findViewById(R.id.progressBar_Circle);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        buttonSignUp.setOnClickListener(v -> {
            progressBar_Circle.setVisibility(View.VISIBLE);
            final String email = editTextEmail.getText().toString();
            String password1 = editTextPassword1.getText().toString();
            String password2 = editTextPassword2.getText().toString();
            String nameF = editTextFName.getText().toString();
            String nameL = editTextLName.getText().toString();

            if(!(password1.equals(password2))){
                editTextPassword2.setError("incorrect password");
                editTextPassword2.requestFocus();
                return;
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar_Circle.setVisibility(View.INVISIBLE);
                }
            },2000);

            Bundle bundle = new Bundle();
            bundle.putString("nameF", nameF);
            bundle.putString("nameL", nameL);
            bundle.putString("email", email);
            bundle.putString("password", password1);

            Intent i = new Intent(this, VehicleRegistration.class);
            i.putExtra("nameF", nameF);
            i.putExtra("nameL", nameL);
            i.putExtra("email", email);
            i.putExtra("password", password1);
            startActivity(i);
            //startActivity(new Intent(getApplicationContext(),VehicleRegistration.class));
            finish();
        });

    }

    public void goToLogin(View view) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
