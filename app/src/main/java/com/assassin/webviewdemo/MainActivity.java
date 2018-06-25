package com.assassin.webviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.assassin.webviewlibrary.ChromeWebViewActivity;
import com.assassin.webviewlibrary.util.Param;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextInputEditText til = findViewById(R.id.til);
      
        findViewById(R.id.tvWebView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String url = til.getEditableText().toString();
                Intent intent = new Intent(MainActivity.this, ChromeWebViewActivity.class);
                intent.putExtra(Param.INTENT_PARAM.URL, url);
                intent.putExtra(Param.INTENT_PARAM.TITLE, "标题");
                startActivity(intent);
                
            }
        });
    }
}
