package com.example.voms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SaccadesHorizontalInstructions extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saccades_horizontal_instructions);

        button = findViewById(R.id.saccades_horizontal_instructions_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaccadesHorizontalInstructions.this, SaccadesHorizontal.class);
                startActivity(intent);
            }
        });

    }
}
