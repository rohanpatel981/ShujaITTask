package com.example.shujaassignmenttask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeNavigate extends AppCompatActivity {

    FirebaseAuth fAuth;
    //String userID;
    //Button buttonLogout;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_navigate);

        fAuth = FirebaseAuth.getInstance();
        //userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new RefuelingFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {

                Fragment selectedFragment = null;

                switch (item.getItemId()){
                    case R.id.nav_history:
                        selectedFragment = new HistoryFragment();
                        break;
                    case R.id.nav_report:
                        selectedFragment = new ReportsFragment();
                        break;
                    case R.id.nav_refueling:
                        selectedFragment = new RefuelingFragment();
                        break;
                    case R.id.nav_reminder:
                        selectedFragment = new ReminderFragment();
                        break;
                    case R.id.nav_more:
                        selectedFragment = new MoreFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();

                return true;
            };
}