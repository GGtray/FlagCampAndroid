package com.example.flagcamp.Profile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flagcamp.R;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

public class EditProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ImageView closeImageView = findViewById(R.id.profile_menu_close);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.animy, R.anim.slide_out_down);
            }
        });
        ImageView submit = findViewById(R.id.profile_menu_save);
        submit.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText title = findViewById(R.id.editText4);


                finish();
                overridePendingTransition(R.anim.animy, R.anim.slide_out_down);

            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animy, R.anim.slide_out_down);
    }
}
