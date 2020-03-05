package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaccadesHorizontalSymptoms extends AppCompatActivity {

    public static final String EXTRA_NUMBER = "com.example.voms.EXTRA_NUMBER";
    EditText saccadesHorizontalDizziness;
    EditText saccadesHorizontalNausea;
    EditText saccadesHorizontalHeadache;
    EditText saccadesHorizontalFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saccades_horizontal_symptoms); //TODO Put correct value here

        Button button = findViewById(R.id.saccades_horizontal_button); //this is the start button that enters into the next test, VOR horizontal
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Open saccades horizontal page
                saccadesHorizontalDizziness = (EditText) findViewById(R.id.saccades_horizontal_dizziness); //when the start button is pressed, the current values are saved as the baseline attributes
                saccadesHorizontalNausea = (EditText) findViewById(R.id.saccades_horizontal_nausea);
                saccadesHorizontalHeadache = (EditText) findViewById(R.id.saccades_horizontal_headache);
                saccadesHorizontalFogginess = (EditText) findViewById(R.id.saccades_horizontal_fogginess);
                Intent intent = new Intent(SaccadesHorizontalSymptoms.this, VORHorizontalInstructions.class);
                startActivity(intent);

            }


        });

        //the following getter setter will be used for the results column
        //TODO: Set min and max at 0-10 for user input, convert to int
   /* public EditText getHorizontalSaccadesDizziness(){
        return this.HorizontalSaccadesDizziness;
    }

    public EditText getHorizontalSaccadesFogginess(){
        return this.HorizontalSaccadesFogginess;
    }

    public EditText getHorizontalSaccadesNausea(){
        return this.HorizontalSaccadesNausea;
    }

    public EditText getHorizontalSaccadesHeadache(){
        return this.HorizontalSaccadesHeadache;
    }
*/
    }
}