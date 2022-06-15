package com.example.schedulenotification.Activities;

import static android.content.ContentValues.TAG;
import static com.example.schedulenotification.Activities.CalendarActivities.CreateEvent.months;
import static com.example.schedulenotification.Activities.Information.all;
import static com.example.schedulenotification.Activities.Information.complete;
import static com.example.schedulenotification.Classes.Notification.channelID;
import static com.example.schedulenotification.Classes.Notification.messageExtra;
import static com.example.schedulenotification.Classes.Notification.notificationID;
import static com.example.schedulenotification.Classes.Notification.titleExtra;
import static com.example.schedulenotification.refFB.database;
import static com.example.schedulenotification.refFB.reAuth;
import static com.example.schedulenotification.refFB.refDB;
import static com.example.schedulenotification.refFB.storage;
import static com.example.schedulenotification.Classes.Listener.status;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
import com.example.schedulenotification.Classes.Mission;
import com.example.schedulenotification.Classes.Notification;
import com.example.schedulenotification.Adapters.ColorAdapter;
import com.example.schedulenotification.R;
import com.example.schedulenotification.Classes.User;
import com.example.schedulenotification.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * The type Create mission.
 *
 * @author Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version beta
 * @since 1 /10/2021 An activity to create a mission, to review one and to it creates a notification whe the mission in finished
 */
public class CreateMission extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener, View.OnCreateContextMenuListener{

    /**
     * The constant refDBUC.
     */
    public static DatabaseReference refDBUC= database.getReference(
            "Mission/"+ reAuth.getCurrentUser().getUid() +"/uncompleted");

    /**
     * The constant refDBC.
     */
    public static DatabaseReference refDBC= database.getReference(
            "Mission/"+ reAuth.getCurrentUser().getUid() +"/completed");


    EditText t, des;
    Spinner catS;

    ListView linksNames;
    /**
     * The importance of the mission (0-highest, 1- middle, 2-lowest)
     */
    RadioButton i0, i1, i2;

    RadioGroup iGroup;

    Spinner colors;
    /**
     * times- for the start of a mission and the end of one
     */
    TextView times;
    /**
     * string array of the colors
     */
    String[] colorsName = { "#FFFFFF","#FFE3D4", "#ECD5E3","#8FCACA","#ECEAE4", "#FFBF91", "#D4D479", "#CCE2CB", "#FFFFB5"
            , "#97C1A9", "#D7EFEF", "#FCB9AA", "#C6DBDA", "#F6EAC2", "#E67C73"};

    /**
     * The Names for the colors(mostly blank for good appearance).
     */
    String [] names = {"No Color","","","","","","","","","","","","","",""};

    User user;
    /**
     * @param uid - takes the users id from firebase auth.
     * @param n - the category the user adds
     */
    String uid, n;
    /**
     * the user comes back to this activity from to different activities:
     * 1.Camera_or_Gallery- check = 0
     * 2.CheckList-> check= 1;
     */
    int check;
    /**
     * Strings, boolean and arrayList for the update user's category.
     */
    String name, phone, uID, email;
    /**
     * @param c -the array list of the categories
     */
    ArrayList<String> c;
    /**
     * @param a- the active type of the mission(for the User object)
     */
    boolean a;

    /**
     * params for a new Mission.
     *
     * @param e - end of mission
     * @param category - takes the index of the chosen category.
     * @param importance -
     * 0-very important
     * 1-less important
     * 2-not important
     */
    int category;
    /**
     * for a check in when the user goes between activities and he didn't press any importance "RadioButtons"
     */
    int importance=3;
    /**
     * The Title.
     *
     * @param title - the title of the mission.
     */
    String title, /**
     * The Description of the mission
     */
    description, /**
     * The E -  the finish time of the mission
     */
    e, /**
     * The Color that was chosen for the mission.
     */
    color;
    /**
     * Saves the list of links of the images that the user chose to add.
     * (this list will be in Camera or Gallery activity too in order to save the links directly from there).
     */
    public static ArrayList<String> imagesLinks = new ArrayList<String>();
    /**
     * @param imageNames - saves the name of each picture.
     *                   (this list will be in Camera or Gallery activity too in order to save the names directly from there).
     */
    public static ArrayList<String> imagesNames = new ArrayList<String>();
    /**
     * Adapter for the spinner of the categories.
     */
    ArrayAdapter<String> adp;
    /**
     * the activity adds the current date and time the mission was created and sorts with is in the firebase
     */
    String currentDate;
    /**
     * in order to use the schedules notification i learned online
     */
    private ActivityMainBinding binding;
    /**
     * for the custom toolbar in the activity
     */
    Toolbar tb;
    /**
     * to identify the right activity the user got here from.
     */
    int p;
    /**
     * The Color pos.
     *
     * @param colorPos - the index of the chosen color in the spinner.
     */
    int colorPos, pos;
    /**
     * in order to set the datepicker and the timepicker to the time that the user chose.
     */
    int year, month, day, hour, minute;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        t=(EditText) findViewById(R.id.t);
        des=(EditText) findViewById(R.id.des);
        catS=(Spinner) findViewById(R.id.catS);
        linksNames =(ListView) findViewById(R.id.links);

        iGroup=(RadioGroup) findViewById(R.id.iGroup);
        i0=(RadioButton) findViewById(R.id.i0);
        i1=(RadioButton) findViewById(R.id.i1);
        i2= (RadioButton) findViewById(R.id.i2);

        times = (TextView) findViewById(R.id.times);

        colors = (Spinner) findViewById(R.id.colors);


        ColorAdapter adpS = new ColorAdapter(this,colorsName, names);
        colors.setAdapter(adpS);
        colors.setOnItemSelectedListener(this);


        tb = (Toolbar) findViewById(R.id.tb_cm);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        linksNames.setOnItemClickListener(CreateMission.this);
        linksNames.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(linksNames);

        catS.setOnItemSelectedListener(this);

        FirebaseUser fbuser = reAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query q = refDB.orderByChild("uID").equalTo(uid);
        q.addListenerForSingleValueEvent(VEL);


        createNotificationChannel();

        binding.savemission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(t.getText().toString().equals("")){
                    Toast.makeText(CreateMission.this, "You must have a title", Toast.LENGTH_SHORT).show();
                }

                else {
                    title = t.getText().toString();
                    description = des.getText().toString();

                    long time = getTime();
                    Date date = new Date(time);

                    DateFormat dateFormate = android.text.format.DateFormat.getLongDateFormat(getApplicationContext());
                    DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
                    String s  = times.getText().toString();
                    e = dateFormate.format(date) + "--" + timeFormat.format(time);
                    Mission m;
                    if(p ==2 && !s.equals(e)){
                        int index = s.indexOf(", Ends at:");
                        s = s.substring(index+(", Ends at:").length());

                        m = new Mission(title, importance, description, currentDate, s, category, color);
                        m.setimagesLinks(imagesLinks);
                        m.setImagesNames(imagesNames);
                        Log.i("CM","mission updated");
                        startActivity(new Intent(CreateMission.this, CheckList.class));
                        finish();
                    }
                    else{
                        scheduleNotification();
                        /*adds a mission to the total missions for the Information page*/

                        all = all +1;

                        User u0 = new User(Information.name, Information.email, Information.phone, Information.uid,Information.profilelink,true);
                        u0.setAll(all);
                        u0.setComplete(complete);
                        u0.setCategory(c);
                        refDB.child(Information.uid).setValue(u0);
                        //if this is a mission, the notification doesn't need to be blocked.
                        status = false;

                        m = new Mission(title, importance, description, currentDate, e, category,color);


                        SharedPreferences settingsi = getSharedPreferences("INFO_NOTIF", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settingsi.edit();
                        editor.putString("title", title);
                        editor.putString("start", currentDate);
                        editor.putString("end", e);
                        editor.commit();


                        m.setimagesLinks(imagesLinks);
                        m.setImagesNames(imagesNames);
                    }
                    if (check == 1) {
                        SharedPreferences settings = getSharedPreferences("missionInfo", MODE_PRIVATE);
                        if (title != settings.getString("title", "a")) {
                            refDBUC.child(settings.getString("title", "a")).removeValue();
                        }
                    }
                    refDBUC.child(title).setValue(m);
/*
restarts the page to its original start after the mission is saved.
 */
                    t.setText("");
                    des.setText("");
                    catS.setSelection(0);
                    colors.setSelection(0);
                    iGroup.clearCheck();
                    imagesLinks.clear();
                    imagesLinks.add("Links");
                    imagesNames.clear();
                    imagesNames.add("images->");
                    times.setText("...");

                    Log.d("success", "Mission Uploaded");
                }
            }
        });

        //the user will create a mission right now so we need the time and date for the present time
        Date currentTime = java.util.Calendar.getInstance().getTime();
        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(getApplicationContext());
        DateFormat timeFormat =  android.text.format.DateFormat.getTimeFormat(getApplicationContext());

        currentDate = dateFormat.format(currentTime)+ "--" + timeFormat.format(currentTime);
    }


    /**
     * reading all the categories from the current user's root in the FireBase
     */
    ValueEventListener VEL = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dS) {
            if (dS.exists()) {
                for(DataSnapshot data : dS.getChildren()) {
                    user = data.getValue(User.class);
                    c= user.getCategory();
                    name=user.getName();
                    phone= user.getPhone();
                    uID= user.getuID();
                    email= user.getEmail();
                    a= user.getActive();
                }

                if (c.isEmpty()){
                    c.add("category->");
                }
                adp = new ArrayAdapter<String>(CreateMission.this,
                        androidx.preference.R.layout.support_simple_spinner_dropdown_item,
                        c);
                catS.setAdapter(adp);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };

    /**
     * the intent gets a check from Camera_or_gallery activity and from CheckList activity in order to know the
     * difference between them(from checkList activity it needs to brings and images Arraylist that was in the mission for a update.
     *
     * (90% of the code is the same except the images ArrayList part.)
     *
     * check=3 -> the user got back from Camera_or_Gallery Activity
     * check=2-> the user got back to update a mission from CheckList Activity
     * check = 4 -> the user got here to create a new mission
     */
    @Override
    protected void onResume() {
        super.onResume();

        Intent getI= getIntent();

        p = getI.getIntExtra("check", -1);
        if ((p == 2) || (p == 3)){
            /* came from CheckList activity- update mission
            * or from Camera of gallery activity*/
            SharedPreferences settings= getSharedPreferences("missionInfo", MODE_PRIVATE);
            t.setText(settings.getString("title", "a"));
            currentDate = settings.getString("start","000");
            String e = settings.getString("end","000");
            if(p == 2 && e.equals("Starts at:" + currentDate + ", Ends at:" + e)) {
                times.setText(e);
            }
            else{
                times.setText("Starts at:" + currentDate + ", Ends at:" + e);
            }
            switch (settings.getInt("importance", -1)){
                case 0: i0.setChecked(true);break;
                case 1: i1.setChecked(true); break;
                case 2: i2.setChecked(true); break;
                case 3: iGroup.clearCheck(); break;
            }
            //in order to get the right index of the chosen color
            int ci= 0;
            while (!settings.getString("color","-1").equals(colorsName[ci])){
                ci++;
            }
            int finalCi = ci;
            colors.post(new Runnable() {
                @Override
                public void run() {
                    colors.setSelection(finalCi);
                }
            });

            category = settings.getInt("category", -1);
            catS.post(new Runnable() {
                @Override
                public void run() {
                    catS.setSelection(category);
                }
            });

            /*loading the images' links from the file
            if (p == 2){
                imagesLinks.clear();
                int size = settings.getInt("ImagesLink_Size", 0);
                for (int i = 0; i < size; i++) {
                    imagesLinks.add(settings.getString("ImagesLink_" + i, null));
                }

                imagesNames.clear();
                size = settings.getInt("ImagesName_Size", 0);
                for (int i = 0; i < size; i++) {
                    imagesNames.add(settings.getString("ImagesName_" + i, null));
                }
            }

             */
            /*else{//if the user cane from camera or gallery.
                ArrayAdapter<String> adp = new ArrayAdapter<String>(CreateMission.this,
                        androidx.preference.R.layout.support_simple_spinner_dropdown_item, imagesNames);
                linksNames.setAdapter(adp);
            }

             */

            des.setText(settings.getString("description","fff"));
/*
@param months- the list of months that was used in the CreateEvent Activity.
 */
            month = 0;
            while(!(month < 12 && e.startsWith(months[month]))){
                month++;
            }
            month= month + 1 ;
            e = e.substring(months[month-1].length()+1);

            int p = e.indexOf(", ");
            int dot = e.indexOf("--");
            day = Integer.parseInt(e.substring(0,p));
            year = Integer.parseInt(e.substring(p+2,dot));

            String time = e.substring(dot+2);

            int dote = time.indexOf(":");
            hour = Integer.parseInt(time.substring(0,dote));
            minute = Integer.parseInt(time.substring(dote+1));

            binding.dd.updateDate(year, month, day);

            binding.tt.setCurrentHour(hour);
            binding.tt.setCurrentMinute(minute);
        }
        else{//if the user came for a new mission
            imagesLinks.clear();
            imagesNames.clear();
            imagesLinks.add("links->");
            imagesNames.add("images->");
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(CreateMission.this,
                androidx.preference.R.layout.support_simple_spinner_dropdown_item, imagesNames);
        linksNames.setAdapter(adp);
    }

    /**
     * An OnClick for the fab button.
     *
     * @param view the view
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addPicture(View view)
    {
        SharedPreferences setting = getSharedPreferences("missionInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();

        editor.putString("title", t.getText().toString());
        editor.putString("start", currentDate);
        long time = getTime();
        Date date = new Date(time);

        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(getApplicationContext());
        DateFormat timeFormat =  android.text.format.DateFormat.getTimeFormat(getApplicationContext());

        e = dateFormat.format(date) + "--" + timeFormat.format(time);
        editor.putString("end", e);

        editor.putInt("importance", importance);
        editor.putInt("category", category);
        editor.putString("description", des.getText().toString());
        editor.putString("color", color);
        editor.commit();

        Intent pic1 = new Intent(CreateMission.this, Camera_or_Gallery.class);
        pic1.putExtra("page", 1);
        startActivity(pic1);
    }

    /**
     *  collects the number of the category the user chose.
     * @param par
     * @param v
     * @param pos
     * @param rowid
     */
    @Override
    public void onItemSelected(AdapterView<?> par, View v, int pos, long rowid)
    {
        switch(par.getId()){
            case R.id.catS:
                if (!(category != 0)) {
                    category = pos;
                }
                break;
            case R.id.colors:
                color = colorsName[pos];
                colorPos = pos;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    /**
     * the user can add a category to the mission.
     *
     * @param view the view
     */
    public void showCustomDialog(View view) {
        final Dialog dialog = new Dialog(CreateMission.this);
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

                n= nameC.getText().toString();
                c.add(n);

                ArrayAdapter<String> adp2= new ArrayAdapter<String>(CreateMission.this
                        , androidx.preference.R.layout.support_simple_spinner_dropdown_item
                        ,c);
                catS.setAdapter(adp2);

                User newU= new User(name,email,phone,uID,Information.profilelink,a);
                newU.setCategory(c);
                newU.setComplete(complete);
                newU.setAll(all);
                Information.category.add(n);
                refDB.child(uID).setValue(newU);


                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * shows the image for the user in a dialog.(need fixing???)
     * @param per
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> per, View view, int position, long l) {
        switch(per.getId()){
            case R.id.colors:
                color = colorsName[position];
                break;
            case R.id.links:
                if(pos == 0){
                    Toast.makeText(this, "Wrong choose", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * creates options for the list of images: to delete, to see the picture or cancel the menu.
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId() == R.id.links) {

            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String s = (String) lv.getItemAtPosition(acmi.position);

            int i = 0;
            while ((i < imagesNames.size()) && (!s.equals(imagesNames.get(i)))) {
                i++;
            }
            pos = i;
        }

        menu.add("Delete Image");
        menu.add("Show Image");
        menu.add("Cancel");
    }

    /**
     * gives a reaction to every press: deletes the chosen picture,
     * shows the user the picture or cancels the menu.
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getTitle().toString()){
            case "Delete Image":
                imagesNames.remove(pos+1);

                StorageReference storageRef = storage.getReference();
                StorageReference photoRef = storageRef.child(imagesLinks.get(pos+1));

                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateMission.this, "deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(CreateMission.this, "problem", Toast.LENGTH_SHORT).show();
                    }
                });
                imagesLinks.remove(pos+1);
                ArrayAdapter<String> adp = new ArrayAdapter<String>(CreateMission.this,
                        androidx.preference.R.layout.support_simple_spinner_dropdown_item, imagesNames);
                linksNames.setAdapter(adp);
                break;
            case "Show Image":
                final Dialog picture = new Dialog(CreateMission.this);

                picture.setCancelable(true);
                picture.setContentView(R.layout.show_picture);

                ImageView showpic = (ImageView) picture.findViewById(R.id.showpic1);
                Button cancel = (Button) picture.findViewById(R.id.cancel);
                TextView name = (TextView) picture.findViewById(R.id.name);

                storageRef = storage.getReference();

                StorageReference imageRef = storageRef.child(imagesLinks.get(pos));

                final long ONE_MEGABYTE = 3150 * 3150;

                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        showpic.setImageBitmap(bMap);
                        name.setText(imagesNames.get(pos));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateMission.this, "not Working.", Toast.LENGTH_SHORT).show();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        picture.cancel();
                    }
                });
                picture.show();

                break;
            case "Cancel":
                closeContextMenu();
                break;
        }
        return true;
    }

    /**
     * 0.
     *
     * @param view the view
     */
    public void i0(View view) {
        importance=0;
    }

    /**
     * 1.
     *
     * @param view the view
     */
    public void i1(View view) {
        importance=1;
    }

    /**
     * 2.
     *
     * @param view the view
     */
    public void i2(View view) {
        importance=2;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scheduleNotification()
    {
        Intent intent = new Intent(getApplicationContext(), Notification.class);
        String title = binding.t.getText().toString();
        String message = "Tap check if finished!";

        intent.putExtra(titleExtra, title);
        intent.putExtra(messageExtra, message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long time = getTime();

        alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
        );

        showAlert(time, title, message);
    }

    /**
     * shows an Alert for the chosen time and date shows the user an notification was scheduled.
     * @param time
     * @param title
     * @param message
     */
    private void showAlert(long time, String title, String message) {
        Date date = new Date(time);
        DateFormat dateFormate = android.text.format.DateFormat.getLongDateFormat(getApplicationContext());
        DateFormat timeFormat =  android.text.format.DateFormat.getTimeFormat(getApplicationContext());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("Notification Scheduled✅")
                .setMessage("Title: " + title +
                        "\nDescription: " + description +
                        "\nAt: " + dateFormate.format(date) + " " + timeFormat.format(time))
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog ad = alertDialog.create();
        ad.show();
    }

    /**
     * gets the time and date from the activity
     *
     * @return time
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public long getTime()
    {
        int minute = binding.tt.getMinute();
        int hour = binding.tt.getHour();
        int day = binding.dd.getDayOfMonth();
        int month =  binding.dd.getMonth();
        int year = binding.dd.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTimeInMillis();
    }

    /**
     * creates a channel for the notifications.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel()
    {
        String name =  "Notification Channel";
        String desc = "A Description Of The Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelID, name , importance);
        channel.setDescription(desc);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i;
        switch(item.getItemId()){
            case R.id.i:
                i = new Intent(this, About.class);
                startActivity(i);
                break;
            case R.id.cm:
                break;
            case R.id.cl:
                i= new Intent(this, CheckList.class);
                startActivity(i);
                break;
            case R.id.c:
                i= new Intent(this, CalendarView.class);
                startActivity(i);
                break;
            case R.id.tblock:
                i= new Intent(this, TimerBlock.class);
                startActivity(i);
                break;
            case R.id.ui:
                i= new Intent(this, Information.class);
                startActivity(i);
                break;
        }
        return true;
    }

}