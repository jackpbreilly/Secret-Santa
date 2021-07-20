package com.example.cmp309;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAccess {

    public ArrayList<Button> groupBtns_ = new ArrayList<>(); // Buttons for GroupDashboard display
    private FirebaseAuth mAuth;
    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ArrayList<String> members_arr = new ArrayList<>(); // List of members used on Group Information


    public FirebaseAccess(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
    }


    // Add group to users groups list
    public void AddToGroup(String GID, final String memberID) {

        final DatabaseReference dbRef = database.getReference().child("Group/" + GID + "");
        final DatabaseReference dbRefUp = database.getReference().child("Group/" + GID + "/members");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> members;
                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map.containsKey("members")) {
                    members = (ArrayList<String>) map.get("members");
                } else {
                    members = new ArrayList<>();
                }
                members.add(memberID);
                dbRefUp.setValue(members);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public ArrayList<String> getMembers_arr() {
        return members_arr;
    }

    // Checks if user has account then transfers them to their groudashboard
    public void SignIn(String email, String password, final ProgressBar progressBar) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(context, GroupDashboard.class);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


    // Fills groupinformation page with data
    public void GetUsersGroupInformation(String dbRefPath, final UI ui, final ArrayList<Button> groupBtns, final Listeners GroupDashboardListener, final LinearLayout layout) {
        DatabaseReference dbRef = database.getReference().child(dbRefPath);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                int i = 0;
                if (map.containsKey("groups")) {
                    HashMap<String, String> groups = (HashMap<String, String>) map.get("groups");
                    for (String group : groups.keySet()) {
                        groupBtns_ = ui.CreateUsersGroupButton(groups.get(group), i, groupBtns, GroupDashboardListener);
                        i++;
                    }
                    ui.AddUsersGroupButtonsToDisplay(layout, groupBtns_);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

// Adds user to group's members list
    void JoinGroup(final String dbRefPath, final String code, final FirebaseAccess fb, final Listeners ls, final Class toOpen) {

        final DatabaseReference dbRef = database.getReference().child("Group");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map.containsKey(code)) {
                    DatabaseReference dbRefUp = database.getReference().child(dbRefPath);
                    dbRefUp.child("groups").push().setValue(code);
                    Toast.makeText(context, "Sucessfully Joined Group", Toast.LENGTH_LONG).show();
                    ls.LaunchIntentWithData(toOpen, code);
                } else {
                    Toast.makeText(context, "Error Joined Group", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

// creates new group based on passed in group class
    void CreateGroup(String dbRefPath, Group group, final FirebaseAccess fb) {
        DatabaseReference dbRef = database.getReference().child(dbRefPath);
        dbRef
                .push()
                .setValue(group, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference) {
                        fb.JoinGroup("Members/" + FirebaseAuth.getInstance().getCurrentUser().getUid(), databaseReference.getKey(), fb, new Listeners(context), GroupInformation.class);
                        fb.AddToGroup(databaseReference.getKey(), FirebaseAuth.getInstance().getUid());
                    }
                });
        Toast.makeText(context, "Sucessfully Created Group", Toast.LENGTH_LONG).show();
    }

    // Logic to generate secret santa.

    public void CreateSecretSanta(String dbRefPath, String sid, FirebaseAccess FB, String msg) {
        DatabaseReference dbRef = database.getReference().child(dbRefPath + "/" + sid + "/Santa/");

        // adds each user to shuffled array
        ArrayList<String> shuffled_selection = new ArrayList<>();
        for (String member : members_arr) {
            shuffled_selection.add(member);
        }
        boolean perfectShuffle = false; // nobody has self

        // shuffles the group until no member has self
        while (perfectShuffle != true) {
            Collections.shuffle(shuffled_selection);
            boolean hadPerfectShuffle = true;
            for (int i = 0; i < shuffled_selection.size(); i++) {
                if (shuffled_selection.get(i) == members_arr.get(i)) {
                    hadPerfectShuffle = false;
                }
            }
            if (hadPerfectShuffle == true) {
                perfectShuffle = true;
            }
        }

        /// adds data to new santa array in db
        for (int i = 0; i < shuffled_selection.size(); i++) {
            dbRef.child(members_arr.get(i)).setValue(shuffled_selection.get(i));
        }
        Toast.makeText(context, "Sucessfully Created Secret Santa", Toast.LENGTH_LONG).show();
        for (String member : members_arr) {
            FB.PushNotifications(member, msg);
        }
    }

    // adds members to members_arr
    void GetMembers(String dbRefPath, final TextView memberstv) {
        DatabaseReference dbRef = database.getReference().child(dbRefPath);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                ArrayList<String> members = (ArrayList<String>) map.get("members");
                for (int j = 0; j < members.size(); ++j) {
                    members_arr.add(members.get(j));
                }

                GetMembers(memberstv);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Fills textviews with group data
    public void GetGroupInformation(String dbRefPath, final TextView name, final TextView time, final TextView limit, final TextView date, final TextView theme, final TextView lat, final TextView lon, final TextView santa, final GoogleMap mMap, final FirebaseAccess fb, final Button genBtn, final LinearLayout layout, final Button viewBtn, final LinearLayout vlayout) {
        DatabaseReference dbRef = database.getReference().child(dbRefPath);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                name.setText((CharSequence) map.get("name"));
                time.setText("⌚ " + map.get("time"));
                date.setText("\uD83D\uDCC5 " + map.get("date"));
                theme.setText("\uD83D\uDCA1 " + map.get("theme"));
                limit.setText("\uD83D\uDCB0 " + map.get("limit"));
                lat.setText((CharSequence) map.get("lat"));
                lon.setText((CharSequence) map.get("lon"));
                String cid = (String) map.get("creator");
                String thisId = FirebaseAuth.getInstance().getUid();


                // looking to see if creator and if santa has been generated
                if (map.containsKey("Santa")) {
                    layout.removeView(genBtn);

                } else {
                    vlayout.removeView(viewBtn);
                    santa.setText("The secret santa has not started...");
                }
                if (thisId.equals(cid)) {
                } else {
                    layout.removeView(genBtn);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // sets the santa textview to the users name rather than their id
    public void SetSanta(String dbRefPath, final TextView santa, final FirebaseAccess fb) {
        DatabaseReference dbRef = database.getReference().child(dbRefPath);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                HashMap<String, String> santaList = (HashMap<String, String>) map.get("Santa");
                fb.GetUserNameSanta(santaList.get(FirebaseAuth.getInstance().getUid()), santa, "name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Set members text view to show members name on group information
    public void GetMembers(TextView members) {
        for (String member : members_arr) {
            GetUserName(member, members, "name");
            members.setText(members.getText());
        }
    }

    // returns users name
    public void GetUserName(String UserId, final TextView textView, final String el) {
        DatabaseReference dbRef = database.getReference().child("Members/" + UserId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                textView.setText(textView.getText() + (String) map.get(el) + "\n");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Get users name for the santa element
    public void GetUserNameSanta(String UserId, final TextView textView, final String el) {
        DatabaseReference dbRef = database.getReference().child("Members/" + UserId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                textView.setText((String) map.get(el));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // sets up user db
    public void Setup(String name) {
        DatabaseReference dbRef = database.getReference().child("Members/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbRef.child("name").setValue(name);
    }

    // Request data from webpage which pushes a notification to members of group
    public void PushNotifications(String UserId, String msg) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://mayar.abertay.ac.uk/~1704736/pb.php?alias=" + UserId + "&msg=" + msg;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    // validation for join group check if member of group
    public void CheckIfInGroup(String dbRefPath, final String member_, final Class toOpen, final EditText code, final FirebaseAccess fb) {
        DatabaseReference dbRef = database.getReference().child(dbRefPath + "/" + code.getText());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Object val = dataSnapshot.getValue();
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map == null) {
                    Toast.makeText(context, "Error: Group Doesn't Exist", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> members = (ArrayList<String>) map.get("members");

                if (!members.contains(member_)) {
                    fb.JoinGroup("Members/" + FirebaseAuth.getInstance().getCurrentUser().getUid(), code.getText().toString(), fb, new Listeners(context), toOpen);
                    fb.AddToGroup(code.getText().toString(), FirebaseAuth.getInstance().getUid());
                } else {
                    Toast.makeText(context, "Error: Cannot Join Group", Toast.LENGTH_SHORT).show();
                    return;
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
