package com.example.lokata.Driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.lokata.Fragment.HomeFragment;
import com.example.lokata.Fragment.NotificationFragment;
import com.example.lokata.Fragment.ProfileFragment;
import com.example.lokata.Fragment.SettingFragment;
import com.example.lokata.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DriverActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    SettingFragment settingFragment = new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(8);

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
                } else if (itemId == R.id.profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                    return true;
                } else if (itemId == R.id.setting) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, settingFragment).commit();
                    return true;
                }

                return false;
            }
        });

    }
}