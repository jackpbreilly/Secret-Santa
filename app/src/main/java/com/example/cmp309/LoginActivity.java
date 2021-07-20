package com.example.cmp309;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private EditText email, password;
    private Button loginBtn, regBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiseUI();
        FirebaseAccess LoginFirebase = new FirebaseAccess(this);
        Listeners LoginListeners = new Listeners(this);
        User LoggedInUser = new User();
        LoginListeners.LoginButtonListener(loginBtn, LoggedInUser, LoginFirebase, progressBar, email, password);
        LoginListeners.RegisterButtonListener(regBtn, RegistrationActivity.class, FirebaseAuth.getInstance().getCurrentUser());
        LoginListeners.CheckIfLoggedIn(FirebaseAuth.getInstance().getCurrentUser(), GroupDashboard.class);

    }

    private void initialiseUI() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        regBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
    }
}