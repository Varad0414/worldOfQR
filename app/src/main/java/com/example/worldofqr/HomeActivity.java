package com.example.worldofqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    Button scanQr, generateQr, changeQr, decryptText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scanQr = findViewById(R.id.scanQR);
        generateQr = findViewById(R.id.generateQR);
        changeQr = findViewById(R.id.changeQR);
        decryptText = findViewById(R.id.decryptText);

        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ScanQRActivity.class);;
                startActivity(i);
            }
        });

        generateQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, GenerateQRMenu.class);
                startActivity(i);
            }
        });

        changeQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, QRSafety.class);
                startActivity(i);
            }
        });

        decryptText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, DecryptText.class);
                startActivity(i);
            }
        });
    }
}