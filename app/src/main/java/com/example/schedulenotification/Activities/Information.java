package com.example.schedulenotification.Activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.schedulenotification.Activities.CreateMission.refDBC;
import static com.example.schedulenotification.Activities.CreateMission.refDBUC;
import static com.example.schedulenotification.refFB.reAuth;
import static com.example.schedulenotification.refFB.refDB;
import static com.example.schedulenotification.refFB.storage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
import com.example.schedulenotification.Classes.Mission;
import com.example.schedulenotification.R;
import com.example.schedulenotification.Classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Information extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static int complete;
    public static int all;

    EditText n, e,p;
    CheckBox cBconnectview;
    ImageView profile;
    ListView categories;
    TextView showCom;

    String name, email, uid,phone;

    ArrayList<String> category;
    //for the position of the chosen item in the Array List.
    int pos;

    User user;

    Toolbar tb;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        n= (EditText) findViewById(R.id.nameu);
        e= (EditText) findViewById(R.id.emailu);
        p=(EditText) findViewById(R.id.phoneu);
        categories = (ListView) findViewById(R.id.categories);
        cBconnectview=(CheckBox)findViewById(R.id.cBconnectview);
        profile = (ImageView) findViewById(R.id.profile);
        showCom = (TextView) findViewById(R.id.showCom);

        categories.setOnItemClickListener(this);
        categories.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        categories.setOnCreateContextMenuListener(this);

        tb = findViewById(R.id.tb);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences settings = getSharedPreferences("MISSION_NUM", MODE_PRIVATE);
        complete = settings.getInt("complete", -1);
        all = settings.getInt("all", -2);

        showCom.setText("Completed Missions:" +
                "\n" + complete + "/" + all);
        checkPermission(42, Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SCHEDULE_EXACT_ALARM);
    }

    /**
     * asks the user for permissions in order for the app to run smoothly.
     * @param callbackId
     * @param permissionsId
     */
    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }

    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    n.setText(user.getName());
                    p.setText(user.getPhone());

                    category = user.getCategory();

                    ArrayAdapter<String> adp = new ArrayAdapter<String>(Information.this,
                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, category);
                    categories.setAdapter(adp);
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
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        cBconnectview.setChecked(isChecked);
    }

    /**
     * if the user wants to pick a picture for his account
     * @param view
     */

    /*Intent pic2 = new Intent(this, Camera_or_Gallery.class);
        pic2.putExtra("page", 2);
        startActivity(pic2);
     */
    public void pickPic(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Pick Camera or Gallery:");
        adb.setCancelable(true);
        adb.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });
        adb.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profile.setImageBitmap(imageBitmap);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    profile.setImageURI(selectedImage);
                }
                break;
        }
    }

    public void update(View view) {
        phone= p.getText().toString();
        name = n.getText().toString();
        email = e.getText().toString();
        User u2 = new User(name,email,phone, uid,false);
        u2.setCategory(category);
        refDB.child(uid).setValue(u2);

        Toast.makeText(this, "Information Updated!", Toast.LENGTH_SHORT).show();
    }

    /**
     * if the user wants to log out of his account
     * @param view
     */
    public void logOut(View view) {
        if (!cBconnectview.isChecked()) {
            reAuth.signOut();

            SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("stayConnect", false);
            editor.commit();

            Intent si = new Intent(Information.this, Authentication.class);
            startActivity(si);
            finish();
        }
        else{
            Toast.makeText(this, "You chose to stay connected.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * an option to add a category.
     * @param view
     */
    public void addCat(View view) {
        final Dialog dialog = new Dialog(Information.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.create_category);

        final EditText nameC= dialog.findViewById(R.id.nameC);
        Button add= dialog.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String n= nameC.getText().toString();
                category.add(n);

                ArrayAdapter<String> adp2= new ArrayAdapter<String>(Information.this
                        , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                        ,category);
                categories.setAdapter(adp2);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void warning(View view) {
        Toast.makeText(this, "You can't change the email!", Toast.LENGTH_SHORT).show();
    }

    /**
     * gets the position of the category
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pos = position;
    }

    /**
     * Creates a Context Menu for each space in the ListView
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("Delete Category");
        menu.add("Cancel");
    }

    /**
     * check mission- deletes the mission from uncompleted root, updates the activity
     * to true(the mission is done), and adds the mission into completed root in Firebase.
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getTitle().toString()){
            case "Delete Category":
                category.remove(pos+1);
                ArrayAdapter<String> adp = new ArrayAdapter<String>(Information.this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, category);
                categories.setAdapter(adp);
            break;
            case "Cancle":
                closeContextMenu();
                break;
        }
        return true;
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