package com.example.applicationfestival;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailGroupe extends AppCompatActivity {

    TextView nomArtisteTv;
    ImageView imgGroupeIv;
    TextView textePresentationTv;
    TextView sceneTv;
    TextView dateTv;
    String nomGroupe;
    String urlImage;
    String urlWebPage;
    Button webPageBtn;
    Button favoriBtn;
    Context context;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_groupe);
        context = getApplicationContext();

        nomArtisteTv = findViewById(R.id.artiste);
        imgGroupeIv = findViewById(R.id.imgGroupe);
        textePresentationTv = findViewById(R.id.texte);
        sceneTv = findViewById(R.id.scene);
        dateTv = findViewById(R.id.date);
        webPageBtn = findViewById(R.id.webPage);
        favoriBtn = findViewById(R.id.favori);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        nomGroupe = extras.getString("nom_groupe");

        // Initializing a new request queue
        mQueue = Volley.newRequestQueue(this);
        // Glide.with(this).load(urlImage).into(imgGroupe);
        // Perform a GET request to parse the JSON
        jsonParse(context);
    }

    private void jsonParse(Context context) {
        // URL API of a specific group
        String url = "https://daviddurand.info/D228/festival/info/" + nomGroupe;

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
                            nomArtisteTv.setText(infosGroupe.getString("artiste"));
                            // url with the image of the band
                            urlImage = "https://daviddurand.info/D228/festival/illustrations/" + nomGroupe + "/image.jpg";
                            // display the image of the band with url previously reached
                            Glide.with(context).load(urlImage).into(imgGroupeIv);
                            // we set the TextView for the info about the band
                            textePresentationTv.setText(infosGroupe.getString("texte"));
                            // info about the scene
                            sceneTv.setText("Scène : " + infosGroupe.getString("scene"));
                            // we set the TextView for the date of the concert
                            dateTv.setText("Le " + infosGroupe.getString("jour") + " à " + infosGroupe.getString("heure"));
                            // we get the url of the webpage of the band
                            urlWebPage = infosGroupe.getString("web");
                            // we make the button clickable after we get the url web page, if there is one
                            if (!urlWebPage.isEmpty()) {
                                webPageBtn.setClickable(true);
                                webPageBtn.setVisibility(View.VISIBLE);
                            } else { // else we hide the button
                                webPageBtn.setVisibility(View.INVISIBLE);
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

    public void goToWebPage(View view) {
        Uri webpage = Uri.parse(urlWebPage);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
    }
}