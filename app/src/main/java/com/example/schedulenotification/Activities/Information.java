package com.example.schedulenotification.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.schedulenotification.refFB.reAuth;
import static com.example.schedulenotification.refFB.refDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
import com.example.schedulenotification.R;
import com.example.schedulenotification.Classes.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Information extends AppCompatActivity {

    EditText n, e, uID,p;
    CheckBox cBconnectview;

    String name, email, uid,phone;

    ArrayList<String> category;

    User user;

    Toolbar tb;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        n= (EditText) findViewById(R.id.nameu);
        e= (EditText) findViewById(R.id.emailu);
        uID= (EditText) findViewById(R.id.uidu);
        p=(EditText) findViewById(R.id.phoneu);
        cBconnectview=(CheckBox)findViewById(R.id.cBconnectview);

        tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    n.setText(user.getName());
                    p.setText(user.getPhone());
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };

    /**
     * ON start the activity shows the information about the user.
     */
    @Override
    public void onStart() {
        super.onStart();


        FirebaseUser fbuser = reAuth.getCurrentUser();
        if(fbuser == null){
            Intent i = new Intent(Information.this, Authentication.class);
            startActivity(i);
        }
        uid = fbuser.getUid();
        Query query = refDB
                .orderByChild("uID")
                .equalTo(uid);

        query.addListenerForSingleValueEvent(VEL);
        email = fbuser.getEmail();
        e.setText(email);
        uID.setText(uid);
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        cBconnectview.setChecked(isChecked);
    }

    public void update(View view) {
        if (!cBconnectview.isChecked()) {
            reAuth.signOut();
        }
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean("stayConnect",false);
        editor.commit();
        phone= user.getPhone();
        name = user.getName();
        category = user.getCategory();
        User u2 = new User(name,email,phone, uid,false);
        refDB.child(uid).setValue(u2);

        Intent si= new Intent(Information.this,Authentication.class);
        startActivity(si);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.info, menu);

        menu.add("Create A Mission📝");
        menu.add("Check List📃");
        menu.add("Calendar📅");
        menu.add("Focus Timer⏱️");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i;
        switch(item.getTitle().toString()){
            case "INFO":
                i = new Intent(this, About.class);
                startActivity(i);
                finish();
                break;
            case "Create A Mission📝":
                i= new Intent(this, CreateMission.class);
                startActivity(i);
                break;
            case "Check List📃":
                i= new Intent(this, CheckList.class);
                startActivity(i);
                break;
            case "Calendar📅":
                i= new Intent(this, CalendarView.class);
                startActivity(i);
                break;
            case "Focus Timer⏱️":
                i= new Intent(this, TimerForFocus.class);
                startActivity(i);
                break;
        }
        return true;
    }

}