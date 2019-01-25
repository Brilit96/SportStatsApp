package com.blittle.sportstatsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSearchSummoner;
    private EditText editTextSearchSummoner;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //If user is not logged in, send them to SignUpActivity
        //If user is logged in but not email verified, send them to SettingsActivity
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        } else if(!firebaseAuth.getCurrentUser().isEmailVerified()) {
            finish();
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }

        buttonSearchSummoner = findViewById(R.id.buttonSearchSummoner);
        editTextSearchSummoner = findViewById(R.id.editTextSearchSummoner);

        buttonSearchSummoner.setOnClickListener(this);

        //Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemLogout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LogInActivity.class));
                return true;
            case R.id.itemSettings:
                finish();
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSearchSummoner) {
            String sum = editTextSearchSummoner.getText().toString().trim();
            searchSummoner(sum);
        }
    }

    //Search summoner by name
    private void searchSummoner(String summoner) {
        //Retrieve Summoner Info
        RiotApiController apiController = new RiotApiController();
        try {
            String summonerID = apiController.getSummonerID(summoner);
            String accountID = apiController.getAccountID(summoner);
            String match = apiController.getMatchLists(accountID);
            Log.d("JSON: ", summonerID);
            Log.d("JSON: ", accountID);
            Log.d("JSON: ", match);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
