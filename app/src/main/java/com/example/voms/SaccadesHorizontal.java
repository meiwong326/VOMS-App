package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SaccadesHorizontal extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saccades_horizontal);

        button = findViewById(R.id.saccades_horizontal_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaccadesHorizontal.this, SaccadesVertical.class);
                startActivity(intent);
            }
        });
    }
}
