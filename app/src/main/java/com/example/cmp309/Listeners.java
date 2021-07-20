package com.example.cmp309;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import me.abhinay.input.CurrencyEditText;

public class Listeners {
    public DatePickerDialog.OnDateSetListener mDateSetListener;


    private Context context;
    public Listeners(Context context) {
        this.context = context;
    }

    public void LoginButtonListener(Button btn, final User user, final FirebaseAccess fb, final ProgressBar progressBar, final EditText email, final EditText password){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.loginUserAccount(context,fb, progressBar, email.getText().toString(), password.getText().toString());
            }
        });
    }

    public void RegisterButtonListener(Button btn, final Class toOpen, final FirebaseUser currentUser){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser == null)
                    LaunchIntent(toOpen);
            }
        });
    }

    public void CopyCode(final TextView tv){

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Code", tv.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied code to clipboard", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void SignOutButtonListener(Button btn, final Class toOpen){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LaunchIntent(toOpen);
            }
        });
    }

    public void GroupInformationButtonListener(Button btn, final Class toOpen, final String data){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchIntentWithData(toOpen, data);
            }
        });
    }

    public void JoinGroupButtonListener(Button btn, final Class toOpen, final EditText code, final FirebaseAccess fb){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fb.CheckIfInGroup("Group",FirebaseAuth.getInstance().getUid(),GroupInformation.class,code,fb );

            }
        });
    }
    public void CreateGroupButtonListener(Button btn, final FirebaseAccess fb, final EditText name, final TextView time, final TextView date, final EditText theme, final EditText limit, final TextView lat, final TextView lon){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(time.getText()) || TextUtils.isEmpty(date.getText()) || TextUtils.isEmpty(limit.getText()) || TextUtils.isEmpty(lat.getText()) || TextUtils.isEmpty(lon.getText())){
                    Toast.makeText(context, "Error: Input Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                Group group = new Group(name.getText().toString(),time.getText().toString(),date.getText().toString(),theme.getText().toString(),limit.getText().toString(),  lat.getText().toString(), lon.getText().toString(), FirebaseAuth.getInstance().getUid().toString());
                fb.CreateGroup("Group/", group, fb);
            }
        });

    }


    public void DateButtonListener(final TextView btn){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        context,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                btn.setText(date);
            }
        };
    }

    public void GenerateSecretSantaButton(Button btn, final FirebaseAccess fb, final String sessionId, final String msg, final TextView name){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fb.getMembers_arr().size() == 1){
                    Toast.makeText(context, "Error: Cannot Generate Secret Santa", Toast.LENGTH_SHORT).show();
                    return;
                }
                fb.CreateSecretSanta("/Group", sessionId, fb, msg + name.getText().toString() );
                LaunchIntentWithData(GroupInformation.class, sessionId);
            }
        });

    }

    public void GeneralButtonListener(Button btn, final Class toOpen){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchIntent(toOpen);
            }
        });
    }

    public void CheckIfLoggedIn(final FirebaseUser currentUser, final Class toOpen){
       if(currentUser != null)
            LaunchIntent(toOpen);
    }


    public void LaunchIntent(Class toOpen){
        Intent intent = new Intent(context, toOpen);
        context.startActivity(intent);
    }

    public void LaunchIntentWithData(Class toOpen, String data){
        Intent intent = new Intent(context, toOpen);
        intent.putExtra("DATA", data);
        context.startActivity(intent);
    }

    public void LimitListener(CurrencyEditText limitText) {
        limitText.setCurrency("£");
        limitText.setDelimiter(false);
        limitText.setSpacing(false);
        limitText.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        limitText.setSeparator(".");
    }

    public void TimeButtonListener(final TextView btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);





                TimePickerDialog timePickerDialog = new TimePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        btn.setText(hourOfDay+":"+minute);
                    }
                }, hour, minute, true);

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();

            }
        });
    }


    public void ViewSantaButton(final Button viewBtn, final FirebaseAccess fb, final TextView santa, final String dbRef) {

        viewBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(santa.getText().equals("You're Santa To...")){
                    fb.SetSanta(dbRef, santa, fb);
                        viewBtn.setText("Hide");
                    }
                    else{
                        viewBtn.setText("Reveal");
                        santa.setText("You're Santa To...");
                    }
                    return true;
                }
                return false;
            }
        });
    }
}

