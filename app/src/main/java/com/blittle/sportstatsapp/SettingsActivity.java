package com.blittle.sportstatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button buttonLogout;
    private TextView textViewUserName;
    private TextView textViewVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        }

        //Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewUserName = findViewById(R.id.textViewUserName);
        textViewVerified = findViewById(R.id.textViewVerified);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        loadUserInformation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LogInActivity.class));
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user.getDisplayName() != null) {
            textViewUserName.setText("Welcome " + user.getDisplayName());
        }

        //Checks if user's email is verified, if not, send verification email.
        if(user.isEmailVerified()){
            textViewVerified.setText(R.string.emailVerified);
        } else {
            textViewVerified.setText(R.string.emailNotVerified);
            textViewVerified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SettingsActivity.this, "Email verification sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemHome:
                if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                    finish();
                    startActivity(new Intent(this, LogInActivity.class));
                } else {
                    Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LogInActivity.class));
        }
    }
}
