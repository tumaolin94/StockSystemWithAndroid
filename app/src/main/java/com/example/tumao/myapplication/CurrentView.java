package com.example.tumao.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * Created by tumao on 2017/11/26.
 */

public class CurrentView  extends Fragment {
    static String[] itemTitle ={"Stock Symbol","Last Price","Change","Timestamp","Open","Close",
            "Day's Range","Volume","Indicators"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current, container, false);
        ListView listview = (ListView)rootView.findViewById(R.id.listView);

        tableRequest("aapl",listview,this.getContext());
        View footView = inflater.inflate(R.layout.afterlist, null);
        String testURL = "file:///android_asset/highchart.html";
        final WebView webView = (WebView)footView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(testURL);
        webView.post(new Runnable() {
            @Override
            public void run() {

                webView.loadUrl("javascript:submitSymbol()");
            }
        });

        listview.addFooterView(footView);
        return rootView;
    }
    public void tableRequest(String symbol, final ListView listview, final Context context){
        if(symbol.contains("-")) return;
        String url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/symbol?symbol="+symbol;
        final String[] values = new String[8];
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
                            for(int ind=0;ind<values.length;ind++){
                                Log.i("values",values[ind]);
                            }
                            ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
                            for(int i=0;i<values.length;i++)
                            {
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("itemTitle", itemTitle[i]);
                                map.put("itemValue", values[i]);
//            map.put("itemValue", "Finished in 1 Min 54 Secs, 70 Moves! ");
                                listItem.add(map);
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


}

