package com.example.voms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class VMSTestSymptoms extends AppCompatActivity {

    EditText vmsDizziness;
    EditText vmsNausea;
    EditText vmsHeadache;
    EditText vmsFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vms_symptoms);

        vmsDizziness = findViewById(R.id.vms_dizziness);
        vmsNausea = findViewById(R.id.vms_nausea);
        vmsHeadache = findViewById(R.id.vms_headache);
        vmsFogginess = findViewById(R.id.vms_fogginess);

        Button button = findViewById(R.id.vms_symptoms_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VMSTestSymptoms.this, Results.class);
                startActivity(intent);

            }


        });
    }
}
