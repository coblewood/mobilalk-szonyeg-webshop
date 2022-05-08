package com.example.szonyegwebshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity{
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 42;

    private FirebaseAuth user_auth;

    EditText LoginUsername;
    EditText LoginPassword;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginUsername = findViewById(R.id.LoginEmail);
        LoginPassword = findViewById(R.id.LoginPassword);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        user_auth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        String username = LoginUsername.getText().toString();
        String password = LoginPassword.getText().toString();

        user_auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "User loged in successfully");
                openShop();
            } else {
                Log.d(LOG_TAG, "User log in failed");
                Toast.makeText(MainActivity.this, "User log in failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openShop(){
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
    }

    public void register(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    public void guestLogin(View view){
        user_auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Guest log in successful.");
                openShop();
            } else {
                Log.d(LOG_TAG, "Guest log in fail!");
                Toast.makeText(MainActivity.this, "Guest log in fail: "
                        + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", LoginUsername.getText().toString());
        editor.putString("password", LoginPassword.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}