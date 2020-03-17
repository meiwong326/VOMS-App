package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaccadesHorizontalSymptoms extends AppCompatActivity {

    EditText saccadesHorizontalDizziness;
    EditText saccadesHorizontalNausea;
    EditText saccadesHorizontalHeadache;
    EditText saccadesHorizontalFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saccades_horizontal_symptoms); //TODO Put correct value here

        saccadesHorizontalDizziness = findViewById(R.id.saccades_horizontal_dizziness); //when the start button is pressed, the current values are saved as the baseline attributes
        saccadesHorizontalNausea = findViewById(R.id.saccades_horizontal_nausea);
        saccadesHorizontalHeadache = findViewById(R.id.saccades_horizontal_headache);
        saccadesHorizontalFogginess = findViewById(R.id.saccades_horizontal_fogginess);

        Button button = findViewById(R.id.saccades_horizontal_symptoms_button);//this is the start button that enters into the next test, VOR horizontal
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaccadesHorizontalSymptoms.this, SaccadesVerticalInstructions.class);
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