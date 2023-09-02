package com.example.lokata.Driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.lokata.Fragment.HomeFragment;
import com.example.lokata.Fragment.MenuFragment;
import com.example.lokata.Fragment.NotificationFragment;
import com.example.lokata.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DriverActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    MenuFragment menuFragment = new MenuFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(8);

        Intent i = getIntent();
        String userLicenseID = i.getStringExtra("licenseIDGet");
        if (userLicenseID == null || userLicenseID.isEmpty()) {
            Log.d("USER LICENSE ID ", "IS NULL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return;
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                } else if (itemId == R.id.notification) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                    return true;
                } else if (itemId == R.id.menu) {
                    // Pass userLicenseID to the ProfileFragment
                    Bundle args = new Bundle();
                    args.putString("licenseIDGet", userLicenseID);
                    menuFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction().replace(R.id.container, menuFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }
}