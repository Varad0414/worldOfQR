package com.example.worldofqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DecryptText extends AppCompatActivity {
    Button decrypt;
    TextView output;
    TextInputEditText input, inputKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt_text);

        decrypt = findViewById(R.id.decryptBtn);
        output = findViewById(R.id.decryptedText);
        input = findViewById(R.id.inputText);
        inputKey = findViewById(R.id.inputKey);

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String aesText = input.getText().toString();
                if(aesText.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Encrypted Text cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    String key = inputKey.getText().toString();
                    if(key.isEmpty() || key.length() != 16){
                        Toast.makeText(getApplicationContext(), "Enter appropriate 16 character key", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String dcText = null;
                        try {
                            dcText = decryptWithAES(aesText, key);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        output.setText(dcText);
                    }
                }
            }
        });
    }

    private static String decryptWithAES(String encryptedData, String key) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] keyBytes = key.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
}