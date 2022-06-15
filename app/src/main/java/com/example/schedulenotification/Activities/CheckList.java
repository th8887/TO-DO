package com.example.schedulenotification.Activities;

import static com.example.schedulenotification.Activities.CreateMission.imagesLinks;
import static com.example.schedulenotification.Activities.CreateMission.imagesNames;
import static com.example.schedulenotification.Activities.CreateMission.refDBC;
import static com.example.schedulenotification.Activities.CreateMission.refDBUC;
import static com.example.schedulenotification.Activities.Information.all;
import static com.example.schedulenotification.Activities.Information.complete;
import static com.example.schedulenotification.refFB.refDB;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
import com.example.schedulenotification.Classes.Mission;
import com.example.schedulenotification.Classes.User;
import com.example.schedulenotification.R;
import com.example.schedulenotification.databinding.ActivityCheckListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * @author		Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version	beta
 * @since		1/10/2021
 * shows the uncompleted mission, gives an opportunity to prioritize them, to see the completed ones
 *and to create new ones.
 */
public class CheckList extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {

    ListView titles;

    ArrayList<Spannable> title_Mission= new ArrayList<Spannable>();
    public static ArrayList<Mission> missions= new ArrayList<Mission>();

    Mission m;
    Toolbar tb;
    /*
    if the user sorts to uncompleted the menu will be the usual, other it will change.
     */
    boolean b = false;

    private ActivityCheckListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        binding = ActivityCheckListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        titles=(ListView) findViewById(R.id.titles);

        tb = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CheckList.this, CreateMission.class);
                i.putExtra("check", 4);
                startActivity(i);
            }
        });


        titles.setOnItemClickListener(this);
        titles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(titles);

        int mission = getIntent().getIntExtra("check",-1);

        refDBUC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                title_Mission.clear();
                missions.clear();
                ArrayList<Spannable> title0 = new ArrayList<Spannable>();
                for (DataSnapshot data : dS.getChildren()) {
                    Mission mTmp = data.getValue(Mission.class);
                        SharedPreferences settingsi = getSharedPreferences("INFO_NOTIF",MODE_PRIVATE);
                        if(mTmp.getTitle().equals(settingsi.getString("title","aaa"))&& mission == 1){
                            complete = complete + 1;
                            m = mTmp;
                            refDBUC.child(m.getTitle()).removeValue();
                            m.setActive(true);
                            refDBC.child(m.getTitle()).setValue(m);

                            int i=0;
                            while(i<missions.size()&& !m.equals(missions.get(i))){
                                i++;
                            }

                            //Toast.makeText(CheckList.this, "Works!😁", Toast.LENGTH_SHORT).show();
                        }
                    else {
                        Spannable WordtoSpan = new SpannableString("● "+ mTmp.getTitle());
                        if (mTmp.getColor() == null){
                            WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        else {
                            WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor(mTmp.getColor())), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        title_Mission.add(WordtoSpan);
                        missions.add(mTmp);
                    }
                }
                ArrayAdapter<Spannable> adp = new ArrayAdapter<Spannable>(CheckList.this,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                titles.setAdapter(adp);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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

        if (v.getId() == R.id.titles) {

            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Spannable s = (Spannable) lv.getItemAtPosition(acmi.position);

            int i = 0;
            while ((i < title_Mission.size()) && (!s.equals(title_Mission.get(i)))) {
                i++;
            }
            m = missions.get(i);
        }

        if(!b){

            menu.add("Check Mission");
            menu.add("Update Mission");
            menu.add("Delete Mission");
            menu.add("Cancel");
        }
        else{
            menu.add("Un-Check Mission");
            menu.add("Delete Mission");
            menu.add("Cancel");
        }
    }

    /**
     * check mission- deletes the mission from uncompleted root, updates the activity
     * to true(the mission is done), and adds the mission into completed root in Firebase.
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ArrayAdapter<Spannable> adp;
        int i;

        User u0;
//true- came from completed list
        //false- came from uncompleted list
        if (!b) {
            switch (item.getTitle().toString()) {
                case "Check Mission":
                    /*adds the completed mission to the number of total completed missions*/
                    complete = complete + 1;

                    u0 = new User(Information.name, Information.email, Information.phone, Information.uid,Information.profilelink, true);
                    u0.setComplete(complete);
                    u0.setAll(all);
                    u0.setCategory(Information.category);
                    refDB.child(Information.uid).setValue(u0);

                    refDBUC.child(m.getTitle()).removeValue();
                    m.setActive(true);
                    refDBC.child(m.getTitle()).setValue(m);

                    i = 0;
                    while (i < missions.size() && !m.equals(missions.get(i))) {
                        i++;
                    }
                    missions.remove(i);
                    title_Mission.remove(i);

                    adp = new ArrayAdapter<Spannable>(CheckList.this,
                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                    titles.setAdapter(adp);

                    break;
                case "Update Mission":
                    Intent si = new Intent(CheckList.this, CreateMission.class);

                    si.putExtra("status", true);
                    si.putExtra("check", 2);
                    //si.putExtra("images", m.getImages());

                    SharedPreferences setting = getSharedPreferences("missionInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();

                    editor.putString("title", m.getTitle());
                    editor.putString("start", m.getOpenDate());
                    editor.putString("end", m.getDueDate());
                    editor.putInt("importance", m.getImportance());
                    editor.putInt("category", m.getCategory());
                    editor.putString("description", m.getDescription());
                    editor.putString("color", m.getColor());
                    imagesLinks = m.getImagesLinks();
                    imagesNames = m.getImagesNames();

                    editor.commit();

                    startActivity(si);
                    finish();
                    break;
                case "Delete Mission":
                    refDBUC.child(m.getTitle()).removeValue();

                    all = all -1;

                    u0 = new User(Information.name, Information.email, Information.phone, Information.uid,Information.profilelink,true);
                    u0.setAll(all);
                    u0.setComplete(complete);
                    u0.setCategory(Information.category);
                    refDB.child(Information.uid).setValue(u0);


                    i = 0;
                    while (i < missions.size() && !m.equals(missions.get(i))) {
                        i++;
                    }
                    missions.remove(i);
                    title_Mission.remove(i);


                    adp = new ArrayAdapter<Spannable>(CheckList.this,
                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                    titles.setAdapter(adp);


                    break;
                case "Cancel":
                    closeContextMenu();
                    break;
            }

        }
        else{
            //In the checked missions list
            switch(item.getTitle().toString()) {
                case "Un-Check Mission":
                    /*minus the completed mission to the number of total completed missions*/
                    complete = complete - 1;

                    u0 = new User(Information.name, Information.email, Information.phone, Information.uid,Information.profilelink, true);
                    u0.setComplete(complete);
                    u0.setAll(all);
                    u0.setCategory(Information.category);
                    refDB.child(Information.uid).setValue(u0);

                    refDBC.child(m.getTitle()).removeValue();
                    m.setActive(true);
                    refDBUC.child(m.getTitle()).setValue(m);

                    i = 0;
                    while (i < missions.size() && !m.equals(missions.get(i))) {
                        i++;
                    }
                    missions.remove(i);
                    title_Mission.remove(i);

                    adp = new ArrayAdapter<Spannable>(CheckList.this,
                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                    titles.setAdapter(adp);

                    break;
                case "Delete Mission":
                    refDBC.child(m.getTitle()).removeValue();

                    all = all - 1;
                    complete = complete - 1;

                    u0 = new User(Information.name, Information.email, Information.phone, Information.uid,Information.profilelink,true);
                    u0.setComplete(complete);
                    u0.setAll(all);
                    u0.setCategory(Information.category);
                    refDB.child(Information.uid).setValue(u0);


                    i = 0;
                    while (i < missions.size() && !m.equals(missions.get(i))) {
                        i++;
                    }
                    missions.remove(i);
                    title_Mission.remove(i);

                    adp = new ArrayAdapter<Spannable>(CheckList.this,
                            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                    titles.setAdapter(adp);

                    break;
                case "Cancel":
                    closeContextMenu();
                    break;
            }
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> par,
                            View v, int pos, long l) {
        m= missions.get(pos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menuchecklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i;
        switch(item.getItemId()){
            case R.id.i:
                i = new Intent(this, About.class);
                startActivity(i);
                finish();
                break;
            case R.id.complete:
                b = true;
                refDBC.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        title_Mission.clear();
                        missions.clear();
                        for (DataSnapshot data : dS.getChildren()) {
                            Mission mTmp = data.getValue(Mission.class);

                            Spannable WordtoSpan = new SpannableString("● "+ mTmp.getTitle());
                            if (mTmp.getColor() == null){
                                WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            else {
                                WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor(mTmp.getColor())), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            title_Mission.add(WordtoSpan);
                            missions.add(mTmp);
                        }

                        ArrayAdapter<Spannable> adp = new ArrayAdapter<Spannable>(CheckList.this,
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                        titles.setAdapter(adp);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                break;
            case R.id.priority:
                if(!b) {
                    Query q = refDBUC.orderByChild("importance");
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dS) {
                            title_Mission.clear();
                            missions.clear();
                            for (DataSnapshot data : dS.getChildren()) {
                                Mission mTmp = data.getValue(Mission.class);

                                Spannable WordtoSpan = new SpannableString("● " + mTmp.getTitle());
                                if (mTmp.getColor() == null) {
                                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                } else {
                                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor(mTmp.getColor())), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                title_Mission.add(WordtoSpan);
                                missions.add(mTmp);
                            }

                            ArrayAdapter<Spannable> adp = new ArrayAdapter<Spannable>(CheckList.this,
                                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                            titles.setAdapter(adp);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else{
                    Query q = refDBC.orderByChild("importance");
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dS) {
                            title_Mission.clear();
                            missions.clear();
                            for (DataSnapshot data : dS.getChildren()) {
                                Mission mTmp = data.getValue(Mission.class);

                                Spannable WordtoSpan = new SpannableString("● " + mTmp.getTitle());
                                if (mTmp.getColor() == null) {
                                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                } else {
                                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor(mTmp.getColor())), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                title_Mission.add(WordtoSpan);
                                missions.add(mTmp);
                            }

                            ArrayAdapter<Spannable> adp = new ArrayAdapter<Spannable>(CheckList.this,
                                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                            titles.setAdapter(adp);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                break;
            case R.id.uncomplete:
                b = false;
                refDBUC.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        title_Mission.clear();
                        missions.clear();
                        for (DataSnapshot data : dS.getChildren()) {
                            Mission mTmp = data.getValue(Mission.class);

                            Spannable WordtoSpan = new SpannableString("● "+ mTmp.getTitle());
                            if (mTmp.getColor() == null){
                                WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            else {
                                WordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor(mTmp.getColor())), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            title_Mission.add(WordtoSpan);
                            missions.add(mTmp);
                        }

                        ArrayAdapter<Spannable> adp = new ArrayAdapter<Spannable>(CheckList.this,
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                        titles.setAdapter(adp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
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