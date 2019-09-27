package com.example.flagcamp.Discover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.flagcamp.R;

public class SearchActivity extends AppCompatActivity {

    SearchView descriptionSearchView;
    SearchView locationSearchView;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bundle = new Bundle();
        descriptionSearchView = findViewById(R.id.search_description);
        descriptionSearchView.setQueryHint("Enter a job title or keyword");
        locationSearchView = findViewById(R.id.search_location);
        locationSearchView.setQueryHint("Enter a location");
        descriptionSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                bundle.putString("description", s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)) {
                    if (bundle.containsKey("description")) {
                        bundle.remove("description");
                    }
                }
                return false;
            }
        });
        locationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                bundle.putString("location", s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)) {
                    if (bundle.containsKey("location")) {
                        bundle.remove("location");
                    }
                }
                return false;
            }
        });
        ImageView closeImageView = findViewById(R.id.search_menu_close);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.animx, R.anim.slide_out_left);
            }
        });
        ImageView saveImageView = findViewById(R.id.search_menu_save);
        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (!TextUtils.isEmpty(descriptionSearchView.getQuery())) {
                    bundle.putString("description", descriptionSearchView.getQuery().toString());
                }
                if (!TextUtils.isEmpty(locationSearchView.getQuery())) {
                    bundle.putString("location", locationSearchView.getQuery().toString());
                }
                if (bundle.isEmpty()) {
                    setResult(RESULT_CANCELED);
                } else {
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                }
                finish();
                overridePendingTransition(R.anim.animx, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.animx, R.anim.slide_out_left);
    }
}
