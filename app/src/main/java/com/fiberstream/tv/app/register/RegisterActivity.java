package com.fiberstream.tv.app.register;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fiberstream.tv.R;

import co.id.gmedia.coremodul.SessionManager;

public class RegisterActivity extends Activity {

    WebView mWebView;
    private String mUrl;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mWebView = (WebView) findViewById(R.id.webview);
        sessionManager = new SessionManager(this);

        if(!sessionManager.getFcmid().equals("")){
            mUrl = "https://fiberstream.net.id/stb/register?fcm_id="+sessionManager.getFcmid();
        }else {
            mUrl = "https://fiberstream.net.id/stb/register?fcm_id=fcm_kosong";
        }

        if (!mUrl.equalsIgnoreCase("")) {
            mWebView.getSettings().setJavaScriptEnabled(true);

            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });

            mWebView.loadUrl(mUrl);
        }
    }
}
