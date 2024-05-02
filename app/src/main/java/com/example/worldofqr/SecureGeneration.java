package com.example.worldofqr;

import androidx.appcompat.app.AppCompatActivity;
import java.security.*;
import java.util.Base64;

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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SecureGeneration extends AppCompatActivity {
    TextView noteText;
    TextView textView;
    Button generateCode;
    TextInputEditText textEt;
    TextInputEditText secretKey;
    ImageView imgView;
    QRCodeWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_generation);

        noteText = findViewById(R.id.noteText);
        generateCode = findViewById(R.id.generateQRBtn);
        textView = findViewById(R.id.txtViewQr);
        textEt = findViewById(R.id.qrEditText);
        imgView = findViewById(R.id.QRtoDisplay);
        secretKey = findViewById(R.id.secureKey);

        generateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = textEt.getText().toString();
                if (data.isEmpty()) {
                    Toast.makeText(SecureGeneration.this, "Please enter some text to generate QR!", Toast.LENGTH_SHORT).show();
                } else {
                    String key = secretKey.getText().toString();
                    if(key.isEmpty() || key.length() != 16){
                        Toast.makeText(SecureGeneration.this, "Enter appropriate 16 characters key to encrypt QR!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try {
                            String encryptedData = encryptWithAES(data, key);
                            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                            Display display = manager.getDefaultDisplay();
                            Point point = new Point();
                            display.getSize(point);

                            int width = point.x;
                            int height = point.y;
                            int dimension = Math.min(width, height) * 3 / 4;

                            writer = new QRCodeWriter();
                            BitMatrix bitMatrix = writer.encode(encryptedData, BarcodeFormat.QR_CODE, dimension, dimension, null);
                            int[] pixels = new int[dimension * dimension];
                            for (int y = 0; y < dimension; y++) {
                                for (int x = 0; x < dimension; x++) {
                                    pixels[y * dimension + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                                }
                            }
                            Bitmap qrCodeBitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);
                            qrCodeBitmap.setPixels(pixels, 0, dimension, 0, 0, dimension, dimension);
                            imgView.setImageBitmap(qrCodeBitmap);
                            textView.setVisibility(View.INVISIBLE);
                        } catch (WriterException e) {
                            e.printStackTrace();
                            Toast.makeText(SecureGeneration.this, "Error generating QR code!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    // SHA-256 encryption
    private static String encryptWithAES(String data, String key) throws Exception {
        byte[] keyBytes = key.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}