package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConvergenceSymptoms extends AppCompatActivity {

    EditText convergenceDizziness;
    EditText convergenceNausea;
    EditText convergenceHeadache;
    EditText convergenceFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convergence_symptoms);

        convergenceDizziness = findViewById(R.id.convergence_dizziness);
        convergenceNausea = findViewById(R.id.convergence_nausea);
        convergenceHeadache = findViewById(R.id.convergence_headache);
        convergenceFogginess = findViewById(R.id.convergence_fogginess);

        Button button = findViewById(R.id.convergence_symptoms_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open VOR horizontal page
                Intent intent = new Intent(ConvergenceSymptoms.this, VORHorizontalInstructions.class);
                startActivity(intent);

            }


        });

        //the following getter setter will be used for the results column
        //TODO: Set min and max at 0-10 for user input
   /* public EditText getConvergenceDizziness(){
        return this.convergenceDizziness;
    }

    public EditText getConvergenceFogginess(){
        return this.convergenceFogginess;
    }

    public EditText getConvergenceNausea(){
        return this.convergenceNausea;
    }

    public EditText getConvergenceHeadache(){
        return this.convergenceHeadache;
    }
*/
    }
}