package com.example.tumao.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
//    String[] fruits = {"Apple", "Apple2","Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creating the instance of ArrayAdapter containing list of fruit names
        final ArrayAdapter<String> adapter = new SingleArrayAdapter
                (this, android.R.layout.select_dialog_item, new String[0]);
        //Getting the instance of AutoCompleteTextView
        final AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        final TextView getQuote = findViewById(R.id.textView2);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = parent.getItemAtPosition(position);
                String temp = listItem.toString();
                String symbol = temp.substring(0,temp.indexOf("-"));
                Log.i("ItemClick",symbol);
                Toast.makeText(getApplicationContext(),(CharSequence)symbol, Toast.LENGTH_LONG).show();
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
                    jsonRequest(actv.getText().toString(), adapter,actv);
                    adapter.notifyDataSetChanged();
                }

            }
        });


        getQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.i("onClick","test");
                jsonRequest(actv.getText().toString(), adapter,actv);
            }
        });
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
