package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaccadesVerticalSymptoms extends AppCompatActivity {

    public static final String EXTRA_NUMBER = "com.example.voms.EXTRA_NUMBER";
    EditText saccadesVerticalDizziness;
    EditText saccadesVerticalNausea;
    EditText saccadesVerticalHeadache;
    EditText saccadesVerticalFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saccades_vertical_symptoms); //TODO Put correct value here

        Button button = findViewById(R.id.saccades_vertical_button); //this is the start button that enters into the next test, VOR vertical
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Open saccades vertical page
                saccadesVerticalDizziness = (EditText) findViewById(R.id.saccades_vertical_dizziness); //when the start button is pressed, the current values are saved as the baseline attributes
                saccadesVerticalNausea = (EditText) findViewById(R.id.saccades_vertical_nausea);
                saccadesVerticalHeadache = (EditText) findViewById(R.id.saccades_vertical_headache);
                saccadesVerticalFogginess = (EditText) findViewById(R.id.saccades_vertical_fogginess);
                Intent intent = new Intent(SaccadesVerticalSymptoms.this, SaccadesHorizontalInstructions.class);
                startActivity(intent);

            }


        });

        //the following getter setter will be used for the results column
        //TODO: Set min and max at 0-10 for user input, convert to int
   /* public EditText getVerticalSaccadesDizziness(){
        return this.VerticalSaccadesDizziness;
    }

    public EditText getVerticalSaccadesFogginess(){
        return this.VerticalSaccadesFogginess;
    }

    public EditText getVerticalSaccadesNausea(){
        return this.VerticalSaccadesNausea;
    }

    public EditText getVerticalSaccadesHeadache(){
        return this.VerticalSaccadesHeadache;
    }
*/
    }
}
