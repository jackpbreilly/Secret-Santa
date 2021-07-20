package com.example.cmp309;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

import me.abhinay.input.CurrencyEditText;

public class CreateGroup extends AppCompatActivity {
    CurrencyEditText limitText;
    LatLng latLng;
    private Button createGroupBtn;
    private EditText nameText, themeText;
    private TextView date, timeText, lat, lon; // lat & lon invisible just for hosting data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initialiseUI();


        // listeners used to gather data
        Listeners CreateGroupListeners = new Listeners(this);

        CreateGroupListeners.CreateGroupButtonListener(createGroupBtn, new FirebaseAccess(this), nameText, timeText, date, themeText, limitText, lat, lon);
        CreateGroupListeners.DateButtonListener(date);
        CreateGroupListeners.TimeButtonListener(timeText);
        CreateGroupListeners.LimitListener(limitText);


        // Location Input
        String apiKey = "AIzaSyCTqHrBomOIIO1WHeBQDnq48pyQSmJclMQ";  // Remove on Production

        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Location");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                // sets lat & lon so data can be input to DB
                latLng = place.getLatLng();
                lat.setText(String.valueOf(latLng.latitude));
                lon.setText(String.valueOf(latLng.longitude));
            }

            @Override
            public void onError(Status status) {
                //Api handles error msg
            }
        });

    }

    // Get Element Information
    private void initialiseUI() {
        createGroupBtn = findViewById(R.id.createGroupBtn);
        nameText = findViewById(R.id.name);
        timeText = findViewById(R.id.time);
        date = findViewById(R.id.date);
        themeText = findViewById(R.id.theme);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        limitText = (CurrencyEditText) findViewById(R.id.limit);
    }
}
