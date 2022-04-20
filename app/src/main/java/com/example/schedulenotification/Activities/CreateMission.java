package com.example.schedulenotification.Activities;

import static android.content.ContentValues.TAG;
import static com.example.schedulenotification.Classes.Notification.channelID;
import static com.example.schedulenotification.Classes.Notification.messageExtra;
import static com.example.schedulenotification.Classes.Notification.notificationID;
import static com.example.schedulenotification.Classes.Notification.titleExtra;
import static com.example.schedulenotification.refFB.database;
import static com.example.schedulenotification.refFB.reAuth;
import static com.example.schedulenotification.refFB.refDB;
import static com.example.schedulenotification.refFB.storage;

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
import android.util.Log;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * An activity to create a mission, to review one and to it creates a notification whe the mission in finished
 */
public class CreateMission extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener{

    public static DatabaseReference refDBUC= database.getReference(
            "Mission/"+ reAuth.getCurrentUser().getUid() +"/uncompleted");

    public static DatabaseReference refDBC= database.getReference(
            "Mission/"+ reAuth.getCurrentUser().getUid() +"/completed");

    EditText t, des;
    Spinner catS;
    ListView links;
    RadioButton i0,i1,i2;
    RadioGroup iGroup;
    ListView colors;
    /**
     * times- for the start of a mission and the end of one
     */
    TextView times;
    /**
     * string array of the colors
     */
    String[] colorsName = { "#FFE3D4", "#ECD5E3","#8FCACA","#ECEAE4", "#FFBF91", "#D4DD479", "#CCE2CB", "#FFFFB5"
            , "#97C1A9", "#D7EFEF", "#FCB9AA", "#C6DBDA", "#F6EAC2", "#E67C73"};

    User user;
    /**
     * uid - takes the users id from firebase auth.
     * n - the category the user adds
     */
    String uid,n;
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
    ArrayList<String> c;
    boolean a;

    /**
     * params for a new Mission.
     * @param- oD- opendate
     * @paran- dD- dueDate
     * @param- oT- openTime
     * @param- dT- dueTime
     * @param- importance:
     *          0-very important
     *          1-less important
     *          2-not important
     */
    int category;
    /**
     * for a check in when the user goes between activities and he didn't press any importance "RadioButtons"
     */
    int importance=3;
    String title, description, e, color;
    ArrayList<String> images= new ArrayList<String>();
    /**
     * if the user went to another activity to upload a picture- true
     * else- false
     */
    boolean status= false;
    /**
     * Adapter for the spinner.
     */
    ArrayAdapter<String> adp;
    /**
     * Adapter for listview- for the links of the images.
     */
    ArrayAdapter<String> adpLinks;

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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        t=(EditText) findViewById(R.id.t);
        des=(EditText) findViewById(R.id.des);
        catS=(Spinner) findViewById(R.id.catS);
        links=(ListView) findViewById(R.id.links);

        iGroup=(RadioGroup) findViewById(R.id.iGroup);
        i0=(RadioButton) findViewById(R.id.i0);
        i1=(RadioButton) findViewById(R.id.i1);
        i2= (RadioButton) findViewById(R.id.i2);

        times = (TextView) findViewById(R.id.times);

        //colors for the list of colors-> check the custom listView
        colors = (ListView) findViewById(R.id.colors);
        colors.setOnItemClickListener(this);
        colors.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,colorsName);
        colors.setAdapter(adp);

        tb = (Toolbar) findViewById(R.id.tb_cm);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        links.setOnItemClickListener(CreateMission.this);
        links.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        catS.setOnItemSelectedListener(this);

        FirebaseUser fbuser = reAuth.getCurrentUser();
        uid = fbuser.getUid();
        Query q = refDB.orderByChild("uID").equalTo(uid);
        q.addListenerForSingleValueEvent(VEL);

        images.add("images->");


        createNotificationChannel();

        binding.savemission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleNotification();

                if(t.getText().toString().equals("")){
                    Toast.makeText(CreateMission.this, "You must have a title", Toast.LENGTH_SHORT).show();
                }

                else{
                    title= t.getText().toString();
                    description= des.getText().toString();

                    long time = getTime();
                    Date date = new Date(time);

                    DateFormat dateFormate = android.text.format.DateFormat.getLongDateFormat(getApplicationContext());
                    DateFormat timeFormat =  android.text.format.DateFormat.getTimeFormat(getApplicationContext());

                    e = dateFormate.format(date) + "--" + timeFormat.format(time);


                    Mission m= new Mission(title, importance, description, currentDate, e, category);
                    m.setimages(images);
                    if(check == 1){
                        SharedPreferences settings= getSharedPreferences("missionInfo", MODE_PRIVATE);
                        if(title!=settings.getString("title", "a")){
                            refDBUC.child(settings.getString("title", "a")).removeValue();
                        }
                    }
                    refDBUC.child(title).setValue(m);

                    t.setText("");
                    des.setText("");
                    catS.setSelection(0);
                    iGroup.clearCheck();
                    images.clear();
                    images.add("images");

                    Log.d("success","Mission Uploaded");
                    //Toast.makeText(this, "Your mission has been uploaded😊", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(getIntent().getIntExtra("check", 0) == 1) {
            SharedPreferences settings = getSharedPreferences("INFO", MODE_PRIVATE);
            binding.t.setText(settings.getString("title", "aaa"));
            binding.des.setText(settings.getString("description", "bbb"));
        }

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
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
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
     * check=0 -> the user got back from Camera_or_Gallery Activity
     * check=1-> the user got back to update a mission from CheckList Activity
     */
    @Override
    protected void onResume() {
        super.onResume();

        Intent getI= getIntent();
        if(getI.getIntExtra("path",-1) == 1){
            
        }
        status=getI.getBooleanExtra("status",false);
        check= getI.getIntExtra("check", 3);
        if(status){
            if(!images.contains(getI.getStringExtra("way"))){
                images.add(getI.getStringExtra("way"));
            }

            SharedPreferences settings= getSharedPreferences("missionInfo", MODE_PRIVATE);
//the user went from checklist to here, so the mission might not be in the Shared Preference file
            if(check == 1){
                Query q = refDBUC.orderByChild("title").equalTo(settings.getString("title", "a"));
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot data : snapshot.getChildren()) {
                                Mission m = data.getValue(Mission.class);
                                //sets the title
                                t.setText(m.getTitle());
                                String e = m.getDueDate();
                                String s = m.getOpenDate();
                                //sets the start and end date.
                                times.setText("Starts at:" + s + ", Ends at:" + e);
                                //sets the importance
                                switch(m.getImportance()){
                                    case 0:
                                        i0.setChecked(true); break;
                                    case 1:
                                        i1.setChecked(true); break;
                                    case 2:
                                        i2.setChecked(true);break;
                                }
                                //sets the description
                                des.setText(m.getDescription());
                                //sets the images
                                adpLinks= new ArrayAdapter<String>(CreateMission.this,
                                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                        m.getImages());
                                links.setAdapter(adpLinks);
                                //sets the catogory
                                catS.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        catS.setSelection(m.getCategory());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                images = getI.getStringArrayListExtra("images");
            }
            //got back from Camera or Gallery activity
            else{
                t.setText(settings.getString("title", "a"));
                currentDate = settings.getString("start","000");
                String e = settings.getString("end","000");
                times.setText("Starts at:" + currentDate + ", Ends at:" + e);
                switch (settings.getInt("importance", -1)){
                    case 0: i0.setChecked(true);break;
                    case 1: i1.setChecked(true); break;
                    case 2: i2.setChecked(true); break;
                    case 3: iGroup.clearCheck(); break;
                }
                catS.post(new Runnable() {
                    @Override
                    public void run() {
                        catS.setSelection(settings.getInt("category", -1));
                    }
                });

                des.setText(settings.getString("description","fff"));
                images = getIntent().getStringArrayListExtra("way");
            }
            //if the user got here from Camera or Gallery activity
        }
        //images.add(getIntent().getStringExtra("way"));
        adpLinks= new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, images);
        links.setAdapter(adpLinks);
    }

    /**
     * An OnClick for the fab button.
     * @param view
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

        e = dateFormat.format(date) + " " + timeFormat.format(time);
        editor.putString("end", e);

        editor.putInt("importance", importance);
        editor.putInt("category", category);
        editor.putString("description", des.getText().toString());
        editor.commit();

        startActivity(new Intent(CreateMission.this, Camera_or_Gallery.class));
    }

    /**
     *  collects the number of the category the user chose.
     * @param parent
     * @param v
     * @param pos
     * @param rowid
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int pos, long rowid)
    {
        if(!(category!=0)) {
            category = pos;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    /**
     * the user can add a category to the mission.
     * @param view
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
                        , androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                        ,c);
                catS.setAdapter(adp2);

                User newU= new User(name,email,phone,uID,a);
                newU.setCategory(c);
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
     * @param pos
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> per, View view, int pos, long l) {
        switch(per.getId()){
            case R.id.colors:
                color = colorsName[pos];
                break;
            case R.id.links:
                if(pos == 0){
                    Toast.makeText(this, "Wrong choose", Toast.LENGTH_SHORT).show();
                }
                else {
                    final Dialog picture = new Dialog(CreateMission.this);

                    picture.setCancelable(true);
                    picture.setContentView(R.layout.show_picture);

                    ImageView showpic = (ImageView) picture.findViewById(R.id.showpic1);
                    Button cancel = (Button) picture.findViewById(R.id.cancel);

                    StorageReference storageRef = storage.getReference();

                    StorageReference imageRef = storageRef.child(images.get(pos));

                    final long ONE_MEGABYTE = 1024 * 1024;

                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            showpic.setImageBitmap(bMap);
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
                }
                break;
        }
    }

    public void i0(View view) {
        importance=0;
    }

    public void i1(View view) {
        importance=1;
    }

    public void i2(View view) { importance=2; }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scheduleNotification()
    {
        Intent intent = new Intent(getApplicationContext(), Notification.class);
        String title = binding.t.getText().toString();
        String message = binding.des.getText().toString();

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
        SharedPreferences settings = getSharedPreferences("INFO",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString("title", title);
        editor.putString("description", message);
        editor.commit();
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
                .setTitle("Notification Scheduled")
                .setMessage("Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormate.format(date) + " " + timeFormat.format(time))
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CreateMission.this, "Works!😁💻", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog ad = alertDialog.create();
        ad.show();
    }

    /**
     * gets the time and date from the activity
     * @return
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
        getMenuInflater().inflate(R.menu.info, menu);

        menu.add("Check List📃");
        menu.add("Calendar📅");
        menu.add("Focus Timer⏱️");
        menu.add("User's Information🔎");

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
            case "User's Information🔎":
                i= new Intent(this, Information.class);
                startActivity(i);
                break;
        }
        return true;
    }

}