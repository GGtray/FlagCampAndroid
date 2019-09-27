package com.example.flagcamp.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.flagcamp.Home.HomeActivity;
import com.example.flagcamp.R;
import com.example.flagcamp.Utils.BottomNavigationViewHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private static String userId;


    private ImageView profileUserImageView;

    private static final int GALLERY_REQUEST_CODE = 10;
    private static final int REQUEST_IMAGE_CAPTURE = 11;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNavigationView();

        setupFirebaseAuth();

        TextView editProfileTextView = findViewById(R.id.profile_edit_text_view);
        editProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.animy);
            }
        });
        profileUserImageView = findViewById(R.id.profile_user_image_view);
        profileUserImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.activity_profile_bottom_sheet);
                Button uploadButton = bottomSheetDialog.findViewById(R.id.upload_image_button);
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickFromGallery();
                        bottomSheetDialog.dismiss();
                    }
                });
                Button cameraButton = bottomSheetDialog.findViewById(R.id.camera_button);
                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent();
                        bottomSheetDialog.dismiss();
                    }
                });
                Button cancelButton = bottomSheetDialog.findViewById(R.id.cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();
            }
        });
        RelativeLayout careerAdviceRelativeLayout = findViewById(R.id.profile_career_advice);
        careerAdviceRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, CareerAdviceActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout manageAccountRelativeLayout = findViewById(R.id.profile_manage_account);
        manageAccountRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });

        //firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        DatabaseReference ref = mDatabase.child("users").child(userId).child("userinfo");
        DatabaseReference refl = ref.child("location");
        DatabaseReference reft = ref.child("title");

        ValueEventListener postListener = new ValueEventListener() {
            final List<String> userinfo = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("snapchat", "count " + dataSnapshot.getChildrenCount());
                Log.d("snapchat","" + dataSnapshot.getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("friebase qeury", "loadPost:onCancelled", databaseError.toException());
            }

        };
        refl.addValueEventListener(postListener);


    }

    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(ProfileActivity.this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri imageUri = data.getData();
                    profileUserImageView.setImageURI(imageUri);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profileUserImageView.setImageBitmap(imageBitmap);
                    break;
            }
        }
    }

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
                    Log.d("AppliedFragment", "onAuthStateChanged: signed out");
//                    Intent intent = new Intent(t, HomeActivity.class);
//                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
