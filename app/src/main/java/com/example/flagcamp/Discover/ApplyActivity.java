package com.example.flagcamp.Discover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flagcamp.R;

public class ApplyActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        Bundle bundle = getIntent().getExtras();
        String applyUrl = bundle.getString("apply_url");
        webView = findViewById(R.id.career_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        if (applyUrl.startsWith("mailto:")) {
            startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(applyUrl)));
            finish();
        } else {
            webView.loadUrl(applyUrl);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Go back to previous web page if applicable.
        if (keyCode == KeyEvent.KEYCODE_BACK && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
