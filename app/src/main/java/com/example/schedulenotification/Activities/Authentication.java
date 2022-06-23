package com.example.schedulenotification.Activities;

import static com.example.schedulenotification.refFB.reAuth;
import static com.example.schedulenotification.refFB.refDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.R;
import com.example.schedulenotification.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Authentication.
 *
 * @author Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version beta
 * @since 1 /10/2021 creates a new user of logs in an old one.
 */
public class Authentication extends AppCompatActivity {

    /**
     * short names for editTexts:
     * n=name
     * p=phone
     * pa=password
     * e=email
     */
    EditText n, p, e, pa;
    /**
     * title- to change the title from log in to register.
     * sol- sign or log- TextView for sign in or log in in the bottom.
     */
    TextView title, sol;

    /**
     * ls- log in or register.
     */
    Button ls;

    /**
     * if the user wnats to stay connected.
     */
    CheckBox connected;

    boolean register, stayConnect;
    /**
     * In order to build an object from User Class.
     */
    String name,
    phone,
    password,
    email;


    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        title = (TextView) findViewById(R.id.tv_title);
        p = (EditText) findViewById(R.id.phone);
        n = (EditText) findViewById(R.id.name);
        ls = (Button) findViewById(R.id.ls);
        sol = (TextView) findViewById(R.id.tvin);
        e = (EditText) findViewById(R.id.email);
        pa = (EditText) findViewById(R.id.password);
        connected = (CheckBox) findViewById(R.id.cBstayconnect);

        tb = (Toolbar)findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        stayConnect = false;
        register = true;

        n.setVisibility(View.INVISIBLE);
        p.setVisibility(View.INVISIBLE);
        regOption();
    }

    /**
     * On activity start- checking if user already logged in.
     */
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        Intent si = new Intent(Authentication.this, Information.class);
        if (reAuth.getCurrentUser() != null && isChecked) {
            stayConnect = true;
            startActivity(si);
        }
    }

    /**
     * On activity Pause- If logged in & asked to be remembered- kill activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }

    /**
     * Switches from log in option to registration option if the user is new in the app.
     */
    public void regOption() {
        SpannableString ss = new SpannableString("Don't have an account?  Register here.");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                title.setText("Sign In : 📝");
                n.setVisibility(View.VISIBLE);
                p.setVisibility(View.VISIBLE);
                ls.setText("Sign in");
                register = false;
                logOption();
            }
        };
        ss.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sol.setText(ss);
        sol.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Switches back to log option from registration in case the user wants to log in.
     */
    private void logOption() {
        SpannableString ss = new SpannableString("Already have an account? login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                title.setText("Log In : 🖥️");
                n.setVisibility(View.INVISIBLE);
                p.setVisibility(View.INVISIBLE);
                ls.setText("Log In");
                register = true;
                regOption();
            }
        };
        ss.setSpan(span, 25, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sol.setText(ss);
        sol.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * this function checks if the user created a new user, or logged in with an old one.
     * If the user created a new account, then the program will create a new
     * object in Firebase Database with all the new information and a user in Firebase Authentication.
     * If the user logged in an old account, the program compares the entered information
     * to the data it has in Firebase Authentication.
     *
     * @param view the view
     */
    public void enter(View view) {
        //if the user is already signed-in and wants to enter the app.
        if(register){
            email=e.getText().toString();
            password=pa.getText().toString();

            if (TextUtils.isEmpty(email)){
                e.setError("Email Is Required.");
            }
            if (TextUtils.isEmpty(password)){
                pa.setError("Password Is Required.");
            }
            else {
                if (password.length() < 6) {
                    pa.setError("Password must be longer than 6 digits.");
                }
                final ProgressDialog pd=ProgressDialog.show(this,"Login","Connecting...",true);
                reAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=settings.edit();
                                    editor.putBoolean("stayConnect",connected.isChecked());
                                    editor.commit();
                                    Log.d("MainActivity", "signinUserWithEmail:success");
                                    Toast.makeText(Authentication.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    Intent si = new Intent(Authentication.this,Information.class);
                                    //si.putExtra("connected",connected.isChecked());
                                    startActivity(si);
                                } else {
                                    Log.d("MainActivity", "signinUserWithEmail:fail");
                                    Toast.makeText(Authentication.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }
        else{
            name=n.getText().toString();
            phone=p.getText().toString();
            email=e.getText().toString();
            password=pa.getText().toString();

            if (TextUtils.isEmpty(email)){
                e.setError("Email Is Required.");
            }
            if (TextUtils.isEmpty(name)){
                n.setError("Name Is Required.");
            }
            if(TextUtils.isEmpty(phone)){
                p.setError("Phone is Required.");
            }
            if (TextUtils.isEmpty(password) ){
                pa.setError("Password Is Required.");
            }
            else {
                if (password.length() < 6) {
                    pa.setError("Password must be longer than 6 digits.");
                }
                else if (!isValidMobileNo(phone)){
                    p.setError("please give a correct phone number");
                }
                else if (!email.endsWith("@gmail.com")){
                    e.setError("please enter a legal email");
                }
                else {
                    final ProgressDialog pd = ProgressDialog.show(this, "Register", "Registering...", true);
                    reAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pd.dismiss();
                                    if (task.isSuccessful()) {
                                        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putBoolean("stayConnect", connected.isChecked());
                                        editor.commit();

                                        SharedPreferences setting01 = getSharedPreferences("count", MODE_PRIVATE);
                                        SharedPreferences.Editor editor01 = setting01.edit();
                                        editor01.putInt("count", 0);
                                        editor01.commit();

                                        Log.d("MainActivity", "createUserWithEmail:success");
                                        User u = new User(name, email, phone, reAuth.getUid(), " ", true);
                                        refDB.child(reAuth.getUid()).setValue(u);

                                        Toast.makeText(Authentication.this, "Successful registration", Toast.LENGTH_SHORT).show();
                                        Intent si = new Intent(Authentication.this, Information.class);
                                        startActivity(si);
                                    } else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                            Toast.makeText(Authentication.this, "User with e-mail already exist!", Toast.LENGTH_SHORT).show();
                                        else {
                                            Log.w("MainActivity", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(Authentication.this, "User create failed.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
            }
        }
    }

    /**
     * checks if the phone number is correct.
     * @param str
     * @return
     */
    public static boolean isValidMobileNo(String str)
    {
        Pattern ptrn = Pattern.compile("(052|053|054|050)[0-9]{7}");
//the matcher() method creates a matcher that will match the given input against this pattern
        Matcher match = ptrn.matcher(str);
//returns a boolean value
        return (match.find() && match.group().equals(str));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.i) {
            Intent i = new Intent(this, About.class);
            i.putExtra("path", 1);
            startActivity(i);
            finish();
        }
        return true;
    }
}