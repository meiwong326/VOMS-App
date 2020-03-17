package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SmoothPursuitsSymptoms extends AppCompatActivity {

    EditText smoothPursuitsDizziness;
    EditText smoothPursuitsNausea;
    EditText smoothPursuitsHeadache;
    EditText smoothPursuitsFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smooth_pursuits_symptoms);

        smoothPursuitsDizziness = findViewById(R.id.SmoothPursuits_dizziness);
        smoothPursuitsNausea = findViewById(R.id.SmoothPursuits_nausea);
        smoothPursuitsHeadache = findViewById(R.id.SmoothPursuits_headache);
        smoothPursuitsFogginess = (EditText) findViewById(R.id.SmoothPursuits_fogginess);

        Button button = findViewById(R.id.smooth_pursuits_symptoms_button);//this is the start button that enters into the next test, VOR horizontal
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SmoothPursuitsSymptoms.this, SaccadesHorizontalInstructions.class);
                startActivity(intent);

            }


        });

        //the following getter setter will be used for the results column
        //TODO: Set min and max at 0-10 for user input
   /* public EditText getSmoothPursuitsDizziness(){
        return this.smoothPursuitsDizziness;
    }

    public EditText getSmoothPursuitsFogginess(){
        return this.smoothPursuitsFogginess;
    }

    public EditText getSmoothPursuitsNausea(){
        return this.smoothPursuitsNausea;
    }

    public EditText getSmoothPursuitsHeadache(){
        return this.smoothPursuitsHeadache;
    }
*/
    }
}