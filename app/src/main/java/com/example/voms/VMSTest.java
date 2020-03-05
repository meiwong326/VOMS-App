package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class VMSTest extends AppCompatActivity implements SensorEventListener{

    private MediaPlayer metronome;

    private Button button;

    private TextView azimuthText;

    private ImageView left_arrow;
    private ImageView right_arrow;

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
        setContentView(R.layout.activity_vmstest);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        button = findViewById(R.id.vms_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VMSTest.this, Results.class);
                startActivity(intent);
            }
        });

        azimuthText = findViewById(R.id.degreesRotated);
        left_arrow = findViewById(R.id.arrow_left);
        right_arrow = findViewById(R.id.arrow_right);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused in this app.
     */
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

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

            // If the user has turned 75 degrees to the left, display right arrow
            if (azimuthDegrees > 75) {
                left_arrow.setVisibility(View.INVISIBLE);
                right_arrow.setVisibility(View.VISIBLE);
            }
            // If the user has turned 75 degrees to the right, display left arrow
            if (azimuthDegrees < -75) {
                right_arrow.setVisibility(View.INVISIBLE);
                left_arrow.setVisibility(View.VISIBLE);
            }
        }

    }

}
