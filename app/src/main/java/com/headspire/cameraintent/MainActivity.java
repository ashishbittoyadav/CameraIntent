package com.headspire.cameraintent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @createby Ashish yadav
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button capture;
    //path to store latitude and longitude...
    TextView path;
    int i = 0;
    ImageView set_image;
    LocationListener locationListener;
    LocationManager locationManager;
    public static int CAMERA_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        capture = findViewById(R.id.capture_image);
        set_image = findViewById(R.id.setimage);
        path = findViewById(R.id.location);
        capture.setOnClickListener(this);
        path.setOnClickListener(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                path.setText("lat:"+location.getLatitude()+"long:"+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(MainActivity.this,"in location listener",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
    }

    @Override
    public void onClick(View v) {

        getConfigure();
        switch (v.getId())
        {
            case R.id.capture_image:
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, CAMERA_REQUEST);
                break;
            case R.id.location:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    getConfigure();
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1,locationListener);
                }
                else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
                }
                break;
        }
    }

    //for get the image captured by the camera.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK)
        {
            if(requestCode==CAMERA_REQUEST)
            {
                Bitmap image= (Bitmap) data.getExtras().get("data");
                getImagePath(image);
                set_image.setImageBitmap(image);
            }
        }
    }
    public  void getImagePath(Bitmap imageBitmap)
    {
        String root=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File mydir=new File(root+"/bitto_image");
        mydir.mkdir();
        try
        {
            FileOutputStream outputStream=new FileOutputStream(mydir+"/IMG"+System.currentTimeMillis()+".jpg");
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        }
        catch (Exception e)
        {}
    }

    //code for required permission to run the application.
    public void getConfigure()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA
                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ,Manifest.permission.READ_EXTERNAL_STORAGE
                            ,Manifest.permission.ACCESS_FINE_LOCATION
                    ,Manifest.permission.ACCESS_COARSE_LOCATION
                    }
                    ,2);
        }
    }
}