package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.location.Location;
import android.widget.Toast;


public class VMSTest extends AppCompatActivity implements SensorEventListener{

    private MediaPlayer metronome;

    private Button button;
    private TextView azimuthText;
    private TextView speedText;
    private ImageView left_arrow;
    private ImageView right_arrow;

    private Location mLastLocation;
    private LocationManager locationManager;
    private LocationProvider locationProvider;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;

    private float[] accelerometerData = new float[3];
    private float[] magnetometerData = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] remapMatrix = new float[9];
    private float[] orientationValues = new float[3];
    float azimuth;
    double azimuthDegrees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vms);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        button = findViewById(R.id.vms_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VMSTest.this, VMSTestSymptoms.class);
                startActivity(intent);
            }
        });

        azimuthText = findViewById(R.id.degreesRotated);
        speedText = findViewById(R.id.speed);
        left_arrow = findViewById(R.id.arrow_left);
        right_arrow = findViewById(R.id.arrow_right);

        /*
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getBaseContext(), "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        //isLocationEnabled();
        locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        */

        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        metronome = MediaPlayer.create(this, R.raw.bpm50);
        metronome.start();
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        if (sensorAccelerometer != null)
            sensorManager.registerListener(
                    this, sensorAccelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        if (sensorMagnetometer != null)
            sensorManager.registerListener(
                    this, sensorMagnetometer, sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Unregister the sensor and metronome when the activity pauses.
        super.onPause();
        sensorManager.unregisterListener(this);
        metronome.stop();
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerData = event.values.clone();
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerData = event.values.clone();
                break;
            default:
                return;
        }

        boolean rotationSuccess = SensorManager.getRotationMatrix(
                rotationMatrix, null, accelerometerData, magnetometerData);

        if (rotationSuccess) {
            // Remap the coordinate system since the phone screen is facing the user
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remapMatrix);
            SensorManager.getOrientation(remapMatrix, orientationValues);

            azimuth = orientationValues[0];
            azimuthDegrees = Math.toDegrees(azimuth);

            //azimuthText.setText(String.format("%1.2f", azimuthDegrees));

            // azimuth degrees = -1 * y-rotation in virtual sensors

            // If the user has turned 75 degrees to the left, display right arrow
            if (azimuthDegrees < -75) {
                left_arrow.setVisibility(View.INVISIBLE);
                right_arrow.setVisibility(View.VISIBLE);
            }
            // If the user has turned 75 degrees to the right, display left arrow
            if (azimuthDegrees > 75) {
                right_arrow.setVisibility(View.INVISIBLE);
                left_arrow.setVisibility(View.VISIBLE);
            }
        }

    }

    /*
     LocationListener locationListener = new LocationListener() {
        private Location mLastLocation;

        @Override
        public void onLocationChanged(Location pCurrentLocation) {
            //manually calculate speed
            double speed = 0;
            if (this.mLastLocation != null)
                speed = Math.sqrt(
                        Math.pow(pCurrentLocation.getLongitude() - mLastLocation.getLongitude(), 2)
                                + Math.pow(pCurrentLocation.getLatitude() - mLastLocation.getLatitude(), 2)
                ) / (pCurrentLocation.getTime() - this.mLastLocation.getTime());
            //if there is speed from location
            if (pCurrentLocation.hasSpeed())
                //get location speed
                speed = pCurrentLocation.getSpeed();
            this.mLastLocation = pCurrentLocation;
            speedText.setText(String.format("%1.2f", speed));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
     */

}
