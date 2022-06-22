package com.example.schedulenotification.Activities;

import static android.app.PendingIntent.getActivity;
import static com.example.schedulenotification.Activities.CreateMission.imagesNames;
import static com.example.schedulenotification.Activities.CreateMission.imagesLinks;
import static com.example.schedulenotification.refFB.reAuth;
import static com.example.schedulenotification.refFB.refStorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Camera or gallery.
 *
 * @author Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version beta
 * @since 1 /10/2021 Lets the user pick a photo from the gallery or take a  picture with the camera and upload it to Firebase Storage
 */
public class Camera_or_Gallery extends AppCompatActivity {

    private ProgressDialog progressDialog;
    /**
     * the title in the tool bar.
     */
    TextView title;
    /**
     * Button that switches between open camera and open gallery
     */
    Button btnS;
    /**
     * The Title pic.
     */
    EditText titlePic;

    /**
     * The Show pic.
     */
    ImageView showPic;
    /**
     * The S.
     *
     * @param- s = status.
     */
    boolean s;
    /**
     * In case the user wants to name the file;
     */
    String path, /**
     * The S 0.
     */
    s0;
    /**
     * for the type of the OnActivityResult
     * 1=gallery
     * 2=camera
     *
     * @param count - counts the pictures with the default name
     */
    int i, /**
     * The Count.
     */
    count;

    /**
     * The Tb.
     */
    Toolbar tb;
    /**
     * The Act.
     *
     * @param act - the activity the user got here before:
     *  0= CreateMission activity
     * 1= Information activity(for the profile picture
     */
    int act;


    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    /**
     * The Photo uri.
     */
    Uri photoUri;
    /**
     * The Camera request.
     */
    final int CAMERA_REQUEST = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_or_gallery);

        title=(TextView) findViewById(R.id.t);
        btnS=(Button) findViewById(R.id.btnS);
        showPic=(ImageView) findViewById(R.id.showPic);
        titlePic=(EditText) findViewById(R.id.titlePic);

        tb = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        s=true;

        Intent geti= getIntent();
        act = geti.getIntExtra("page",-1);

        if(act ==2 ){
            titlePic.setText("profile");
        }

        switchToGallery();

    }

    /**
     * Switches the activity to take a picture from the gallery
     */
    private void switchToGallery() {
        SpannableString ss = new SpannableString("From Gallery 🖼️");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                btnS.setText("Open Camera");
                s = false;
                SwitchToCamera();
            }
        };
        ss.setSpan(span, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(ss);
        title.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Switches back to take a picture from the Camera.
     */
    private void SwitchToCamera() {
        SpannableString ss = new SpannableString("From Camera 📸");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                btnS.setText("Open Gallery");
                s = true;
                switchToGallery();
            }
        };
        ss.setSpan(span, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(ss);
        title.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * opens the chosen app- Gallery or Camera
     * true- gallery
     * false- camera
     *
     * @param view the view
     */
    public void openApp(View view) {
        if(s){
            i=1;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(
                            intent,
                            "Select Image from here..."),
                    PICK_IMAGE_REQUEST);

        }
        else{
            i=2;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (true) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            }

        }
    }

    /**
     * creating the image file and returning it.
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoUri = Uri.fromFile(image);
        return image;
    }

    /**
     * for when the user gets back from the chosen app
     * @param requestCode
     * @param resultcode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultcode, Intent data) {
        super.onActivityResult(requestCode, resultcode, data);
        switch (i){
            case 1:if (requestCode == PICK_IMAGE_REQUEST
                    && resultcode == RESULT_OK
                    && data != null
                    && data.getData() != null) {
                filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    filePath);
                    showPic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
            }
                break;

            case 2:
                if (resultcode == RESULT_OK && requestCode == CAMERA_REQUEST) {
                    showPic.setImageURI(photoUri);
                }
                break;
        }
    }

    /**
     * rotatin the picture by 90 degrees.
     * @param view
     */
    public void rotate(View view) {
        showPic.animate().rotation(showPic.getRotation() + 90).start();
    }

    /**
     * uploading an imagefrom :
     * 1- gallery
     * 2- camera
     *
     * @param view Intents- back to the CreateMission class: gi- gallery intent. ci- camera intent.
     */
    public void upload(View view) {
        switch (i) {
            case 1:
                if (filePath != null) {

                    progressDialog = ProgressDialog.show(this, "", "Uplading...", true);

                    if (titlePic.getText().toString().equals("")) {
                        SharedPreferences settings = getSharedPreferences("count", MODE_PRIVATE);
                        count = settings.getInt("count", -1);
                        path = "images/users/" + reAuth.getCurrentUser().getUid() + "/image-" + count;
                        s0 = "images-" + count;

                        count++;
                        SharedPreferences setting = getSharedPreferences("count", MODE_PRIVATE);
                        SharedPreferences.Editor editor = setting.edit();
                        editor.putInt("count", count);
                        editor.commit();
                    }
                    else {
                        path = "images/users/" + reAuth.getCurrentUser().getUid() + "/" + titlePic.getText().toString();
                        s0 = titlePic.getText().toString();
                    }
                    StorageReference ref = refStorage.child(path);

                    ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(
                                UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            //Toast.makeText(Camera_or_Gallery.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            //Toast.makeText(Camera_or_Gallery.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(
                                UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });

                    switch (act) {
                        case 1:
                            Intent gi = new Intent(this, CreateMission.class);

                            imagesNames.add(s0);
                            gi.putExtra("check", 3);
                            imagesLinks.add(path);
                            startActivity(gi);
                            finish();
                            break;
                        case 2:
                            Intent si = new Intent(this, Information.class);
                            si.putExtra("way", path);
                            si.putExtra("check", 3);
                            startActivity(si);
                            finish();
                            break;

                    }
                }
                break;
            case 2:
                progressDialog = ProgressDialog.show(this, "", "Uploading...", true);

                if (titlePic.getText().toString().equals("")) {
                    SharedPreferences settings = getSharedPreferences("count", MODE_PRIVATE);
                    count = settings.getInt("count", -1);
                    path = "images/users/" + reAuth.getCurrentUser().getUid() + "/image-" + count;
                    s0 = "images-" + count;

                    count++;

                    SharedPreferences setting = getSharedPreferences("count", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putInt("count", count);
                    editor.commit();
                } else {
                    path = "images/users/" + reAuth.getCurrentUser().getUid() + "/" + titlePic.getText().toString();
                    s0 = titlePic.getText().toString();
                }

                UploadTask uploadTask = refStorage.child(path).putFile(photoUri);


                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Log.d("VE","picture camera uploaded");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d("NO","didn't work");
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int)progress + "%");
                    }
                });
                switch (act){
                    case 1:
                        Intent ci = new Intent(this, CreateMission.class);
                                imagesNames.add(s0);
                                ci.putExtra("check",3);
                                imagesLinks.add(path);
                                startActivity(ci);
                                finish();
                                break;
                            case 2:
                                Intent si = new Intent(this, Information.class);
                                si.putExtra("way", path);
                                si.putExtra("check",3);
                                startActivity(si);
                                finish();
                                break;
                        }

                break;
        }
    }

    /**
     * In order to fix a problem with the ProgressDialog Bar
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( progressDialog!=null && progressDialog.isShowing() ){
            progressDialog.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.back, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i;
        switch (item.getItemId()){
            case R.id.back:
                switch (act){
                    case 1:
                        i = new Intent (this, CreateMission.class);
                        startActivity(i);
                        finish();
                        break;
                    case 2:
                        i = new Intent (this, Information.class);
                        startActivity(i);
                        finish();
                        break;
                }
                break;

            case R.id.i:
                i = new Intent(this, About.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }
}