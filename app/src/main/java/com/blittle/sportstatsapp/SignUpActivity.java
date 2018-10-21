package com.blittle.sportstatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextConfirmPassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private TextView textViewSignIn;
    private TextView textViewViablePassword;
    private boolean usernameAvailable;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameAvailable = false;
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        buttonRegister = findViewById(R.id.buttonRegister);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        textViewViablePassword = findViewById(R.id.textViewViablePassword);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkPasswordStrength(textViewViablePassword)) {
                    textViewViablePassword.setText("Password is strong enough");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkPasswordStrength(textViewViablePassword)) {
                    textViewViablePassword.setText("Password is strong enough");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    //Method to handle user registration, should be called in signUp() method
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Register User
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //Send verification email
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(SignUpActivity.this, "Email verification sent", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //Set Display Name for User
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editTextUsername.getText().toString().trim())
                                    .build();

                            String username = editTextUsername.getText().toString().trim();
                            String username_insensitive = username.toLowerCase().replaceAll("\\s","");
                            //Input userInfo into Cloud Firestore database
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("email", user.getEmail());
                            userInfo.put("username", username);
                            userInfo.put("username_insensitive", username_insensitive);

                            db.collection("users").document(user.getUid()).set(userInfo);

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Could not set username.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //Returns a boolean of whether or not the password is strong enough and writes feedback in the given TextView
    private boolean checkPasswordStrength(TextView textViewPasswordFeedback) {
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        int passwordStrength = 0;

        //Check if passwords are the same, contains a number, uppercase character, and is longer than 8 characters.
        textViewPasswordFeedback.setText("");

        if(!password.equals(confirmPassword)) {
            textViewPasswordFeedback.setText("Passwords MUST match\n");
            return false;
        }
        if(!password.matches("(.*)[0-9](.*)")) {
            textViewPasswordFeedback.append("Password MUST contain a number\n");
        } else {
            passwordStrength++;
        }
        if(!password.matches("(.*)[A-Z](.*)")) {
            textViewPasswordFeedback.append("Password MUST contain an uppercase character\n");
        } else {
            passwordStrength++;
        }
        if(password.length() < 7) {
            textViewPasswordFeedback.append("Password MUST be at least 8 characters\n");
        } else {
            passwordStrength++;
        }

        //Only return true if all requirements are met
        return (passwordStrength >= 3);
    }

    //Returns a boolean of whether or not the password is strong enough
    private boolean checkPasswordStrength() {
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        return(password.equals(confirmPassword) &&
                password.matches("(.*)[0-9](.*)") &&
                password.matches("(.*)[A-Z](.*)") &&
                password.length() > 7);
    }

    //Check database to see if username is available
    private void signUp() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        } else if(TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        } else if(TextUtils.isEmpty(confirmPassword)) {
            //confirm password is empty
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        } else if(!checkPasswordStrength()) {
            Toast.makeText(this, "Passwords do not match, or is not strong enough", Toast.LENGTH_SHORT).show();
            return;
        }

        //Make username all lowercase and remove white space, this will have it's own field in the database named "username_insensitive"
        String username_insensitive = username.toLowerCase().replaceAll("\\s","");

        //Check if username is available, then call registerUser() if it is
        db.collection("users").whereEqualTo("username_insensitive", username_insensitive).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Username is available
                            if(task.getResult().isEmpty()) {
                                //Register user
                                registerUser();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Username is not available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Something went wrong, try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRegister:
                signUp();
                break;

            case R.id.textViewSignIn:
                startActivity(new Intent(this, LogInActivity.class));
                break;
        }
    }
}
