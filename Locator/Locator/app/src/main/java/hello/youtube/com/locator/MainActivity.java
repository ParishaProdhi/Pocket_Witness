package hello.youtube.com.locator;

import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.R.attr.data;
import static android.R.attr.minDate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button map_button;
    private Button camera_button , send_button ;
    GPSTracker gps ;
    private FirebaseAuth auth ;
    private Firebase mref;
    private  static final int CAMERA_REQUEST = 2 ;
    Context mContext;
    private StorageReference mStorage ;

    private ProgressDialog progressdialog;
    private static final int PICK_IMAGE_REQUEST = 234;
    NotificationManager manager;
    Notification myNotication;
    //ImageView
    private ImageView imageView;

    //a Uri object to store file path
    private Uri filePath;
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        auth = FirebaseAuth.getInstance();
        mref = new Firebase("https://locator-9ebc3.firebaseio.com/location");
        map_button = (Button) findViewById(R.id.button3);
        send_button = (Button) findViewById(R.id.button5);
        camera_button = ( Button )findViewById( R.id.button4) ;
        imageView = (ImageView) findViewById(R.id.imageView);
        mStorage = FirebaseStorage.getInstance().getReference();
        progressdialog = new ProgressDialog(this);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mContext = this;
        send_button.setOnClickListener(this);
        camera_button.setOnClickListener(this);
        map_button.setOnClickListener(this);




    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    gps = new GPSTracker(mContext, MainActivity.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onClick(View view) {
      if ( view == map_button){
          if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

          } else {
              //Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
              gps = new GPSTracker(mContext, MainActivity.this);

              // Check if GPS enabled
              if (gps.canGetLocation()) {

                  double latitude = gps.getLatitude();
                  double longitude = gps.getLongitude();
                  //    String label = "Current Location";
                  //   String uriBegin = "geo:" + latitude + "," + longitude;
                  //   String query = latitude + "," + longitude + "(" + label + ")";
                  //   String encodedQuery = Uri.encode(query);
                  //   String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                  //  Uri uri = Uri.parse(uriString);
                  //  Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                  //  startActivity(intent);

                  // \n is for new line
                  Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                  mref.child("latitude").setValue(latitude);
                  mref.child("longitude").setValue(longitude);
              } else {
                  // Can't get location.
                  // GPS or network is not enabled.
                  // Ask user to enable GPS/network in settings.
                  gps.showSettingsAlert();
              }
          }
      }
        if ( view == camera_button){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        if ( view == send_button){

           // Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           // Intent intent = new Intent(MainActivity.this,notif.class);

           // PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intent, 0);

          //  Notification.Builder builder = new Notification.Builder(MainActivity.this);

          //  builder.setAutoCancel(false);
         //   builder.setSound(soundUri);
         //   builder.setTicker("this is ticker text");
         //   builder.setContentTitle("WhatsApp Notification");
         //   builder.setContentText("You have a new message");
         //   builder.setSmallIcon(R.drawable.ic_launcher);
         //   builder.setContentIntent(pendingIntent);
         //   builder.setOngoing(true);
         //   builder.setSubText("This is subtext...");   //API level 16
         //   builder.setNumber(100);
         //   builder.build();

           // myNotication = builder.getNotification();
         //   manager.notify(11, myNotication);

           // manager.cancel(11);

                /*
                //API level 8
                Notification myNotification8 = new Notification(R.drawable.ic_launcher, "this is ticker text 8", System.currentTimeMillis());

                Intent intent2 = new Intent(MainActivity.this, SecActivity.class);
                PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 2, intent2, 0);
                myNotification8.setLatestEventInfo(getApplicationContext(), "API level 8", "this is api 8 msg", pendingIntent2);
                manager.notify(11, myNotification8);
                */



            Toast.makeText(this,"Data Sended",Toast.LENGTH_LONG).show();

        }





        }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }


    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                uploadFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference riversRef = mStorage.child("images/pic.jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            Uri download = taskSnapshot.getDownloadUrl();
                            mref.child("pic").setValue(download.toString());

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }

}