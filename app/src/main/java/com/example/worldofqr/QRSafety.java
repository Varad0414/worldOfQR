package com.example.worldofqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRSafety extends AppCompatActivity {
    private SurfaceView cameraView;
    private TextView qrCodeDisplay;
    Button copyText;

    private static final int CAMERA_REQUEST_CODE = 100;
    private boolean qrScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrsafety);
        copyText = findViewById(R.id.checkSafetyBtn);
        cameraView = findViewById(R.id.cameraView);
        qrCodeDisplay = findViewById(R.id.qr_code_display);

        if (checkCameraPermission()) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        copyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = qrCodeDisplay.getText().toString();
                if(text.isEmpty()){
                    Toast.makeText(QRSafety.this, "Scan a QR Code first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(QRSafety.this, CheckSafety.class);
                    i.putExtra("qrText", text);
                    startActivity(i);
                }
            }
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
            return;
        }

        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    if (!qrScanned) {
                        cameraSource.start(cameraView.getHolder());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (!qrScanned && barcodes.size() > 0) {
                    qrScanned = true;
                    qrCodeDisplay.post(new Runnable() {
                        @Override
                        public void run() {
                            qrCodeDisplay.setText(barcodes.valueAt(0).displayValue);
                            qrCodeDisplay.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createCameraSource();
            } else {
                Toast.makeText(this, "Camera permission required for QR scanning", Toast.LENGTH_SHORT).show();
            }
        }
    }
}