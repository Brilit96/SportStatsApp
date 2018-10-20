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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextConfirmPassword;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignIn;
    private TextView textViewViablePassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        buttonRegister = findViewById(R.id.buttonRegister);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
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

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();


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
                                    .setDisplayName("Default").build();
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
                password.length() < 7);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRegister:
                registerUser();
                break;

            case R.id.textViewSignIn:
                startActivity(new Intent(this, LogInActivity.class));
                break;
        }
    }
}
