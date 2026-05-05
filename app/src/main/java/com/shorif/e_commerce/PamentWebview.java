package com.shorif.e_commerce;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.nio.charset.StandardCharsets;

public class PamentWebview extends AppCompatActivity {

    WebView webView;

    LinearLayout loading;

    public static String WEB_POST_DATA = "";

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pament_webview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        webView = findViewById(R.id.webView);
        loading=findViewById(R.id.loading);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                loading.setVisibility(VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loading.setVisibility(GONE);

                if (url.contains("success")) {
                    CheckOutActivity.PAMENT_PROCESS= true;
                }

                if (url.contains("fail") || url.contains("cancel")) {
                    CheckOutActivity.PAMENT_PROCESS= false;
                }
            }
        });

        if (WEB_POST_DATA != null && !WEB_POST_DATA.isEmpty()) {
            webView.postUrl(
                    "https://dhakashoping.xyz/ssl/SSLCommerz-PHP-master/checkout_hosted.php",
                    WEB_POST_DATA.getBytes(StandardCharsets.UTF_8)
            );
        } else {
            Toast.makeText(this, "Payment data missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}