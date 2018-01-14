package hello.youtube.com.get;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.InputStream;
import java.net.URL;

public class see extends AppCompatActivity implements View.OnClickListener {
    private Firebase mref;
    private  double lat , lon ;
    private  String url ;
    private Button get_direction;
    private ImageView photo ;
    NotificationManager manager;
    Notification myNotication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see);
        Firebase.setAndroidContext(this);
        mref = new Firebase("https://locator-9ebc3.firebaseio.com/location");
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        get_direction = ( Button ) findViewById( R.id.go);
        photo = ( ImageView )findViewById(R.id.imageView) ;
        manager.cancel(11);

        mref.child("pic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String a = dataSnapshot.getValue(String.class);
                new see.DownLoadImageTask(photo).execute(a);

                // Toast.makeText( MainActivity.this,a,Toast.LENGTH_LONG).show();



            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mref.child("latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lat = dataSnapshot.getValue(double.class);




            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mref.child("longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lon = dataSnapshot.getValue(double.class);




            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        get_direction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if ( view == get_direction ){
            String label = "Location Where Incident has occured";
            String uriBegin = "geo:" + lat + "," + lon;
            String query = lat+ "," + lon + "(" + label + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }
    }
    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }


}
