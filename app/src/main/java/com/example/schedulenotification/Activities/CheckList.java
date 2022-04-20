package com.example.schedulenotification.Activities;

import static com.example.schedulenotification.Activities.CreateMission.refDBC;
import static com.example.schedulenotification.Activities.CreateMission.refDBUC;
import static com.example.schedulenotification.refFB.reAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
import com.example.schedulenotification.Classes.Mission;
import com.example.schedulenotification.R;
import com.example.schedulenotification.databinding.ActivityCheckListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckList extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {

    ListView titles;

    ArrayList<String> title_Mission= new ArrayList<String>();
    public static ArrayList<Mission> missions= new ArrayList<Mission>();

    Mission m;
    Toolbar tb;

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
                i.putExtra("path", 1);
                startActivity(i);
            }
        });


        titles.setOnItemClickListener(this);
        titles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        titles.setOnCreateContextMenuListener(this);

        refDBUC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                title_Mission.clear();
                missions.clear();
                for (DataSnapshot data : dS.getChildren()) {
                    Mission mTmp = data.getValue(Mission.class);
                    title_Mission.add(mTmp.getTitle());
                    missions.add(mTmp);
                }
                ArrayAdapter<String> adp = new ArrayAdapter<String>(CheckList.this,
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
            String s = (String) lv.getItemAtPosition(acmi.position);

            int i = 0;
            while ((i < title_Mission.size()) && (!s.equals(title_Mission.get(i)))) {
                i++;
            }
            m = missions.get(i);
        }
            menu.add("Check Mission");
            menu.add("Update Mission");
            menu.add("Delete Mission");
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
        String oper = item.getTitle().toString();
        if (oper.equals("Check Mission")) {
            refDBUC.child(m.getTitle()).removeValue();
            m.setActive(true);
            refDBC.child(m.getTitle()).setValue(m);

            int i=0;
            while(i<missions.size()&& !m.equals(missions.get(i))){
                i++;
            }
            missions.remove(i);
            title_Mission.remove(i);
            ArrayAdapter <String> adp= new ArrayAdapter<String>(CheckList.this,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
            titles.setAdapter(adp);
            Toast.makeText(this, "Works!😁", Toast.LENGTH_SHORT).show();
        }

        if(oper.equals("Update Mission")){
            Intent si= new Intent(CheckList.this, CreateMission.class);

            si.putExtra("status", true);
            si.putExtra("check", 1);
            si.putExtra("images", m.getImages());

            SharedPreferences setting = getSharedPreferences("missionInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = setting.edit();

            editor.putString("title",m.getTitle() );
            editor.putString("start",  m.getOpenDate());
            editor.putString("end", m.getDueDate());
            editor.putInt("importance",  m.getImportance());
            editor.putInt("category", m.getCategory());
            editor.putString("description", m.getDescription());
            //editor.putString("uID",reAuth.getCurrentUser().getUid());
            editor.commit();

            startActivity(si);
            finish();
        }

        if (oper.equals("Delete Mission")){
            refDBUC.child(m.getTitle()).removeValue();

            int i=0;
            while(i<missions.size()&& !m.equals(missions.get(i))){
                i++;
            }
            missions.remove(i);
            title_Mission.remove(i);
            ArrayAdapter <String> adp= new ArrayAdapter<String>(CheckList.this,
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
            titles.setAdapter(adp);
        }

        if(oper.equals("Cancel")){
            closeContextMenu();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> par, View v, int pos, long l) {
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
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();

                refDBC.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        title_Mission.clear();
                        missions.clear();
                        for (DataSnapshot data : dS.getChildren()) {
                            Mission mTmp = data.getValue(Mission.class);
                            title_Mission.add(mTmp.getTitle());
                            missions.add(mTmp);
                        }
                        ArrayAdapter<String> adp = new ArrayAdapter<String>(CheckList.this,
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                        titles.setAdapter(adp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                break;
            case R.id.priority:
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                Query q = refDBUC.orderByChild("importance");
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        title_Mission.clear();
                        missions.clear();
                        for (DataSnapshot data : dS.getChildren()) {
                            Mission mTmp = data.getValue(Mission.class);
                            title_Mission.add(mTmp.getTitle());
                            missions.add(mTmp);
                        }
                        ArrayAdapter<String> adp = new ArrayAdapter<String>(CheckList.this,
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title_Mission);
                        titles.setAdapter(adp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                break;
            case R.id.uncomplete:
                refDBUC.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        title_Mission.clear();
                        missions.clear();
                        for (DataSnapshot data : dS.getChildren()) {
                            Mission mTmp = data.getValue(Mission.class);
                            title_Mission.add(mTmp.getTitle());
                            missions.add(mTmp);
                        }
                        ArrayAdapter<String> adp = new ArrayAdapter<String>(CheckList.this,
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
            case R.id.ft:
                i= new Intent(this, TimerForFocus.class);
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