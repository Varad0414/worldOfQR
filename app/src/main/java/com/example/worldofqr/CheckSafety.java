package com.example.worldofqr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckSafety extends AppCompatActivity {
    TextView qrSafety, qrText;
    Button checkSafe;
    private static final String IP_QUALITY_SCORE_API_KEY = "LoEE4gwvlwmplnWsBd8LOddlBQ3Uomid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_safety);

        qrText = findViewById(R.id.qrText);
        qrSafety = findViewById(R.id.qrSafety);
        checkSafe = findViewById(R.id.safeBtn);

        Intent i = getIntent();
        final String text = i.getStringExtra("qrText");

        qrText.setText(text);

        checkSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (containsUrl(text)) {
                    checkUrlSafety(text);
                } else {
                    Toast.makeText(CheckSafety.this, "QR Does not contain URL!", Toast.LENGTH_SHORT).show();
                    qrSafety.setText("Text is not a URL!");
                }
            }
        });
    }

    private void checkUrlSafety(final String url) {
        String apiKey = "LoEE4gwvlwmplnWsBd8LOddlBQ3Uomid";
        try {
            String encodedUrl = URLEncoder.encode(url, "UTF-8");
            String apiUrl = "https://www.ipqualityscore.com/api/json/url/" + apiKey + "/" + encodedUrl;

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CheckSafety.this, "Failed to check URL safety", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    final String responseData = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseData);
                                boolean unsafe = jsonResponse.getBoolean("unsafe");
                                if (unsafe) {
                                    qrSafety.setText("URL may not be safe");
                                } else {
                                    qrSafety.setText("URL is safe");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                qrSafety.setText("Failed to parse response");
                            }
                        }
                    });
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to encode URL", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean containsUrl(String text) {
        String urlRegex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(urlRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
}
