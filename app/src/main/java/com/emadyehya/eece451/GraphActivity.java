package com.emadyehya.eece451;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by mohamadghaziraad on 5/6/16.
 */
public class GraphActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        WebView browser  = (WebView) findViewById(R.id.webView) ;


        browser.getSettings().setJavaScriptEnabled(true);
        browser.loadUrl("http://emadyehya.com/arbor-master/docs/sample-project/");






    }


}
