package com.assassin.webviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.assassin.webviewlibrary.ChromeWebViewActivity;
import com.assassin.webviewlibrary.util.Param;

public class MainActivity extends AppCompatActivity {
    private String url = "http://erp2.goldmantis.com/OAManage/Notice/NoticePreView?id=5020368&ignoreMenuAuthority=1&fromOtherSystem=true&accessToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50Ijoiemh1bGVpMiIsImpvYkNvZGUiOiJKMDk4NzA4IiwidXNlcklEIjoxMzY2MywibmJmIjoxNTQ1MjA1Mjk2LCJpc3N1ZSI6ImdvbGRNYW50aXMiLCJleHAiOjE1Mjk0NjAwMDB9.rndcpukke0DB98JsnETQWalujjHK2cDR9aAC1SRIgFY";
    private String url2 = "https://www.baidu.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextInputEditText til = findViewById(R.id.til);
      
        findViewById(R.id.tvWebView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String url = til.getEditableText().toString();
              //  url = "http://218.4.189.196:8008/NewERPApp/Android/GMERPAPP_NEW.apk";
               // url = "http://erp2.goldmantis.com/OAManage/Institution/Show?id=5000044&ignoreMenuAuthority=1&fromOtherSystem=true&accessToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50Ijoiemh1bGVpMiIsImpvYkNvZGUiOiJKMDk4NzA4IiwidXNlcklEIjoxMzY2MywibmJmIjoxNTQ1MjcxNjcxLCJpc3N1ZSI6ImdvbGRNYW50aXMiLCJleHAiOjE1Mjk1NDY0MDB9.VQriVn2ajncN2ik7JKTA_mRUSaQM5m1wZDr7MTmobco";
                Intent intent = new Intent(MainActivity.this, ChromeWebViewActivity.class);
                intent.putExtra(Param.INTENT_PARAM.URL, url);
                intent.putExtra(Param.INTENT_PARAM.TITLE, "标题");
                startActivity(intent);
                
            }
        });
    }
}
