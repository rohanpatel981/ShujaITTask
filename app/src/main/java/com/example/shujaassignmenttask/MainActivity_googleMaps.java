package com.example.shujaassignmenttask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainActivity_googleMaps extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    Spinner spinner_locate;
    //Button buttonLocate;
    GoogleMap map;

    double currentLat = 0, currentLong = 0;
    String[] placeTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_google_maps);

        spinner_locate = findViewById(R.id.spinner_locate);
        //buttonLocate = findViewById(R.id.buttonLocate);

        placeTypeList = new String[]{"atm", "bank", "petrol_pump", "movie_theater", "restaurant"};
        String[] placeNameList = {"ATM", "Bank", "Petrol Pump", "Movie Theater", "Restaurant"};

        spinner_locate.setAdapter(new ArrayAdapter<>(MainActivity_googleMaps.this,
                android.R.layout.simple_spinner_dropdown_item, placeNameList));

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MainActivity_googleMaps.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        } /*
        buttonLocate.setOnClickListener(v -> {
            //Toast.makeText(getApplicationContext(), String.valueOf(currentLat),Toast.LENGTH_LONG);
            //startActivity(new Intent(getApplicationContext(), AddRefuel.class));
            int i = spinner_locate.getSelectedItemPosition();
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"+ "?location="+
                    ","+currentLat+","+currentLong+"&radius=5000"+"&types="+placeTypeList[i]+"&sensor=true"+
                    "key="+getResources().getString(R.string.google_map_key);

            new PlaceTask().execute(url);
        }); */
    }
    public void goToLocate(View view) {

        int i = spinner_locate.getSelectedItemPosition();
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"+ "?location="+
                ","+currentLat+","+currentLong+"&radius=5000"+"&types="+placeTypeList[i]+"&sensor=true"+
                "key="+getResources().getString(R.string.google_map_key);

        new PlaceTask().execute(url);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null){
                currentLat = location.getLatitude();
                currentLong = location.getLongitude();
                supportMapFragment.getMapAsync(googleMap -> {
                    map = googleMap;
                    //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //MarkerOptions options = new MarkerOptions().position(latLng).title("I am here");
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLong),10));
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }



    private class PlaceTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null ){
            builder.append(line);
        }
        String data = builder.toString();
        reader.close();
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> mapList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            map.clear();
            for (int i = 0; i < hashMaps.size(); ++i){
                HashMap<String, String> hashMapList = hashMaps.get(i);
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                map.addMarker(options);
            }
        }
    }
}