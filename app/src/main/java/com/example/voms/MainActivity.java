package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "OpenCVConfiguration";

    EditText baseDizziness;
    EditText baseNausea;
    EditText baseHeadache;
    EditText baseFogginess;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV configuration was a success");
        }
        else{
            Log.d(TAG, "OpenCV configuration was NOT a success");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.main_activity_button);//this is the start button that enters into the first test, smooth pursuits
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Smooth Pursuits page
                Intent intent = new Intent(MainActivity.this, SmoothPursuitsInstructions.class);
                startActivity(intent);
                baseDizziness = findViewById(R.id.baseline_dizziness); //when the start button is pressed, the current values are saved as the baseline attributes
                baseNausea = findViewById(R.id.baseline_nausea);
                baseHeadache = findViewById(R.id.baseline_headache);
                baseFogginess = findViewById(R.id.baseline_fogginess);
            }
        });
    }

}
