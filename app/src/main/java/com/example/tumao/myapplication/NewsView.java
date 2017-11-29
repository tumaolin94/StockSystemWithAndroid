package com.example.tumao.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tumao on 2017/11/26.
 */

public class NewsView  extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        final ListView newsView = (ListView)rootView.findViewById(R.id.newsView);
        final Context context = this.getContext();
        newsRequest("AAPL",newsView,context);
        newsView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                HashMap<String,Object> map=(HashMap<String,Object>)newsView.getItemAtPosition(position);
                String url = map.get("link").toString();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);

            }
        });
        return rootView;
    }

    public void newsRequest(String symbol, final ListView listview, final Context context){
        if(symbol.contains("-")) return;
        String url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/news?symbol="+symbol;
        Log.i("beforeRequest",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("Error Message")){
                                Log.e("beforeRequest",response.toString());
                                return;
                            }
                            JSONObject array_values = response.getJSONObject("rss");
                            JSONArray item_values = array_values.getJSONArray("channel").getJSONObject(0).getJSONArray("item");
                            Log.i("item_values",item_values.toString());
                            int count =0;
                            List<String> titleList = new ArrayList<>();
                            List<String> linkList = new ArrayList<>();
                            List<String> dateList = new ArrayList<>();
                            List<String> autohrList = new ArrayList<>();
                            for(int i=0;i<item_values.length();i++){
                                if(item_values.getJSONObject(i).getJSONArray("link").getString(0).indexOf("article")>0){
                                    String title = item_values.getJSONObject(i).getJSONArray("title").getString(0);
                                    String link = item_values.getJSONObject(i).getJSONArray("link").getString(0);
                                    String date = item_values.getJSONObject(i).getJSONArray("pubDate").getString(0);
                                    String author = item_values.getJSONObject(i).getJSONArray("sa:author_name").getString(0);
                                    Log.i("title",title);
                                    titleList.add(title);
                                    linkList.add(link);
                                    dateList.add(date);
                                    autohrList.add(author);
                                    count++;
                                    if(count == 5 ) break;
                                }
                            }

                            ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
                            for(int i=0;i<titleList.size();i++)
                            {
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("title", titleList.get(i));
                                map.put("author", autohrList.get(i));
                                map.put("date", dateList.get(i));
                                map.put("link", linkList.get(i));
                                listItem.add(map);
                            }
                            //生成适配器的Item和动态数组对应的元素
                            SimpleAdapter listItemAdapter = new SimpleAdapter(context,listItem,//数据源
                                    R.layout.newslist,//ListItem的XML实现
                                    //动态数组与ImageItem对应的子项
                                    new String[] {"title", "author","date"},
                                    //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                                    new int[] {R.id.title,R.id.author,R.id.date}
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
