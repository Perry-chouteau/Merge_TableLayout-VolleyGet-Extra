package com.example.merge_tablelayout_volleyget_extra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;

public class MainActivity extends AppCompatActivity {

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // init
        initDefaultApp(savedInstanceState);
        initDefaultTableLayout(savedInstanceState);
        //button handler
        Button button = findViewById(R.id.button);
        queue = Volley.newRequestQueue(this);

        button.setOnClickListener(view -> {
            createTableRowFromVolley(view);
        });
        //next
    }

    //init
     private void initDefaultApp(Bundle savedInstanceState) {
         Log.d("HERE", "initDefaultApp");
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    private void initDefaultTableLayout(Bundle savedInstanceState) {
        Log.d("HERE", "initDefaultTableLayout");
        LayoutInflater I = getLayoutInflater();
        for (int i = 0; i < 2; i++){
            TableLayout tl = findViewById(R.id.tableLayoutDefault);
            View v = I.inflate(R.layout.fragment_display__json_values, null); //inflate slideLayout in a view
            TableRow tr = v.findViewById(R.id.tableRow); // TableRow = swipeLayout from xml Files keep inflate in view
            if (i != 0) {
                tr.setBackgroundColor(getColor(R.color.cardview_dark_background));
                TextView tmp;
                for (int j = 0; j < 3; ++j) {
                    tmp = (j < 1) ? tr.findViewById(R.id.type): (j < 2) ? tr.findViewById(R.id.amount) : tr.findViewById(R.id.date);
                    tmp.setText("");
                }
            }
            tl.addView(tr);
        }

    }
    //button
    private void createTableRowFromVolley(View view) {
        Log.d("HERE", "searchForData");
        //Url
        TextView textViewUrl = findViewById(R.id.editText);
        String url = textViewUrl.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {
                Log.d("HERE", "response");
                // ------ DATA ---------
                if (response.charAt(0) == '[') { // It's a jsonArray ?
                    Log.d("TYPE", "It's a jsonArray !");
                    try {
                        // jsonArray
                        JSONArray jsonArray = new JSONArray(response);
                        jsonArray = sortJsonArray(jsonArray);
                        TableLayout tl = findViewById(R.id.tableLayout);
                        tl.removeAllViews();
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            //jsonObject
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //Mid 1.0
                            Log.d("JsonArray", "|" + "CREDIT TRANSFER" + "|");
                            // ---------------- SKIP UNVALIDETED OPERATION
                            if (jsonObject.getString("status").charAt(0) != 'V')
                                continue;
                            //Mid 1.0
                            LayoutInflater I = getLayoutInflater();
                            View v = I.inflate(R.layout.fragment_display__json_values, null); //inflate slideLayout in a view
                            TableRow tr = v.findViewById(R.id.tableRow); // TableRow = swipeLayout from xml Files keep inflate in vie
                            tr.setBackgroundColor(getColor(R.color.white));
                            //Mid 1.5
                            for (int j = 0; j < 3; ++j) {
                                TextView tmp = (j < 1) ? tr.findViewById(R.id.type): (j < 2) ? tr.findViewById(R.id.amount) : tr.findViewById(R.id.date);
                                tmp.setText( (j < 1) ? jsonObject.getString("type"): (j < 2) ? jsonObject.getString("amount") + " €" : jsonObject.getString("executionDate") );
                            }
                            TextView tmp = tr.findViewById(R.id.type);
                            tmp.setText(jsonObject.getString("type")/*jsonObject.toString()*/);
                            tl.addView(tr);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } /*else if (response.charAt(0) == '{') { // It's a jsonObject ?
                    Log.d("TYPE", "It's a jsonObject !");
                    // translate string to JsonObject & put it in a textView
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        //textView.setText(jsonObject.toString());
                        tl.addView(tr);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //String
                } else { // It's a string !
                    Log.d("TYPE", "It's a String !");
                    tl.addView(tr);
                    // no translation & put it in a textView.
                    //textView.setText(response);
                }*/
                //add to Screen
            },error -> {
            Log.d("HERE", "error");
            error.printStackTrace();
            }
        );
        queue.add(stringRequest);
    }

    private JSONArray sortJsonArray(JSONArray jsonArray) {
        return jsonArray;
    }
}















































