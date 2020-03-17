package com.example.voms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class VORVerticalSymptoms extends AppCompatActivity {

    EditText vorVerticalDizziness;
    EditText vorVerticalNausea;
    EditText vorVerticalHeadache;
    EditText vorVerticalFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vor_vertical_symptoms);

        vorVerticalDizziness = findViewById(R.id.vor_vertical_dizziness);
        vorVerticalNausea = findViewById(R.id.vor_vertical_nausea);
        vorVerticalHeadache = findViewById(R.id.vor_vertical_headache);
        vorVerticalFogginess = findViewById(R.id.vor_vertical_fogginess);

        Button button = findViewById(R.id.vor_vertical_symptoms_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VORVerticalSymptoms.this, VMSTestInstructions.class);
                startActivity(intent);

            }


        });
    }


    //the following getter setter will be used for the results column
    //TODO: Set min and max at 0-10 for user input, convert to int
   /* public EditText getVerticalVORDizziness(){
        return this.VerticalVORDizziness;
    }

    public EditText getVerticalVORFogginess(){
        return this.VerticalVORFogginess;
    }

    public EditText getVerticalVORNausea(){
        return this.VerticalVORNausea;
    }

    public EditText getVerticalVORHeadache(){
        return this.VerticalVORHeadache;
    }
*/
}