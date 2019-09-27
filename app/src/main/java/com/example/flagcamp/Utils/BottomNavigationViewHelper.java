package com.example.flagcamp.Utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.flagcamp.Discover.DiscoverActivity;
import com.example.flagcamp.Likes.LikesActivity;
import com.example.flagcamp.Profile.ProfileActivity;
import com.example.flagcamp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {
    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ic_discover:
                        Intent discover = new Intent(context, DiscoverActivity.class);
                        discover.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(discover);
                        break;
                    case R.id.ic_alert:
                        Intent alert = new Intent(context, LikesActivity.class);
                        alert.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(alert);
                        break;
                    case R.id.ic_profile:
                        Intent profile = new Intent(context, ProfileActivity.class);
                        profile.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(profile);
                        break;
                }
                return false;
            }
        });
    }
}
