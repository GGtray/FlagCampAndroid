package com.example.flagcamp.Likes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flagcamp.Discover.DiscoverActivity;
import com.example.flagcamp.Home.HomeActivity;
import com.example.flagcamp.Utils.Job;
import com.example.flagcamp.Utils.ListViewAdapter;
import com.example.flagcamp.R;
import com.example.flagcamp.Utils.QueryUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppliedFragment extends Fragment {



    private DatabaseReference mDatabase;
    private  ListViewAdapter jobAdapter;
    private  ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private static String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setupFirebaseAuth();

        View view = inflater.inflate(R.layout.fragment_applied, container, false);

        progressBar = view.findViewById(R.id.progress_bar);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();



//        DatabaseReference ref = mDatabase.child("user").child(userId).child("userinfo");
//
//       ValueEventListener postListener = new ValueEventListener() {
//           @Override
//           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("count", "count " + dataSnapshot.getChildrenCount());
//               Log.d("lister", dataSnapshot.getKey());
//               Log.d("userinfp", "info " + dataSnapshot.getValue());
//           }
//
//           @Override
//           public void onCancelled(@NonNull DatabaseError databaseError) {
//
//           }
//       };
//       ref.addValueEventListener(postListener);

        DatabaseReference ref = mDatabase.child("users").child(userId).child("history");

        ValueEventListener postListener = new ValueEventListener() {
            final List<Job> jobs = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("snapchat", "count " + dataSnapshot.getChildrenCount());
                for (DataSnapshot favoriteSnap : dataSnapshot.getChildren()) {
                    Job job = favoriteSnap.getValue(Job.class);

//                   Map<String, String> map= (HashMap)favoriteSnap.getValue();
//                   Log.d("aaaaaaa", map.get("company_logo"));

                    jobs.add(job);
                    Log.d("size", jobs.size()+"");
                    Log.d("Read", job.getCompany());
                }
                jobAdapter.addAll(jobs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("friebase qeury", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);




        ListView jobListView = view.findViewById(R.id.list);
        jobAdapter = new ListViewAdapter(getActivity(), new ArrayList<Job>());

        progressBar.setVisibility(View.GONE);
//        jobAdapter.clear();

        if (jobAdapter == null) {
            Log.d("sattt", "jobAdapter is null");
        } else {
            Log.d("sattt", "jobAdapter is  not null");
        }
//        if (jobs.size() == 0) {
//            Log.d("sattt", "jobs is null");
//        } else {
//            Log.d("sattt", "jobs is not null");
//        }


//        jobAdapter.addAll(jobs);
        jobListView.setAdapter(jobAdapter);
        jobListView.setEmptyView(view.findViewById(R.id.empty));

        return view;
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
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
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
