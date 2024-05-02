package com.example.worldofqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GenerateQRMenu extends AppCompatActivity {
    Button normalQr, secureQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrmenu);

        normalQr = findViewById(R.id.generateNormal);
        secureQR = findViewById(R.id.generateSecure);

        normalQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GenerateQRMenu.this, GenerateQRActivity.class);
                startActivity(i);
            }
        });

        secureQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GenerateQRMenu.this, SecureGeneration.class);
                startActivity(i);
            }
        });
    }
}