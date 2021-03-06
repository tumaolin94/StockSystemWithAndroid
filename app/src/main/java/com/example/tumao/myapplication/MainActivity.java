package com.example.tumao.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tumao.util.FavComparator;
import com.example.tumao.util.Util;
import com.example.tumao.adapters.FavListAdapter;
import com.example.tumao.adapters.SingleArrayAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    List<FavObj> sortList = new ArrayList<>();
    int refreshCount = 0;
    int deletePosition = 0;
    int selectedsortPositon = 0;
    int selectedorderPositon = 0;
    ListView listview ;
    AutoCompleteTextView actv;
    ArrayAdapter<String> adapter;
    Context context;

    boolean validName = false;
    private Timer timer;
    ProgressBar pb;
    /*
    * TimerTask for AutoRefresh
    * */
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            refresh(sortList,pb);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        adapter = new SingleArrayAdapter
                (this, android.R.layout.select_dialog_item, new String[0]);
        //Getting the instance of AutoCompleteTextView
        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView); //AutoCompleteTextView
        actv.setGravity(Gravity.CENTER);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        final TextView getQuote = findViewById(R.id.textView2);
        final TextView clear = findViewById(R.id.textView3);
        pb = (ProgressBar)findViewById(R.id.progressBar) ;
        final Switch sw = (Switch)findViewById(R.id.switch1);
        listview = findViewById(R.id.favlist);
        List<FavObj> list = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences settings = getApplicationContext().getSharedPreferences("setting", 0);
        pb.setVisibility(View.GONE);
        /*
        * AutoCompleteTextView ClikListener
        * */
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = parent.getItemAtPosition(position);
                String temp = listItem.toString();
                Log.i("ItemClick",temp);
                actv.setAdapter(null);
                actv.setText(temp);
            }
        });
        /*
        * AutoCompleteTextView TextChangedListener
        * */
        actv.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                actv.showDropDown();
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
                        jsonRequest(actv.getText().toString(), adapter,pb);
                        adapter.notifyDataSetChanged();
                    }

                }

            }
        });

        /*
        * getQuote ClickListener
        * */
        getQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.i("onClick","test");
                Context mContext = getApplicationContext();
               if(validation(actv)){
                   Util.showToast(mContext, "Please enter a stock name or symbol");
               }else if(!validStock()) {
                    Util.showToast(mContext, "The stock does not exist");
                }else {
                   Intent i = new Intent(MainActivity.this, StockActivity.class);
                   String temp = actv.getText().toString();

                   String symbol = temp.indexOf("-")>0?temp.substring(0,temp.indexOf("-")):temp;
                   i.putExtra("data",symbol.toUpperCase());
                   startActivity(i);
               }

            }
        });

        /*
        * Clear ClickListener
        * */
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.i("onClick","clear");
                actv.setText("");

            }
        });

        String oldList = settings.getString("save_data","");
        Log.i("Mainloaddata",oldList);
        if(!oldList.equals("")){
            list = gson.fromJson(oldList,new TypeToken<List<FavObj>>(){}.getType());
        }
        FavListAdapter fAdapter = new FavListAdapter(list);
        //add and show list
        listview.setAdapter(fAdapter);
        sortList = list;
        String[] sortItems = getResources().getStringArray(R.array.sortBy);
        String[] orderItems = getResources().getStringArray(R.array.order);

        //adapter for sort Spinner
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,sortItems){
            @Override
            public boolean isEnabled(int position){
                return !(position == 0 || position==selectedsortPositon);
            }
            @Override
            public boolean areAllItemsEnabled(){
                return false;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView,parent);
                TextView itemView = (TextView) view;
                if(position == 0 || position==selectedsortPositon){
                    itemView.setTextColor(Color.GRAY);
                }else{
                    itemView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        //adapter for order Spinner
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,orderItems){
            @Override
            public boolean isEnabled(int position){
                return !(position == 0 || position==selectedorderPositon);
            }
            @Override
            public boolean areAllItemsEnabled(){
                return false;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView,parent);
                TextView itemView = (TextView) view;
                if(position == 0 || position==selectedorderPositon){
                    itemView.setTextColor(Color.GRAY);
                }else{
                    itemView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        final Spinner sortby = findViewById(R.id.spinner2);
        sortby.setAdapter(sortAdapter);
        final Spinner order = findViewById(R.id.spinner3);
        order.setAdapter(orderAdapter);

        sortby.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.i("Sort by",sortby.getSelectedItem().toString());
                Log.i("Sort by",position+"");
                Collections.sort(sortList,new FavComparator(sortby.getSelectedItem().toString(),order.getSelectedItem().toString()));
                selectedsortPositon = position;
                FavListAdapter fAdapter = new FavListAdapter(sortList);
                listview.setAdapter(fAdapter);
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
                selectedorderPositon = position;
                FavListAdapter fAdapter = new FavListAdapter(sortList);
                listview.setAdapter(fAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        ImageView refresh = (ImageView)findViewById(R.id.refreshView);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.i("onClick","clear");
                refresh(sortList,pb);

            }
        });
        listview.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent i = new Intent(MainActivity.this, StockActivity.class);
                FavObj itemObj = (FavObj)listview.getItemAtPosition(position);

                String symbol = itemObj.getSymbol();
                i.putExtra("data",symbol.toUpperCase());
                startActivity(i);

            }
        });

        listview.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                FavObj itemObj = (FavObj)listview.getItemAtPosition(position);
                String item = itemObj.getSymbol();
                deletePosition = position;
                return false;
            }

        });
        listview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("Remove from Favourites?");
                contextMenu.add(0,0,0,"No");
                contextMenu.add(0,1,0,"Yes");
            }
        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    pb.setVisibility(View.VISIBLE);
                    Log.i("switch","startAutoRefresh");
                    timer = new Timer();
                    // 1000，delay 1 second。
                    // 5000，execute per 5 second。
                    timer.schedule(task, 1000, 5000);
                } else {
                    //非选中时 do some thing
                    Log.i("switch","stopAutoRefresh");
                    timer.cancel();
                    task.cancel();
                }
            }
        });
        refresh(sortList,pb);
    }
    /*
    * save favorite data to local storage
    * */
    public void saveLocal(){
        Gson gson = new Gson();
        Log.i("star","star");
//1、open Preferences
        SharedPreferences settings = context.getSharedPreferences("setting", 0);
//2、editor
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        String newList = gson.toJson(sortList);
        Log.i("newsortList ",newList);

        editor.putString("save_data",newList);
        editor.commit();
    }
    /*
    *
    * @Parameter item: the item chosen
    * */
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        switch(item.getItemId()) {
            case 0:
            Toast.makeText(getBaseContext(),
                    "Select No",
                    Toast.LENGTH_SHORT).show();
            break;

            case 1:
                Toast.makeText(getBaseContext(),
                        "Select Yes",
                        Toast.LENGTH_SHORT).show();
                sortList.remove(deletePosition);
                Gson gson = new Gson();
                Log.i("star","star");
//1、open Preferences
                SharedPreferences settings = context.getSharedPreferences("setting", 0);
//2、editor
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                String newList = gson.toJson(sortList);
                Log.i("newsortList ",newList);
                editor.putString("save_data",newList);
                editor.commit();
                FavListAdapter fAdapter = new FavListAdapter(sortList);
                listview.setAdapter(fAdapter);
            break;

            default:
                break;
        }

        return super.onContextItemSelected(item);

    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        List<FavObj> list = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences settings = getApplicationContext().getSharedPreferences("setting", 0);

        String oldList = settings.getString("save_data","");
        Log.i("Mainloaddata",oldList);
        if(!oldList.equals("")){
            list = gson.fromJson(oldList,new TypeToken<List<FavObj>>(){}.getType());
        }
        FavListAdapter fAdapter = new FavListAdapter(list);
        sortList = list;
        listview.setAdapter(fAdapter);
        refresh(sortList,pb);
    }
    public boolean validation(AutoCompleteTextView actv){
        String text= actv.getText().toString();
        int length = text.replaceAll("\\s","").length();
        Log.i("length",length+"");
        if (length == 0) {
            return true;
        }
        return false;
    }
    public void jsonRequest(String symbol, final ArrayAdapter<String> adapter,final ProgressBar pb){
        if(symbol.contains("-")) {
            return;
        }
        pb.setVisibility(View.VISIBLE);
        String url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/auto?input="+symbol;
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray  response) {
                        Log.i("Autocomplete",response.toString());
                        try {
                            if(response.length()!=0){
                                Log.i("Autocomplete",response.getJSONObject(0).get("Symbol").toString()+" "+response.length());
                                String[] altArray = new String[Math.min(5,response.length())];
                                int len = altArray.length;

                                for(int i = 0 ;i<len;i++){
                                    JSONObject temp = response.getJSONObject(i);
                                    altArray [i] = temp.getString("Symbol")+"-"+temp.getString("Name")+"("+temp.getString("Exchange")+")";
                                    Log.i("Inner",altArray[i]);
                                }
                                displayAuto(altArray);
                                Log.i("jsonRequest","beforeshowDropDown");
                                actv.showDropDown();
                                validName = true;
                            }else{
                                validName = false;
                            }

                            pb.setVisibility(View.GONE);
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

    public void displayAuto(String[] altArray){
        if(altArray.length!=0){
            adapter = new SingleArrayAdapter
                    (this, android.R.layout.select_dialog_item, altArray);
            Log.i("displayAuto",adapter.toString());
            actv.setAdapter(null);
            actv.setAdapter(adapter);
            actv.showDropDown();
        }
    }

    public void refresh(List<FavObj> sortList,final ProgressBar pb){
        for(int i=0;i<sortList.size();i++){
            String url = "http://newphp-nodejs-env.rakp9pisrm.us-west-1.elasticbeanstalk.com/symbol?symbol="+sortList.get(i).getSymbol();
            refreshRequest(i,url,getApplicationContext(),pb);
        }
    }
    public void refreshRequest(final int i, String url, Context context,final ProgressBar pb){
        if(sortList.get(i).getSymbol().contains("-")) return;

        Log.i("beforerefreshRequest",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String[] values = new String[8];
                            if(response.has("Error Message")){
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
                            dealRefresh(values,i,pb);

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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(10000,3,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
    }
    public boolean validStock(){
        return validName;
    }
    public void dealRefresh(String[] values,int i, final ProgressBar pb){
        double change = Double.parseDouble(values[1]) - Double.parseDouble(values[5]);
        double change_per = change /  Double.parseDouble(values[5]);
        Log.i("RefreshfavObj ",values[0]);
        Log.i("RefreshfavObj ",values[1]);
        Log.i("RefreshfavObj ",change+"");
        Log.i("RefreshfavObj ",change_per+"");
        if(i>=sortList.size()) return;
        FavObj favobj = sortList.get(i);
        sortList.get(i).setSymbol(values[0]);
        sortList.get(i).price=Double.parseDouble(values[1]);
        sortList.get(i).change=change;
        sortList.get(i).change_per=change_per;
        refreshCount++;
        FavListAdapter fAdapter = new FavListAdapter(sortList);
        listview.setAdapter(fAdapter);
        saveLocal();
        pb.setVisibility(View.GONE);
        if(refreshCount == sortList.size()){
            pb.setVisibility(View.GONE);
        }
    }

}
