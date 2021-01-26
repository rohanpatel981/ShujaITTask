package com.example.shujaassignmenttask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddRefuel extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    String incoming_message = "", incoming_message_phone_number = "";

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID = fAuth.getCurrentUser().getUid();

    Button buttonAddFuel;
    TextView textViewDate, textViewTime;
    String date, month, year, amPm;
    TimePickerDialog timePickerDialog;
    int currentHour, currentMinute;
    Calendar calendar;

    EditText editTextOdometer, EditTextPricePerKwh, EditTextTotalCost, EditTextGallons, editTextGasStation;
    String odometerReading, fuelTypeSpinner, priceGal, totalCost, gallons, gasStation, curDate, curTime;
    Spinner spinner_fuel;

    MyReceiver receiver = new MyReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            incoming_message = msg;
            incoming_message_phone_number = phoneNo;
            parseMessageInput();
        }
    };

    private void parseMessageInput() {
        EditTextTotalCost.setText(computeRegex(getResources().getString(R.string.regex_sms_Amount)));
        editTextGasStation.setText(computeRegex(getResources().getString(R.string.regex_sms_Place)));
        String x = computeRegex(getResources().getString(R.string.regex_sms_Place));
        Toast.makeText(AddRefuel.this, incoming_message,Toast.LENGTH_LONG)
                .show();
    }

    private String computeRegex(String expression) {
        String ans = "Not found";
        Pattern p = Pattern.compile(expression);
        Matcher m = p.matcher(incoming_message);
        while (m.find()){
            ans = m.group();
            break;
        }
        return ans;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerReceiver(receiver, new IntentFilter(SMS_RECEIVED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_refuel);

        buttonAddFuel = findViewById(R.id.buttonAddFuel);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        editTextOdometer = findViewById(R.id.editTextOdometer);
        EditTextPricePerKwh = findViewById(R.id.EditTextPricePerKwh);
        EditTextTotalCost = findViewById(R.id.EditTextTotalCost);
        EditTextGallons = findViewById(R.id.EditTextGallons);
        editTextGasStation = findViewById(R.id.editTextGasStation);
        spinner_fuel = findViewById(R.id.spinner_fuel);


        spinner_fuel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fuelTypeSpinner = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //checking for SMS permission...
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)){

            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }


        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        textViewDate.setText(dateTimeFormatter.format(now));


        DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now1 = LocalDateTime.now();
        textViewTime.setText(dateTimeFormatter1.format(now1));

        textViewDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        textViewTime.setOnClickListener(v -> {
            showTimePickerDialog();
        });

        buttonAddFuel.setOnClickListener(v -> {
            AddToFirebase();
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void AddToFirebase() {
        curDate = year+"-"+month+"-"+date;
        curTime = currentHour+":"+currentMinute;
        odometerReading = editTextOdometer.getText().toString();
        priceGal = EditTextPricePerKwh.getText().toString();
        totalCost = EditTextTotalCost.getText().toString();
        gallons = EditTextGallons.getText().toString();
        gasStation = editTextGasStation.getText().toString();



        DocumentReference documentReference = fStore.collection("Refuel_Data").document(userID);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("Date", curDate);
        userInfo.put("Time", curTime);
        userInfo.put("Odometer", odometerReading);
        userInfo.put("FuelType", fuelTypeSpinner);
        userInfo.put("Price/Gal", priceGal);
        userInfo.put("TotalCost", totalCost);
        userInfo.put("Gallons", gallons);
        userInfo.put("GasStation", gasStation);
        documentReference.set(userInfo).addOnSuccessListener(aVoid -> Log.d("TAG", "On success: Data added successfully for " + userID));
        Toast.makeText(AddRefuel.this, "Refueling data added!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), HomeNavigate.class));
        finish();
    }

    private void showTimePickerDialog() {
        calendar = Calendar.getInstance();
        currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        currentMinute = Calendar.getInstance().get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(AddRefuel.this,
                (view, hourOfDay, minute) -> {
                    if(hourOfDay >= 12){
                        amPm = "PM";
                    }
                    else{
                        amPm = "AM";
                    }
                }, currentHour, currentMinute, false);
        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int yeaR, int montH, int dayOfMonth) {
        date = Integer.toString(dayOfMonth);
        month = Integer.toString(montH);
        year = Integer.toString(yeaR);
    }

    public void goToMap(View view) {
        startActivity(new Intent(this, MainActivity_googleMaps.class));
    }

}