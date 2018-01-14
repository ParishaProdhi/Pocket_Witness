package hello.youtube.com.get;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ImageReader;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity  {

    private Firebase mref;
    private double lat, lon;
    private String url;
    private Button get_direction;
    private ImageView photo;
    NotificationManager manager;
    Notification myNotication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        mref = new Firebase("https://locator-9ebc3.firebaseio.com/location");
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        get_direction = (Button) findViewById(R.id.go);
        photo = (ImageView) findViewById(R.id.imageView);

        mref.child("pic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String a = dataSnapshot.getValue(String.class);

                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent intent = new Intent(MainActivity.this, see.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intent , 0);

                Notification.Builder builder = new Notification.Builder(MainActivity.this);

                builder.setAutoCancel(false);
                builder.setSound(soundUri);
                builder.setTicker("this is ticker text");
                builder.setContentTitle("Incident Occured");
                builder.setContentText("An Incident has occured.Click Here.");
                builder.setSmallIcon(R.drawable.ic_launcher);
                builder.setContentIntent(pendingIntent);
                builder.setOngoing(true);
               // builder.setSubText("This is subtext...");   //API level 16
                builder.setNumber(100);
                builder.build();

                myNotication = builder.getNotification();
                manager.notify(11, myNotication);


                // Toast.makeText( MainActivity.this,a,Toast.LENGTH_LONG).show();


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


    }
}
