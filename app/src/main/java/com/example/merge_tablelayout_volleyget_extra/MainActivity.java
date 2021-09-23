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
import android.widget.Toast;

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
    // ------------------- initDefaultApp --------------------
     private void initDefaultApp(Bundle savedInstanceState) {
         Log.d("HERE", "initDefaultApp");
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    // ------------------- initDefaultTableLayout --------------------
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
    // ------------------- Handle Get Request --------------------
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
/*Skip Unvalid Operation*/  if (!jsonObject.getString("status").equalsIgnoreCase("Validated"))
                                continue;
                            //Mid 1.0
                            LayoutInflater I = getLayoutInflater();
                            View v = I.inflate(R.layout.fragment_display__json_values, null); //inflate slideLayout in a view
                            TableRow tr = v.findViewById(R.id.tableRow); // TableRow = swipeLayout from xml Files keep inflate in vie
                            tr.setBackgroundColor(getColor(R.color.white));
                            //Mid 1.5
                            for (int j = 0; j < 3; ++j) {
/*choose text view*/            TextView tmp = (j < 1) ? tr.findViewById(R.id.type): (j < 2) ? tr.findViewById(R.id.amount) : tr.findViewById(R.id.date);
/*set text view*/               tmp.setText( (j < 1) ? jsonObject.getString("type"): (j < 2) ? debitOrCredit(jsonObject) + jsonObject.getString("amount") + " €" : jsonObject.getString("executionDate") );
                            }
                            TextView tmp = tr.findViewById(R.id.type);
                            tmp.setText(jsonObject.getString("type"));
                            tl.addView(tr);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response.charAt(0) == '{') { // It's a jsonObject ?
                    Log.d("TYPE", "It's a jsonObject !");
                    // translate string to JsonObject & put it in a textView

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        TableLayout tl = findViewById(R.id.tableLayout);
                        tl.removeAllViews();
                        //Mid 1.0
                        Log.d("JsonArray", "|" + "CREDIT TRANSFER" + "|");
                        // ---------------- SKIP UNVALIDETED OPERATION
                        if (jsonObject.getString("status").equalsIgnoreCase("Validated")) {
                            //Mid 1.0
                            LayoutInflater I = getLayoutInflater();
                            View v = I.inflate(R.layout.fragment_display__json_values, null); //inflate slideLayout in a view
                            TableRow tr = v.findViewById(R.id.tableRow); // TableRow = swipeLayout from xml Files keep inflate in vie
                            tr.setBackgroundColor(getColor(R.color.white));
                            //Mid 1.5
                            for (int j = 0; j < 3; ++j) {
                                TextView tmp = (j < 1) ? tr.findViewById(R.id.type) : (j < 2) ? tr.findViewById(R.id.amount) : tr.findViewById(R.id.date);
                                tmp.setText((j < 1) ? jsonObject.getString("type") : (j < 2) ? debitOrCredit(jsonObject) + jsonObject.getString("amount") + " €" : jsonObject.getString("executionDate"));
                                //Credit or Debit
                            }
                            TextView tmp = tr.findViewById(R.id.type);
                            tmp.setText(jsonObject.getString("type"));
                            tl.addView(tr);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //String
                } else { // It's a string !
                    Log.d("TYPE", "It's a String !"); Log.e("TYPE", "It's a String !");
                }
                //add to Screen
            },error -> {
                Log.d("HERE", "error");
                Log.e("ERROR:", error.toString());
                if (url.length() == 0) {
                    Log.e("TYPE", "Write your URL..");
                    Toast.makeText(getBaseContext(), "Write your URL..", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("TYPE", "Wrong URL..");
                    Toast.makeText(getBaseContext(), "Wrong URL..", Toast.LENGTH_SHORT).show();

                }
            }
        );
        queue.add(stringRequest);
    }
    private String debitOrCredit(JSONObject jsonObject) throws JSONException {
        return (jsonObject.getString("direction").equalsIgnoreCase("DEBIT")) ? "-" : "";
    }

    // ------------------- sorting JsonArray --------------------
    private JSONArray sortJsonArray(JSONArray jsonArray) {
        return jsonArray;
    }
}