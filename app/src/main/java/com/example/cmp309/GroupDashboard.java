package com.example.cmp309;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.pushbots.push.Pushbots;

import java.util.ArrayList;

public class GroupDashboard extends AppCompatActivity {
    private Button createGroupBtn, joinGroupBtn, signOutBtn;
    private LinearLayout layout;
    private ArrayList<Button> groupBtns=new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_dashboard);
        initialiseUI();

        // sets up push notifications
        Pushbots.sharedInstance().registerForRemoteNotifications();
        Pushbots.setName(FirebaseAuth.getInstance().getUid());
        Pushbots.setAlias(FirebaseAuth.getInstance().getUid());


        Listeners GroupDashboardListeners = new Listeners(this);
        FirebaseAccess FB = new FirebaseAccess(this);
        UI UI_ = new UI(this);

        // btn listeners
        GroupDashboardListeners.GeneralButtonListener(createGroupBtn, CreateGroup.class);
        GroupDashboardListeners.SignOutButtonListener(signOutBtn, LoginActivity.class);
        GroupDashboardListeners.GeneralButtonListener(joinGroupBtn, JoinGroup.class);

        // fills btns
        FB.GetUsersGroupInformation("Members/" + FirebaseAuth.getInstance().getCurrentUser().getUid(), UI_, groupBtns, GroupDashboardListeners, layout );
    }

    // Get Element Information
    private void initialiseUI() {
        createGroupBtn = findViewById(R.id.createGroupBtn);
        signOutBtn = findViewById(R.id.logout);
        joinGroupBtn = findViewById(R.id.joinGroupBtn);
        layout = findViewById(R.id.listOfGroups);

    }
}
