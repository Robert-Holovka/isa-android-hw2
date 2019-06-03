package com.example.robo.dz2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private EditText url;
    private Button webviewBtn;
    private Button browserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = findViewById(R.id.url_editText);
        webviewBtn = findViewById(R.id.btn_webview);
        browserBtn = findViewById(R.id.btn_browser);

        //sets cursor at the end of default text (https://)
        String text = url.getText().toString();
        url.setSelection(text.length());

        browserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentBrowser = new Intent(Intent.ACTION_VIEW);
                String inputUrl = url.getText().toString();
                inputUrl = correctURL(inputUrl);
                intentBrowser.setData(Uri.parse(inputUrl));
                startActivity(intentBrowser);

            }
        });

        final Intent intentWebView = new Intent(this, WebViewActivity.class);
        webviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String inputUrl = url.getText().toString();
                inputUrl = correctURL(inputUrl);
                intentWebView.setData(Uri.parse(inputUrl));
                startActivity(intentWebView);

            }
        });
    }

    private String correctURL(String input){

        if(!input.startsWith("https://") && !input.startsWith("http://")){
            input = "https://" + input;
        }

        return input;
    }
}
