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

import com.example.flagcamp.Home.HomeActivity;
import com.example.flagcamp.Utils.Job;
import com.example.flagcamp.Utils.ListViewAdapter;
import com.example.flagcamp.R;
import com.example.flagcamp.Utils.QueryUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedFragment extends Fragment {


    private DatabaseReference mDatabase;
    private ListViewAdapter jobAdapter;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private static String userId;

    final List<Job> jobs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setupFirebaseAuth();
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        ListView jobListView = view.findViewById(R.id.list);

        progressBar = view.findViewById(R.id.progress_bar);

        jobAdapter = new ListViewAdapter(getActivity(), new ArrayList<Job>());

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        DatabaseReference ref = mDatabase.child("users").child(userId).child("favorite");

        ValueEventListener postListener = new ValueEventListener() {
            final List<Job> jobs = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("snapchat", "count " + dataSnapshot.getChildrenCount());
                for (DataSnapshot favoriteSnap : dataSnapshot.getChildren()) {
                    Job job = favoriteSnap.getValue(Job.class);

//                    Map<String, String> map= (HashMap)favoriteSnap.getValue();
////                    Log.d("aaaaaaa", map.get("company_logo"));

                    jobs.add(job);
//                    Log.d("Read", jobs.size()+"");
                }
                jobAdapter.addAll(jobs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("friebase qeury", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);

        progressBar.setVisibility(View.GONE);


        jobListView.setAdapter(jobAdapter);
        jobAdapter.clear();

        jobAdapter.addAll(jobs);
        jobListView.setEmptyView(view.findViewById(R.id.empty));



//        jobListView.setAdapter(jobAdapter);
//        SavedFragment.JobAsyncTask jobAsyncTask = new SavedFragment.JobAsyncTask();
//
//        jobAsyncTask.execute("https://jobs.github.com/positions.json?");

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

//    private class JobAsyncTask extends AsyncTask<String, Void, List<Job>> {
//        @Override
//        protected void onPostExecute(List<Job> jobs) {
//            progressBar.setVisibility(View.GONE);
//            jobAdapter.clear();
//            if (jobs != null && !jobs.isEmpty()) {
//                jobAdapter.addAll(jobs);
//            }
//
//        }
//
//        @Override
//        protected List<Job> doInBackground(String... strings) {
//            if (strings.length < 1 || strings[0] == null) {
//                return null;
//            }
//            List<Job> jobs = QueryUtils.extractJobs(strings[0]);
//            return jobs;
//        }
//    }
}
