package com.example.voms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class VORHorizontalInstructions extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vor_horizontal_instructions);

        button = findViewById(R.id.vor_horizontal_instructions_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VORHorizontalInstructions.this, VORHorizontal.class);
                startActivity(intent);
            }
        });

    }
}
