package com.example.flagcamp.Home;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flagcamp.Discover.DiscoverActivity;
import com.example.flagcamp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText rePasswordEditText;
    private Button registerButton;
    private TextView hintTextView;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setupFirebaseAuth();
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_sign_up, container, false);
        nameEditText = layout.findViewById(R.id.name_edit_text);
        emailEditText = layout.findViewById(R.id.register_email_edit_text);
        passwordEditText = layout.findViewById(R.id.register_password_edit_text);
        rePasswordEditText = layout.findViewById(R.id.register_repassword_edit_text);
        registerButton = layout.findViewById(R.id.register_button);
        hintTextView = layout.findViewById(R.id.register_hint_text_view);
        hintTextView.setVisibility(View.GONE);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintTextView.setVisibility(View.VISIBLE);
                String email = emailEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String rePassword = rePasswordEditText.getText().toString();
                if (validateInput(email) && validateInput(name) && validateInput(password) && validateInput(rePassword)) {
                    if (password.equals(rePassword)) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                hintTextView.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    // The user is automatically signed in after the new account was created.
                                    firebaseAuth.signOut();
                                    Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "All fields need to be non-empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return layout;
    }

    private void setupFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in.
                    Intent intent = new Intent(getActivity(), DiscoverActivity.class);
                    startActivity(intent);
                    // Finish the HomeActivity. Only go back to HomeActivity when user signs out.
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                } else {
                    // User is signed out.
                }
            }
        };
    }

    private boolean validateInput(String s) {
        return !TextUtils.isEmpty(s);
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
