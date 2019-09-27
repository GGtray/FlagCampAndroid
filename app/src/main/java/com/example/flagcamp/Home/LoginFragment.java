package com.example.flagcamp.Home;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flagcamp.Discover.DiscoverActivity;
import com.example.flagcamp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView hintTextView;
    private GoogleSignInButton googleSignInButton;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setupFirebaseAuth();
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_login, container, false);
        emailEditText = layout.findViewById(R.id.login_email_edit_text);
        passwordEditText = layout.findViewById(R.id.login_password_edit_text);
        loginButton = layout.findViewById(R.id.login_button);
        hintTextView = layout.findViewById(R.id.login_hint_text_view);
        hintTextView.setVisibility(View.GONE);
        googleSignInButton = layout.findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("10013657951-osog96s4gqv7bgot57dt7d254b7cntiq.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = layout.findViewById(R.id.facebook_login_button);
        facebookLoginButton.setPermissions("email", "public_profile");
        facebookLoginButton.setFragment(this);
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginFragment", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("LoginFragment", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("LoginFragment", "facebook:onError", error);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintTextView.setVisibility(View.VISIBLE);
                Log.d("LoginFragment", "Attempting to log in");
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (validateInput(email) && validateInput(password)) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hintTextView.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                            } else {
//                                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "All fields need to be non-empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return layout;
    }

    private boolean validateInput(String s) {
        return !TextUtils.isEmpty(s);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginFragment", "Google sign in failed", e);
                // ...
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginFragment", "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginFragment", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    // The facebook account can either set their primary contact as email address or Mobile number.
    // If mobile number is marked as primary contact, the email address cannot be retrieved.
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginFragment", "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginFragment", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
