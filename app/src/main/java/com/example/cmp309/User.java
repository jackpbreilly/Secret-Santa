package com.example.cmp309;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class User {

    // validates user data
    void loginUserAccount(Context context,FirebaseAccess fb, ProgressBar progressBar, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        if (email.equals("") || email.equals("Email") || email.length() < 5){
            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(context,"Error: Invalid Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("") || email.equals("Password") || password.length() <5){
            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(context,"Error: Invalid Password",Toast.LENGTH_SHORT).show();
            return;
        }
        fb.SignIn(email, password, progressBar);
    }

}
