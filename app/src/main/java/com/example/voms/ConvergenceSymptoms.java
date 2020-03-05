package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConvergenceSymptoms extends AppCompatActivity {

    public static final String EXTRA_NUMBER = "com.example.voms.EXTRA_NUMBER";
    EditText convergenceDizziness;
    EditText convergenceNausea;
    EditText convergenceHeadache;
    EditText convergenceFogginess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convergence);

        Button button = findViewById(R.id.convergence_button);//this is the start button that enters into the next test, VOR horizontal
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Open Convergence page
                convergenceDizziness = (EditText) findViewById(R.id.convergence_dizziness); //when the start button is pressed, the current values are saved as the baseline attributes
                convergenceNausea = (EditText) findViewById(R.id.convergence_nausea);
                convergenceHeadache = (EditText) findViewById(R.id.convergence_headache);
                convergenceFogginess = (EditText) findViewById(R.id.convergence_fogginess);
                Intent intent = new Intent(ConvergenceSymptoms.this, VORHorizontal.class);
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