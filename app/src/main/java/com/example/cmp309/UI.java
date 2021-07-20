package com.example.cmp309;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class UI {

    private Context context;

    public UI(Context context) {
        this.context = context;
    }

    // Create Bbutton data
    public ArrayList<Button> CreateUsersGroupButton(final String group, final int buttonPos, final ArrayList<Button> groupBtns, final Listeners GroupDashboardListeners) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference().child("Group/" + group);
        final Button btn = new Button(context);
        groupBtns.add(btn);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object val = dataSnapshot.getValue();

                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                groupBtns.get(buttonPos).setText((CharSequence) map.get("name"));
                groupBtns.get(buttonPos).setTag(group);


                groupBtns.get(buttonPos).setTextSize(20);
                groupBtns.get(buttonPos).setBackgroundColor(0xFF74d680);
                groupBtns.get(buttonPos).setTextColor(0xFF378b29);

                GroupDashboardListeners.GroupInformationButtonListener(groupBtns.get(buttonPos), GroupInformation.class,(String) groupBtns.get(buttonPos).getTag());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return groupBtns;
    }

    // Adds buttons to display
    public void AddUsersGroupButtonsToDisplay(LinearLayout layout, ArrayList<Button> groupBtns){
        int i = 0;
        layout.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 20, 0, 0);

        for(i =0; i< groupBtns.size();i++){
            layout.addView(groupBtns.get(i), layoutParams);
        }
    }
}
