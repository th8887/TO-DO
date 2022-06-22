package com.example.schedulenotification.Activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.schedulenotification.refFB.reAuth;
import static com.example.schedulenotification.refFB.refDB;
import static com.example.schedulenotification.refFB.storage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
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

    EditText n, p;
    CheckBox cBconnectview;
    ImageView profile;
    ListView categories;
    TextView showCom, e;

    public static String name, email, uid, phone, profilelink;

    public static ArrayList<String> category;
    /**
     * @param pos-for the position of the chosen item in the Array List.
     */
    int pos;

    User user;

    Toolbar tb;

    boolean b = false;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        n = (EditText) findViewById(R.id.nameu);
        e = (TextView) findViewById(R.id.emailu);
        p = (EditText) findViewById(R.id.phoneu);
        categories = (ListView) findViewById(R.id.categories);
        cBconnectview = (CheckBox) findViewById(R.id.cBconnectview);
        profile = (ImageView) findViewById(R.id.profile);
        showCom = (TextView) findViewById(R.id.showCom);

        categories.setOnItemClickListener(this);
        categories.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        categories.setOnCreateContextMenuListener(this);

        tb = findViewById(R.id.tb);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().getIntExtra("check", -1) == 3) {
            uploadProfile(getIntent().getStringExtra("way"));
            b= true;
        }

        checkPermission(42, Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SCHEDULE_EXACT_ALARM);


        Toast.makeText(this, "If you change things, make sure to press update connect!", Toast.LENGTH_SHORT).show();
    }

    /**
     * asks the user for permissions in order for the app to run smoothly.
     *
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

    /**
     * in order to read the users information from the firebase database.
     */
    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for (DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    n.setText(user.getName());
                    name = user.getName();
                    p.setText(user.getPhone());
                    phone = user.getPhone();

                    category = user.getCategory();

                    profilelink = user.getProfile();

                    complete = user.getComplete();
                    all = user.getAll();


                    if ((!user.getProfile().equals(" ")) && (b == false)) {
                        profilelink = user.getProfile();
                        uploadProfile(user.getProfile());
                    }
                    b= false;


                    showCom.setText("Completed Missions: " + complete + "/" + all);

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
        if (fbuser == null) {
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
     *
     * @param view
     */
    public void pickPic(View view) {
        Intent pic2 = new Intent(this, Camera_or_Gallery.class);
        pic2.putExtra("page", 2);
        startActivity(pic2);
    }

    /**
     * this function will upload the profile picture from Firebase Storage
     * and will show it to the user in the ImageView.
     *
     * @param s
     */
    private void uploadProfile(String s) {
        StorageReference storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child(s);

        final long ONE_MEGABYTE = 3150 * 3150;

        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profile.setImageBitmap(bMap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Information.this, "not Working.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * this function updates the users information and sends the updated data to FireBase Database.
     *
     * @param view
     */
    public void update(View view) {
        phone = p.getText().toString();
        name = n.getText().toString();
        email = e.getText().toString();
        profilelink = getIntent().getStringExtra("way");

        String s = showCom.getText().toString();
        s = s.substring(20);
        complete = Integer.parseInt(s.substring(0, 1));
        all = Integer.parseInt(s.substring(2));

        User u2 = new User(name, email, phone, uid, profilelink, true);
        u2.setCategory(category);
        u2.setComplete(complete);
        u2.setAll(all);
        refDB.child(uid).setValue(u2);

        Toast.makeText(this, "Information Updated!", Toast.LENGTH_SHORT).show();
    }

    /**
     * if the user wants to log out of his account
     *
     * @param view
     */
    public void logOut(View view) {
        if (!cBconnectview.isChecked()) {
            reAuth.signOut();

            SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("stayConnect", false);
            editor.commit();
            name = n.getText().toString();
            email = e.getText().toString();
            phone = p.getText().toString();
            if (profilelink.equals("")){
                profilelink = " ";
            }

            String s = showCom.getText().toString();
            s = s.substring(20);
            complete = Integer.parseInt(s.substring(0, 1));
            all = Integer.parseInt(s.substring(2));

            User u2 = new User(name, email, phone, uid, profilelink, false);
            u2.setCategory(category);
            u2.setComplete(complete);
            u2.setAll(all);
            refDB.child(uid).setValue(u2);

            Intent si = new Intent(Information.this, Authentication.class);
            startActivity(si);
            finish();
        } else {
            Toast.makeText(this, "You chose to stay connected.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * an option to add a category.
     *
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

        final EditText nameC = dialog.findViewById(R.id.nameC);
        Button add = dialog.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String n = nameC.getText().toString();
                category.add(n);

                ArrayAdapter<String> adp2 = new ArrayAdapter<String>(Information.this
                        , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                        , category);
                categories.setAdapter(adp2);

                User newU= new User(name,email,phone,uid,Information.profilelink,true);
                newU.setCategory(category);
                newU.setComplete(complete);
                newU.setAll(all);
                refDB.child(uid).setValue(newU);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * gives the user a warning that he can not change the email of the account
     * because Firebase Authentication knows the user with one email.
     *
     * @param view
     */
    public void warning(View view) {
        Toast.makeText(this, "You can't change the email!", Toast.LENGTH_SHORT).show();
    }

    /**
     * gets the position of the category
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        pos = position;
    }

    /**
     * Creates a Context Menu for each space in the ListView
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (pos == 0 ){
            Toast.makeText(this, "you cant delete this", Toast.LENGTH_SHORT).show();
        }
        else {
            menu.add("Delete Category");
            menu.add("Cancel");
        }
    }

    /**
     * check mission- deletes the mission from uncompleted root, updates the activity
     * to true(the mission is done), and adds the mission into completed root in Firebase.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Delete Category":
                category.remove(pos + 1);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.i:
                i = new Intent(this, About.class);
                startActivity(i);
                break;
            case R.id.cm:
                i = new Intent(this, CreateMission.class);
                startActivity(i);
                break;
            case R.id.cl:
                i = new Intent(this, CheckList.class);
                startActivity(i);
                break;
            case R.id.c:
                i = new Intent(this, CalendarView.class);
                startActivity(i);
                break;
            case R.id.tblock:
                i = new Intent(this, TimerBlock.class);
                startActivity(i);
                break;
            case R.id.ui:
                break;
        }
        return true;
    }


}