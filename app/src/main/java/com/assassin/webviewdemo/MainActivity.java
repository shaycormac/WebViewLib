package com.assassin.webviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.assassin.webviewlibrary.ChromeWebViewActivity;
import com.assassin.webviewlibrary.provider.SPHelper;
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
                SPHelper.save(Param.LOCAL.CLICK_H5_SHORT_CUT, System.currentTimeMillis());
               String url = til.getEditableText().toString();
               if (TextUtils.isEmpty(url))
               {
                   Toast.makeText(MainActivity.this, "网址不能为空", Toast.LENGTH_SHORT).show();
                   return;
               }
                Intent intent = new Intent(MainActivity.this, ChromeWebViewActivity.class);
                intent.putExtra(Param.INTENT_PARAM.URL, url);
                intent.putExtra(Param.INTENT_PARAM.TITLE, "标题");
                startActivity(intent);
                
            }
        });
        findViewById(R.id.shapeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tmpIntent = new Intent(MainActivity.this, ShapeActivity.class);
                startActivity(tmpIntent);
                
            }
        });
    }
}
