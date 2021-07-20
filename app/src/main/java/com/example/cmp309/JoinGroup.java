package com.example.cmp309;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class JoinGroup extends AppCompatActivity {
    private Button joinGroupBtn;
    private EditText codeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        initialiseUI();

        Listeners JoinGroupListeners = new Listeners(this);
        JoinGroupListeners.JoinGroupButtonListener(joinGroupBtn, GroupInformation.class, codeText, new FirebaseAccess(this));
    }

    // Get Element Data
    private void initialiseUI() {
        joinGroupBtn = findViewById(R.id.joinGroupBtn);
        codeText = findViewById(R.id.code);
    }

}

