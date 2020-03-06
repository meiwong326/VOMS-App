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
import android.widget.TextView;


public class VMSTest extends AppCompatActivity implements SensorEventListener{

    private MediaPlayer metronome;

    private Button button;

    private TextView azimuthText;
    private TextView pitchText;
    private TextView rollText;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;

    private float[] accelerometerData = new float[3];
    private float[] magnetometerData = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] inclinationMatrix = new float[9];
    private float[] orientationValues = new float[3];

    float azimuth, pitch, roll;
    double pitchDegrees;
    double rollDegrees;

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

        azimuthText = findViewById(R.id.azimuth);
        pitchText = findViewById(R.id.pitch);
        rollText = findViewById(R.id.roll);

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
        // Be sure to unregister the sensor when the activity pauses.
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
                rotationMatrix, inclinationMatrix, accelerometerData, magnetometerData);

        if (rotationSuccess) {
            SensorManager.getOrientation(rotationMatrix, orientationValues);

            azimuth = orientationValues[0];
            pitch = orientationValues[1];
            roll = orientationValues[2];

            pitchDegrees = Math.toDegrees(pitch);
            rollDegrees = Math.toDegrees(roll);

            azimuthText.setText(String.format("%1.2f", azimuth));
            pitchText.setText(String.format("%1.2f", pitchDegrees));
            rollText.setText(String.format("%1.2f", rollDegrees));
        }
    }

}
