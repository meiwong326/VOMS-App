package com.example.voms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class VORHorizontalSymptoms extends AppCompatActivity {

    EditText vorHorizontalDizziness;
    EditText vorHorizontalNausea;
    EditText vorHorizontalHeadache;
    EditText vorHorizontalFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vor_horizontal_symptoms);

        vorHorizontalDizziness = findViewById(R.id.vor_horizontal_dizziness);
        vorHorizontalNausea = findViewById(R.id.vor_horizontal_nausea);
        vorHorizontalHeadache = findViewById(R.id.vor_horizontal_headache);
        vorHorizontalFogginess = findViewById(R.id.vor_horizontal_fogginess);

        Button button = findViewById(R.id.vor_horizontal_symptoms_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VORHorizontalSymptoms.this, VORVerticalInstructions.class);
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