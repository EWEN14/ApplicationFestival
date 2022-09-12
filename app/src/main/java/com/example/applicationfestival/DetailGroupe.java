package com.example.applicationfestival;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailGroupe extends AppCompatActivity {

    TextView nomArtiste;
    TextView textePresentation;
    TextView scene;
    TextView jour;
    TextView heure;
    String nomGroupe;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_groupe);

        nomArtiste = findViewById(R.id.artiste);
        textePresentation = findViewById(R.id.texte);
        scene = findViewById(R.id.scene);
        jour = findViewById(R.id.jour);
        heure = findViewById(R.id.heure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        nomGroupe = extras.getString("nom_groupe");

        // Initializing a new request queue
        mQueue = Volley.newRequestQueue(this);
        // Perform a GET request to parse the JSON
        jsonParse();
    }

    private void jsonParse() {
        // URL API of a specific group
        String url = "https://daviddurand.info/D228/festival/info/"+nomGroupe;

        // We initialize a new JsonObjectRequest by specifying it's a GET request, the URL we request,
        // the JSON we send (null because GET request), what to do onResponse and what to do onErrorResponse.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // we get the data object of the json object response
                            JSONObject infosGroupe = response.getJSONObject("data");
                            // we set the value of the TextViews with the corresponding key
                            nomArtiste.setText(infosGroupe.getString("artiste"));
                            textePresentation.setText(infosGroupe.getString("texte"));
                            scene.setText(infosGroupe.getString("scene"));
                            jour.setText(infosGroupe.getString("jour"));
                            heure.setText(infosGroupe.getString("heure"));
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