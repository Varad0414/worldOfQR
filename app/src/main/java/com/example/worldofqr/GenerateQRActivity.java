package com.example.worldofqr;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class GenerateQRActivity extends AppCompatActivity {
    TextView textView;
    Button generateCode;
    TextInputEditText textEt;
    ImageView imgView;
    QRCodeWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qractivity);

        generateCode = findViewById(R.id.generateQRBtn);
        textView = findViewById(R.id.txtViewQr);
        textEt = findViewById(R.id.qrEditText);
        imgView = findViewById(R.id.QRtoDisplay);

        generateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = textEt.getText().toString();
                if(data.isEmpty()){
                    Toast.makeText(GenerateQRActivity.this, "Please enter some text to generate QR!", Toast.LENGTH_SHORT).show();
                }
                else {
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);

                    int width = point.x;
                    int height = point.y;
                    int dimension = Math.min(width, height) * 3 / 4;
                    int quietZoneSize = 4;
                    int totalDimension = dimension + (quietZoneSize * 2);

                    try {
                        writer = new QRCodeWriter();
                        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, dimension, dimension, null);
                        int[] pixels = new int[totalDimension * totalDimension];
                        for (int y = quietZoneSize; y < totalDimension - quietZoneSize; y++) {
                            for (int x = quietZoneSize; x < totalDimension - quietZoneSize; x++) {
                                pixels[y * totalDimension + x] = bitMatrix.get(x - quietZoneSize, y - quietZoneSize) ? Color.BLACK : Color.WHITE;
                            }
                        }
                        Bitmap qrCodeBitmap = Bitmap.createBitmap(totalDimension, totalDimension, Bitmap.Config.ARGB_8888);
                        qrCodeBitmap.setPixels(pixels, 0, totalDimension, 0, 0, totalDimension, totalDimension);
                        imgView.setImageBitmap(qrCodeBitmap);
                        textView.setVisibility(View.INVISIBLE);
                    } catch (WriterException e) {
                        e.printStackTrace();
                        Toast.makeText(GenerateQRActivity.this, "Error generating QR code!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
