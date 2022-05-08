package com.example.szonyegwebshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegistrationActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 42;

    private SharedPreferences preferences;
    private FirebaseAuth user_auth;

    EditText RegistrationName;
    EditText RegistrationUserName;
    EditText RegistrationEmail;
    EditText RegistrationPassword;
    EditText PasswordAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if(secret_key != 42){
            finish();
        }

        RegistrationName = findViewById(R.id.RegistrationName);
        RegistrationUserName = findViewById(R.id.RegistrationUsername);
        RegistrationEmail = findViewById(R.id.RegistrationEmail);
        RegistrationPassword = findViewById(R.id.RegistrationPassword);
        PasswordAgain = findViewById(R.id.RegistrationPasswordAgain);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");

        RegistrationUserName.setText(username);
        RegistrationPassword.setText(password);

        user_auth = FirebaseAuth.getInstance();

    }

    public void register(View view) {
        String name = RegistrationName.getText().toString();
        String username = RegistrationUserName.getText().toString();
        String email = RegistrationEmail.getText().toString();
        String password = RegistrationPassword.getText().toString();
        String passwordConfirm = PasswordAgain.getText().toString();

        if (password.equals(passwordConfirm)){
            user_auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Log.d(LOG_TAG, "User successfully created.");
                                loginRedirect();
                            } else {
                                Log.d(LOG_TAG, "User creation failed");
                                Toast.makeText(RegistrationActivity.this, "User creation failed: "
                                        + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Log.d(LOG_TAG, "Passwords do not match!");
            Toast.makeText(RegistrationActivity.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
        }
    }

    public void cancel(View view) {
        finish();
    }

    private void loginRedirect(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}