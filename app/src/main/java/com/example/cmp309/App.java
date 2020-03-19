package com.example.cmp309;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class App extends AppCompatActivity {

    private Button signoutBtn, createGroupBtn;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference().child("Group");

    Group group = new Group();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        initializeUI();
        createGroup();
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String myKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });
    }
    private void signout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(App.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Sucessfully Logged Out", Toast.LENGTH_LONG).show();

    }
    private void createGroup() {

        group.setName(Calendar.getInstance().getTime().toString());
        dbRef.push().setValue(group);
        Toast.makeText(getApplicationContext(), "Sucessfully Created Group", Toast.LENGTH_LONG).show();

    }





    private void initializeUI() {

        signoutBtn = findViewById(R.id.signout);
        createGroupBtn = findViewById(R.id.createGroup);
    }
}
