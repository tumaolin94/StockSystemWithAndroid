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
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by tumao on 2017/11/26.
 */

public class NewsView  extends Fragment {
    View rootView;
    boolean ifError = false;
    int Errorcount =0;
    public NewsView(){}
    public static NewsView newInstance(String symbol){
        NewsView fragment = new NewsView();
        Bundle args = new Bundle();
        args.putString("symbol",symbol);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView!=null) return rootView;
        String str = getArguments().getString("symbol");
        final String symbol =str;
        rootView = inflater.inflate(R.layout.fragment_news, container, false);
        final ListView newsView = (ListView)rootView.findViewById(R.id.newsView);
        final Context context = this.getContext();
        final ProgressBar pb = (ProgressBar)rootView.findViewById(R.id.progressBar_news);
        final TextView tv = (TextView)rootView.findViewById(R.id.error_news);
        pb.setVisibility(View.VISIBLE);
        newsView.setVisibility(View.GONE);
        newsRequest(symbol,newsView,context,pb,tv);
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

    public void newsRequest(String symbol, final ListView listview, final Context context,final ProgressBar pb,final TextView tv){
        if(symbol.contains("-")) return;
        String url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/news?symbol="+symbol;
        Log.i("beforeRequest",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("Error Message")){
                                Log.e("NewsBeforeRequest",response.toString());
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
                                    dateList.add(myFormatDate(date));
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
                                map.put("author", "Author: "+autohrList.get(i));
                                map.put("date", "Date: "+dateList.get(i));
                                map.put("link", linkList.get(i));
                                listItem.add(map);
                            }
                            //adapter for listview
                            SimpleAdapter listItemAdapter = new SimpleAdapter(context,listItem,
                                    R.layout.newslist,
                                    new String[] {"title", "author","date"},
                                    new int[] {R.id.title,R.id.author,R.id.date}
                            );

                            listview.setAdapter(listItemAdapter);
                            listview.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);
                            ifError = false;
                        }catch (JSONException e){
                            Log.e("Return value",e.toString());

                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // TODO Auto-generated method stub
                        Log.e("errorNewsView",error.toString());
                        ifError = true;
                        Errorcount++;
                        Log.e("errorNewsView",Errorcount+"");
                        if(Errorcount == 3){
                            pb.setVisibility(View.GONE);
                            tv.setVisibility(View.VISIBLE);
                        }
                    }
                });
//        Log.i("innerend",values[0]);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(10000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsObjRequest);
    }

    public static String myFormatDate(String date){
        String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
        DateFormat from = new SimpleDateFormat(pattern, Locale.US);
        from.setTimeZone(TimeZone.getTimeZone("UTC"));

        DateFormat to = new SimpleDateFormat(pattern, Locale.US);
        to.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        Date newDate = null;
        try{
            newDate = from.parse(date);
            date = to.format(newDate);
            date = date.substring(0,date.length() - 6);
        }catch (ParseException e){
            e.printStackTrace();
            Log.e("ParseException", e.toString());
        }

        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        if(zone.useDaylightTime()){
            date += " PST";
        }else{
            date += " PDT";
        }
        return date;
    }
}
