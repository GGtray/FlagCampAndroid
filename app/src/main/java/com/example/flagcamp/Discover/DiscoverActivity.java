package com.example.flagcamp.Discover;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.flagcamp.Home.HomeActivity;
import com.example.flagcamp.Utils.Job;
import com.example.flagcamp.Utils.JobAdapter;
import com.example.flagcamp.R;
import com.example.flagcamp.Utils.BottomNavigationViewHelper;
import com.example.flagcamp.Utils.QueryUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class DiscoverActivity extends AppCompatActivity implements CardStackListener, EasyPermissions.PermissionCallbacks, JobAdapter.OnJobClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;
    private static String userId;

    private static JobAdapter jobAdapter;
    private static ProgressBar progressBar;

    RelativeLayout recyclerView;
    private LinearLayout emptyView;
    private CardStackView cardStackView;
    private CardStackLayoutManager cardStackLayoutManager;

    private final int REQUEST_LOCATION_PERMISSION = 1;
    private final int SEARCH_ACTIVITY_REQUEST = 2;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        setupFirebaseAuth();
        setupBottomNavigationView();
        setupButton();

        recyclerView = findViewById(R.id.recycler_view);
        emptyView = findViewById(R.id.empty_view);
        emptyView.setVisibility(View.GONE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        progressBar = findViewById(R.id.progress_bar);
        cardStackView = findViewById(R.id.card_stack_view);
        cardStackLayoutManager = new CardStackLayoutManager(DiscoverActivity.this, this);
        cardStackLayoutManager.setCanScrollVertical(false);
        cardStackLayoutManager.setVisibleCount(2);
        cardStackLayoutManager.setMaxDegree(40.0f);
        cardStackLayoutManager.setSwipeThreshold(0.3f);
        cardStackView.setLayoutManager(cardStackLayoutManager);
        jobAdapter = new JobAdapter(new ArrayList<Job>(), this);
        cardStackView.setAdapter(jobAdapter);
        ImageView searchImageView = findViewById(R.id.discover_search_image_view);
        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscoverActivity.this, SearchActivity.class);
                startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST);
                overridePendingTransition(R.anim.slide_in_right, R.anim.animx);
            }
        });
        requireLocationPermission();
    }

    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(DiscoverActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    /*
     * Setup Firebase auth object.
     */
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
                    Intent intent = new Intent(DiscoverActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private class JobAsyncTask extends AsyncTask<String, Void, List<Job>> {
        @Override
        protected void onPostExecute(List<Job> jobs) {
            progressBar.setVisibility(View.GONE);
            jobAdapter.clear();
            if (jobs != null && !jobs.isEmpty()) {
                jobAdapter.addAll(jobs);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Job> doInBackground(String... strings) {
            if (strings.length < 1 || strings[0] == null) {
                return null;
            }
            List<Job> jobs = QueryUtils.extractJobs(strings[0]);
            return jobs;
        }
    }

    private void setupButton() {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        FloatingActionButton skipButton = findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwipeAnimationSetting swipeSetting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(Duration.Slow.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                cardStackLayoutManager.setSwipeAnimationSetting(swipeSetting);
                cardStackView.swipe();
            }
        });

        FloatingActionButton rewindButton = findViewById(R.id.rewind_button);
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setEnabled(false);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, 1050);
                RewindAnimationSetting rewindSetting = new RewindAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(Duration.Slow.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                cardStackLayoutManager.setRewindAnimationSetting(rewindSetting);
                cardStackView.rewind();
            }
        });

        FloatingActionButton likeButton = findViewById(R.id.like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                SwipeAnimationSetting swipeSetting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(Duration.Slow.duration)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                Log.d("Discover", jobAdapter.getJobs().get(cardStackLayoutManager.getTopPosition()).getCompany());
                cardStackLayoutManager.setSwipeAnimationSetting(swipeSetting);
                cardStackView.swipe();

                Log.d("button", "button like");
                Log.d("button", userId);

                // TODO:

                Log.d("button", jobAdapter.getJobs().get(cardStackView.getTop()).getCompany());
                DatabaseReference ref = mDatabase.child("users").child(userId).child("favorite");
                String key = ref.child("posts").push().getKey();
                Log.d("button", key);
                Log.d("button", jobAdapter.getJobs().get(cardStackView.getTop()).getId());
                Log.d("button1", jobAdapter.getJobs().get(cardStackLayoutManager.getTopPosition()).getId());
                ref.child(jobAdapter.getJobs().get(cardStackLayoutManager.getTopPosition()).getId()).setValue(jobAdapter.getJobs().get(cardStackLayoutManager.getTopPosition()));


            }
        });
    }

    @Override
    // Exit the app on back-press.
    public void onBackPressed() {
        finishAffinity();
//        moveTaskToBack(true);
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Right) {
            // TODO
            Log.d("Swiped", "Like");
        }
        if (direction == Direction.Left) {
            Log.d("Swiped", "Dislike");
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requireLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            getDeviceLocation();
        } else {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, REQUEST_LOCATION_PERMISSION, permissions)
                    .setRationale(R.string.rationale)
                    .setNegativeButtonText("I'M SURE")
                    .setPositiveButtonText("RE-TRY")
                    .build());
        }
    }

    private void getDeviceLocation() {
        locationListener = new JobLocationListener();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        getDeviceLocation();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        JobAsyncTask jobAsyncTask = new JobAsyncTask();
        jobAsyncTask.execute("https://jobs.github.com/positions.json?location=new+york");
    }

    private class JobLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            JobAsyncTask jobAsyncTask = new JobAsyncTask();
            String uri = Uri.parse("https://jobs.github.com/positions.json?")
                    .buildUpon()
                    .appendQueryParameter("lat", Double.toString(latitude))
                    .appendQueryParameter("long", Double.toString(longitude))
                    .build().toString();
            jobAsyncTask.execute(uri);
            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
//                    Toast.makeText(DiscoverActivity.this, "City: " + addresses.get(0).getLocality(), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    @Override
    public void onJobClick(int position) {
        Log.d("DiscoverActivity", "Clicked");
        Intent intent = new Intent(DiscoverActivity.this, ViewJobDetailActivity.class);
        intent.putExtra("job", jobAdapter.getJobs().get(position));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.animy);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String description = bundle.containsKey("description") ? bundle.getString("description") : null;
                String location = bundle.containsKey("location") ? bundle.getString("location") : null;
                JobAsyncTask jobAsyncTask = new JobAsyncTask();
                Uri.Builder uri = Uri.parse("https://jobs.github.com/positions.json?")
                        .buildUpon();
                if (description != null) {
                    uri.appendQueryParameter("description", description);
                }
                if (location != null) {
                    uri.appendQueryParameter("location", location);
                }
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                jobAsyncTask.execute(uri.toString());
            }
        }
    }
}