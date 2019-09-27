package com.example.flagcamp.Home;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.flagcamp.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewPager viewPager = findViewById(R.id.home_view_pager);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragmet(new LoginFragment());
        pagerAdapter.addFragmet(new SignUpFragment());
        viewPager.setAdapter(pagerAdapter);
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentList = new ArrayList<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragmet(Fragment fragment) {
            fragmentList.add(fragment);
        }
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    // May need to override onActivityResult for Google sign in.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}