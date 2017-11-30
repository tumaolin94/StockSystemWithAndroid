package com.example.tumao.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SimpleAdapter listItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ArrayAdapter<String> adapter = new SingleArrayAdapter
                (this, android.R.layout.select_dialog_item, new String[0]);
        //Getting the instance of AutoCompleteTextView
        final AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        final TextView getQuote = findViewById(R.id.textView2);
        final TextView clear = findViewById(R.id.textView3);
        final ListView listview = findViewById(R.id.favlist);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = parent.getItemAtPosition(position);
                String temp = listItem.toString();
                String symbol = temp.substring(0,temp.indexOf("-"));
                Log.i("ItemClick",symbol);
//                Toast.makeText(getApplicationContext(),(CharSequence)symbol, Toast.LENGTH_LONG).show();
                actv.setText(symbol);
            }
        });
        actv.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    Log.i("AutoChange",actv.getText().toString());
                    if(!validation(actv)){
                        jsonRequest(actv.getText().toString(), adapter,actv);
                        adapter.notifyDataSetChanged();
                    }

                }

            }
        });


        getQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.i("onClick","test");
                Context mContext = getApplicationContext();
               if(validation(actv)){
                   Util.showToast(mContext, "Please enter a stock name or symbol");
               }else{
                   Intent i = new Intent(MainActivity.this, StockActivity.class);
                   i.putExtra("data",actv.getText().toString());
                   startActivity(i);
               }

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.i("onClick","clear");

                    Intent i = new Intent(MainActivity.this, StockActivity.class);
                    i.putExtra("data", "AAPL");
                    startActivity(i);

            }
        });
        List<FavObj> list = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences settings = getApplicationContext().getSharedPreferences("setting", 0);

        String oldList = settings.getString("save_data","");
        Log.i("Mainloaddata",oldList);
        if(!oldList.equals("")){
            list = gson.fromJson(oldList,new TypeToken<List<FavObj>>(){}.getType());
        }

        DecimalFormat df = new DecimalFormat("0.00");
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<list.size();i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("symbol", list.get(i).symbol);
            map.put("price", list.get(i).price);
            map.put("change", df.format(list.get(i).change));
            listItem.add(map);
            Log.i("change",list.get(i).symbol);
        }
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(getApplicationContext(),listItem,//数据源
                R.layout.favorite,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[] {"symbol", "price","change"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] {R.id.symbol,R.id.price,R.id.change}
        );

        //添加并且显示
        listview.setAdapter(listItemAdapter);
        final List<FavObj> sortList = list;
        final Spinner sortby = findViewById(R.id.spinner2);
        final Spinner order = findViewById(R.id.spinner3);

        sortby.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
//                Util.showToast(getApplicationContext(), spinner.getSelectedItem().toString());
                Log.i("Sort by",sortby.getSelectedItem().toString());
                Collections.sort(sortList,new FavComparator(sortby.getSelectedItem().toString(),order.getSelectedItem().toString()));
                ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
                DecimalFormat df = new DecimalFormat("0.00");
                for(int i=0;i<sortList.size();i++)
                {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("symbol", sortList.get(i).symbol);
                    map.put("price", sortList.get(i).price);
                    map.put("change", df.format(sortList.get(i).change));
                    listItem.add(map);
                    Log.i("change",sortList.get(i).symbol);
                }
                //生成适配器的Item和动态数组对应的元素
                listItemAdapter = new SimpleAdapter(getApplicationContext(),listItem,//数据源
                        R.layout.favorite,//ListItem的XML实现
                        //动态数组与ImageItem对应的子项
                        new String[] {"symbol", "price","change"},
                        //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                        new int[] {R.id.symbol,R.id.price,R.id.change}
                );

                //添加并且显示
                listview.setAdapter(listItemAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        order.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
//                Util.showToast(getApplicationContext(), spinner.getSelectedItem().toString());
                Log.i("Sort by",order.getSelectedItem().toString());
                Collections.sort(sortList,new FavComparator(sortby.getSelectedItem().toString(),order.getSelectedItem().toString()));
                ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
                DecimalFormat df = new DecimalFormat("0.00");
                for(int i=0;i<sortList.size();i++)
                {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("symbol", sortList.get(i).symbol);
                    map.put("price", sortList.get(i).price);
                    map.put("change", df.format(sortList.get(i).change));
                    listItem.add(map);
                    Log.i("change",sortList.get(i).symbol);
                }
                //生成适配器的Item和动态数组对应的元素
                listItemAdapter = new SimpleAdapter(getApplicationContext(),listItem,//数据源
                        R.layout.favorite,//ListItem的XML实现
                        //动态数组与ImageItem对应的子项
                        new String[] {"symbol", "price","change"},
                        //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                        new int[] {R.id.symbol,R.id.price,R.id.change}
                );

                //添加并且显示
                listview.setAdapter(listItemAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        listItemAdapter.notifyDataSetChanged();
    }
    public boolean validation(AutoCompleteTextView actv){
//        int length = actv.getText().replace(/\s/g, '').length();
        String text= actv.getText().toString();
        int length = text.replaceAll("\\s","").length();
        Log.i("length",length+"");
        if (length == 0) {
            return true;
        }
        return false;
    }
    public void jsonRequest(String symbol, final ArrayAdapter<String> adapter,AutoCompleteTextView actv){
        if(symbol.contains("-")) return;
        String url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/auto?input="+symbol;
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray  response) {
//                    mTxtDisplay.setText("Response: " + response.toString());
                        Log.i("Autocomplete",response.toString());
                        try {
                            Log.i("Autocomplete",response.getJSONObject(0).get("Symbol").toString()+" "+response.length());
                            String[] altArray = new String[Math.min(5,response.length())];
                            int len = altArray.length;

                            for(int i = 0 ;i<len;i++){
                                JSONObject temp = response.getJSONObject(i);
                                altArray [i] = temp.getString("Symbol")+"-"+temp.getString("Name")+"("+temp.getString("Exchange")+")";
                                Log.i("Inner",altArray[i]);
                            }
                            adapter.clear();
                            adapter.addAll(altArray);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("error",error.toString());
                    }
                });


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsObjRequest);
    }


}
