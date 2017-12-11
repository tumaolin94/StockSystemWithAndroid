package com.example.tumao.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by tumao on 2017/11/26.
 */

public class HistoricalView  extends Fragment {
    View rootView;
    public HistoricalView(){}
    public static HistoricalView newInstance(String symbol){
        HistoricalView fragment = new HistoricalView();
        Bundle args = new Bundle();
        args.putString("symbol",symbol);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView!=null) return rootView;
        rootView = inflater.inflate(R.layout.fragment_his, container, false);
        String str = getArguments().getString("symbol");
        final String symbol = str;
        final Context context = this.getContext();
        final String testURL = "file:///android_asset/highstock.html";
        final WebView webView = (WebView)rootView.findViewById(R.id.hisView);
        webView.getSettings().setJavaScriptEnabled(true);
        final ProgressBar pb = (ProgressBar)rootView.findViewById(R.id.progressBar_his);
        final TextView tv = (TextView)rootView.findViewById(R.id.error_his);
        pb.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        webView.loadUrl(testURL);
        Log.i("Historical",testURL);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                Log.i("Historical",testURL);
//                webView.loadUrl("javascript:submitSymbol('"+symbol+"')");
                webView.evaluateJavascript("javascript:submitSymbol('"+symbol+"')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("HisreturnJS",value);
                        if(value.equals("true")){
                            webView.setVisibility(View.VISIBLE);
                        }else {
                            tv.setVisibility(View.VISIBLE);
                        }
                        pb.setVisibility(View.GONE);
                    }
                });
            }
        });


        return rootView;
    }
}
