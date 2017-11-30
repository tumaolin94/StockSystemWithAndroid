package com.example.tumao.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tumao on 2017/11/26.
 */

public class CurrentView  extends Fragment {
    static String[] itemTitle ={"Stock Symbol","Last Price","Change","Timestamp","Open","Close",
            "Day's Range","Volume","Indicators"};
    static String[] indicators = {"Price","SMA","EMA","STOCH","RSI","ADX","CCI","BBANDS","MACD"};
    View rootView;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    public CurrentView(){}
    public static CurrentView newInstance(String symbol){
        CurrentView cv = new CurrentView();
        Bundle args = new Bundle();
        args.putString("symbol",symbol);
        cv.setArguments(args);
        return cv;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView!=null) return rootView;

        String str = getArguments().getString("symbol");
        final String symbol = str;
        rootView = inflater.inflate(R.layout.fragment_current, container, false);
        ListView listview = (ListView)rootView.findViewById(R.id.listView);
        final Context context = this.getContext();
        tableRequest(symbol,listview,this.getContext());
        View footView = inflater.inflate(R.layout.afterlist, null);
        ImageView fb = (ImageView)rootView.findViewById(R.id.imageView);
        ImageView star = (ImageView)rootView.findViewById(R.id.imageView2);
        final String testURL = "file:///android_asset/highchart.html";
        final WebView webView = (WebView)footView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(testURL);
        webView.post(new Runnable() {
            @Override
            public void run() {
                Log.i("highChart",testURL);
                webView.loadUrl("javascript:submitSymbol('"+symbol+"')");
            }
        });

        webView.post(new Runnable() {
                @Override
                public void run() {

                    webView.loadUrl("javascript:fetchAllIndicator('"+symbol+"')");
                }
        });


        listview.addFooterView(footView);
        final Spinner spinner = footView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Util.showToast(context, spinner.getSelectedItem().toString());
                webView.loadUrl("javascript:testVariable()");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        final TextView change = footView.findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.i("onClick","change");
                webView.post(new Runnable() {
                    @Override
                    public void run() {

                        webView.loadUrl("javascript:showChart('"+spinner.getSelectedItem().toString()+"')");
                    }
                });

            }
        });
        initFacebook();
        fb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View arg0) {
//                String url = "";
                Log.i("facebook","facebook");
//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                        .build();
//                javascript:fetchFB('SMA')

                webView.evaluateJavascript("javascript:fetchFB('"+spinner.getSelectedItem().toString()+"')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                        Log.i("returnJS",value);
                        shareToFacebook(arg0,value.substring(1,value.length()-1));
                    }
                });

//                String url = "http://export.highcharts.com/charts/chart.771f84b5a42f4d7894c5f7dbeba8edec.png";
//                shareToFacebook(arg0,url);
            }
        });
        star.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View arg0) {
                List<FavObj> list = new ArrayList<>();
                Gson gson = new Gson();
//                String url = "";
                Log.i("star","star");
//1、open Preferences
                SharedPreferences settings = context.getSharedPreferences("setting", 0);
//2、editor
                SharedPreferences.Editor editor = settings.edit();
//                editor.clear();
//                editor.commit();
//3、load old data

                String oldList = settings.getString("save_data","");
                Log.i("load data",oldList);
                if(!oldList.equals("")){
                    list = gson.fromJson(oldList,new TypeToken<List<FavObj>>(){}.getType());
                }

                double change = Double.parseDouble(values[1]) - Double.parseDouble(values[5]);
                double change_per = change /  Double.parseDouble(values[5]);
                Log.i("favObj ",values[0]);
                Log.i("favObj ",values[1]);
                Log.i("favObj ",change+"");
                Log.i("favObj ",change_per+"");
                FavObj favObj = new FavObj(values[0],Double.parseDouble(values[1]),change, change_per, new Date().getTime());
                Log.i("save data",favObj.toString());
                list.add(favObj);
                String newList = gson.toJson(list);
                Log.i("newList ",newList);

//                SharedPreferences.Editor editor = settings.edit();
//4、完成提交
              editor.putString("save_data",newList);
                editor.commit();


            }
        });
        return rootView;
    }
    ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    final String[] values = new String[8];
    public void tableRequest(String symbol, final ListView listview, final Context context){
        if(symbol.contains("-")) return;
        String url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/symbol?symbol="+symbol;

        Log.i("beforeRequest",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("Error Message")){
                                values[0] = "Error";
                                return;
                            }
                            JSONObject meta = response.getJSONObject("Meta Data");
                            Log.i("MetaData",meta.toString());
                            JSONObject array_values = response.getJSONObject("Time Series (Daily)");
                            String symbol = meta.getString("2. Symbol");
                            String timestamp = meta.getString("3. Last Refreshed");
                            if(timestamp.length()<12){
                                timestamp+=" 16:00:00";
                            }

                            Iterator iterator = array_values.keys();
                            int count = 0;
                            double open = 0,low=0,high=0;
                            int volume=0;
                            double close = 0;
                            double pre_close = 0;
                            while(iterator.hasNext()){
                                String key = (String) iterator.next();
                                if(count == 0){

                                    open = array_values.getJSONObject(key).getDouble("1. open");
                                    close = array_values.getJSONObject(key).getDouble("4. close");
                                    low = array_values.getJSONObject(key).getDouble("3. low");
                                    high = array_values.getJSONObject(key).getDouble("2. high");
                                    volume = array_values.getJSONObject(key).getInt("5. volume");

                                }
                                if(count == 1){
                                    pre_close = array_values.getJSONObject(key).getDouble("4. close");
                                    break;
                                }
                                count++;
                            }
                            DecimalFormat df = new DecimalFormat("0.00");
                            values[0] = symbol;
                            values[1] = df.format((close));
                            values[2] = df.format((close - pre_close))+"("+df.format((close - pre_close)/pre_close)+")";
                            values[3] = timestamp;
                            values[4] = df.format((open));
                            values[5] = df.format((pre_close));
                            values[6] = df.format((low))+"-"+df.format((high));
                            values[7] = String.valueOf(volume);
//                            for(int ind=0;ind<values.length;ind++){
//                                Log.i("values",values[ind]);
//                            }
                            listItem = new ArrayList<HashMap<String, Object>>();
                            for(int i=0;i<values.length;i++)
                            {
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("itemTitle", itemTitle[i]);
                                map.put("itemValue", values[i]);
//            map.put("itemValue", "Finished in 1 Min 54 Secs, 70 Moves! ");
                                listItem.add(map);
                                Log.i("values",values[i]);
                            }
                            //生成适配器的Item和动态数组对应的元素
                            SimpleAdapter listItemAdapter = new SimpleAdapter(context,listItem,//数据源
                                    R.layout.innerlist,//ListItem的XML实现
                                    //动态数组与ImageItem对应的子项
                                    new String[] {"itemTitle", "itemValue"},
                                    //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                                    new int[] {R.id.itemTitle,R.id.itemValue}
                            );

                            //添加并且显示
                            listview.setAdapter(listItemAdapter);
                        }catch (JSONException e){
                            Log.e("Return value",e.toString());
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // TODO Auto-generated method stub
                        Log.e("error",error.toString());
                    }
                });
//        Log.i("innerend",values[0]);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(10000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsObjRequest);
    }
    public void shareToFacebook(View view,String url) {
        Log.i("enterFB",url);
        //这里分享一个链接，更多分享配置参考官方介绍：https://developers.facebook.com/docs/sharing/android
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(url))
                    .build();
            shareDialog.show(linkContent);
        }
    }
    /**
     * facebook配置
     */
    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                //分享成功的回调，在这里做一些自己的逻辑处理
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

}

