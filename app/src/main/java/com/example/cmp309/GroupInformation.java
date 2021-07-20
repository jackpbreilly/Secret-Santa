package com.example.cmp309;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GroupInformation extends FragmentActivity implements OnMapReadyCallback {

    Button generateBtn, viewBtn;
    TextView name, date,limit, time, theme, code, lat,lon, santa, members;
    private GoogleMap mMap;
    private LinearLayout layout, vlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_information);
        initialiseUI();

        String sessionId = getIntent().getStringExtra("DATA"); // used to get group data

        // setup map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        Listeners GroupInformationListeners = new Listeners(this);
        FirebaseAccess FB = new FirebaseAccess(this);
        UI UI_ = new UI(this);
        FB.GetGroupInformation("/Group/"+sessionId,name,time,limit,date,theme,lat, lon, santa, mMap,FB, generateBtn,layout, viewBtn, vlayout);
        FB.GetMembers("Group/"+sessionId, members);

        GroupInformationListeners.GenerateSecretSantaButton(generateBtn,FB,sessionId, "You can now view your secret santa from ", name);
        GroupInformationListeners.ViewSantaButton(viewBtn,FB, santa, "/Group/"+sessionId);
       code.setText(sessionId);
        GroupInformationListeners.CopyCode(code);

    }

// get element details.
    private void initialiseUI() {
        layout = findViewById(R.id.generateLayout);
        vlayout = findViewById(R.id.viewLayout);
        generateBtn = findViewById(R.id.generate);
        viewBtn = findViewById(R.id.view);
        name = findViewById(R.id.name);
        members = findViewById(R.id.members);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        limit = findViewById(R.id.limit);
        theme = findViewById(R.id.theme);
        santa = findViewById(R.id.santa);
        code = findViewById(R.id.code);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);

    }


    // position map
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        lon.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mMap = googleMap;

                // Add a marker in Sydney and move the camera
                LatLng location = new LatLng(Double.valueOf(lat.getText().toString()), Double.valueOf(lon.getText().toString()));
                mMap.addMarker(new MarkerOptions().position(location).title("Meeting Place"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15);
                googleMap.animateCamera(cameraUpdate);
            }
        });


    }

}
