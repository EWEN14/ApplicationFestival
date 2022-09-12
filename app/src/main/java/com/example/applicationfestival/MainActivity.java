package com.example.applicationfestival;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivity extends AppCompatActivity {

    // ArrayList for group names
    ArrayList<String> nomsGroupes = new ArrayList<>();

    private RequestQueue mQueue;

    TextView txtJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get the reference of RecyclerView
        RecyclerView recyclerViewNomsGroupes = findViewById(R.id.recyclerViewNomsGroupes);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewNomsGroupes.setLayoutManager(linearLayoutManager);

        txtJson = findViewById(R.id.tvJsonItem);

        // Initializing a new request queue
        mQueue = Volley.newRequestQueue(this);
        // Perform a GET request to parse the JSON
        jsonParse();

        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        CustomAdapterFestival customAdapterFestival = new CustomAdapterFestival(MainActivity.this, nomsGroupes);
        recyclerViewNomsGroupes.setAdapter(customAdapterFestival);  // set the Adapter to RecyclerView

    }

    private void jsonParse() {
        // URL API of group list
        String url = "https://daviddurand.info/D228/festival/liste";

        // We initialize a new JsonObjectRequest by specifying it's a GET request, the URL we request,
        // the JSON we send (null because GET request), what to do onResponse and what to do onErrorResponse.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // we get the data array of the json object response
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                // we get each string element of the array
                                String nomGroupe = jsonArray.getString(i);
                                // we push the element on the nomsGroupes arrayList
                                nomsGroupes.add(nomGroupe);

                                // test display in a textview
                                txtJson.setText("Liste des groupes :");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // we add the request to the request queue
        mQueue.add(request);
    }
}