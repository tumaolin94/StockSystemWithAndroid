package com.example.tumao.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by tumao on 2017/11/26.
 */

public class HistoricalView  extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_his, container, false);

        final Context context = this.getContext();
        final String testURL = "file:///android_asset/highstock.html";
        final WebView webView = (WebView)rootView.findViewById(R.id.hisView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(testURL);
        Log.i("Historical",testURL);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                Log.i("Historical",testURL);
                webView.loadUrl("javascript:submitSymbol()");
            }
        });
//        webView.post(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("Historical",testURL);
//                webView.loadUrl("javascript:submitSymbol()");
//            }
//        });
//        webView.post(new Runnable() {
//            @Override
//            public void run() {
//
//                webView.loadUrl("javascript:fetchAllIndicator('"+"aapl"+"')");
//            }
//        });
        return rootView;
    }
}
