package com.example.flagcamp.Likes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.flagcamp.R;
import com.example.flagcamp.Utils.BottomNavigationViewHelper;
import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        setupBottomNavigationView();

        final ViewPager viewPager = findViewById(R.id.likes_view_pager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new AppliedFragment(), "Applied");
        tabAdapter.addFragment(new SavedFragment(), "Saved Jobs");
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_star);
    }

    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(LikesActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }

    private class TabAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
