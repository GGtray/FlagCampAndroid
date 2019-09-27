package com.example.flagcamp.Discover;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flagcamp.Home.HomeActivity;
import com.example.flagcamp.R;
import com.example.flagcamp.Utils.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class ViewJobDetailActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;
    private static String userId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupFirebaseAuth();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_view_job_detail);
        TextView titleTextView = findViewById(R.id.view_details_title);
        final TextView companyTextView = findViewById(R.id.view_details_company);
        TextView postDateTextView = findViewById(R.id.view_details_post_date);
        TextView locationTextView = findViewById(R.id.view_details_location);
        TextView typeTextView = findViewById(R.id.view_details_type);
        ExpandableTextView descriptionTextView = findViewById(R.id.expand_text_view);
        Bundle bundle = getIntent().getExtras();
        final Job job = bundle.getParcelable("job");
        titleTextView.setText(job.getTitle());
        companyTextView.setText(job.getCompany());
        postDateTextView.setText(job.getPostDate());
        locationTextView.setText(job.getLocation());
        typeTextView.setText(job.getJobType());
        descriptionTextView.setText(job.getDescription());
        Button shareButton = findViewById(R.id.view_details_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String subject = "Job opportunity at " + job.getCompany();
                String jobUrl = job.getDetailUrl();
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(android.content.Intent.EXTRA_TEXT, jobUrl);
                Intent chooser = Intent.createChooser(intent, "Share using");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });
        Button applyButton = findViewById(R.id.view_details_apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewJobDetailActivity.this, ApplyActivity.class);
                intent.putExtra("apply_url", job.getApplyUrl());
                startActivity(intent);


                Log.d("button", "button like");
                Log.d("button", userId);

                // TODO:

                Log.d("applybutton", companyTextView.getText().toString());
                DatabaseReference ref = mDatabase.child("users").child(userId).child("history");
                Log.d("button", ref.getKey());
                ref.child(job.getId()).setValue(job);




            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animy, R.anim.slide_out_down);
    }

    //         * Setup Firebase auth object.
//            */
    private void setupFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in.
                } else {
                    // User is signed out.
                    Log.d("DiscoverActivity", "onAuthStateChanged: signed out");
                    Intent intent = new Intent(ViewJobDetailActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        };
    }
}