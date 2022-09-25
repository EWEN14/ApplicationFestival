package com.example.applicationfestival;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@EActivity
public class MainActivity extends AppCompatActivity {

    // ArrayList for group names
    ArrayList<String> nomsGroupes = new ArrayList<>();

    ProgressDialog progressDialog;
    ListeGroupe listeGroupe;

    private final String BASE_URL = "https://daviddurand.info/D228/festival/";
    private static final String TAG_CALL_LISTE = "ListeGroupe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get the reference of RecyclerView
        RecyclerView recyclerViewNomsGroupes = findViewById(R.id.recyclerViewNomsGroupes);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewNomsGroupes.setLayoutManager(linearLayoutManager);

        // Perform a GET request to parse the JSON
        jsonParseRetrofit(recyclerViewNomsGroupes);
    }

    /**
     * task executed in background, in a thread, thanks to the @Background annotation
     * @param recyclerViewNomsGroupes
     */
    @Background
    public void jsonParseRetrofit(RecyclerView recyclerViewNomsGroupes) {
        // start to show the ProgressDialog
        publishProgress(true, null);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonApiGroupe jsonApiGroupe = retrofit.create(JsonApiGroupe.class);

        Call<ListeGroupe> callableResponse = jsonApiGroupe.getListe();

        try {
            // we try to execute the HTTP GET call to receive the JSON, converted as a ListeGroupe object
            Response<ListeGroupe> response = callableResponse.execute();
            listeGroupe = response.body();

            // log to check if we getted the list
            Log.i(TAG_CALL_LISTE, listeGroupe.toString());

            // we push the band name on the nomsGroupes arrayList
            nomsGroupes.addAll(listeGroupe.data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // stop to show the ProgressDialog and pass the recyclerView
        publishProgress(false, recyclerViewNomsGroupes);
    }

    /**
     * method executed on the main thread, as a response to what we do in
     * jsonParseRetrofit method which use the @Background annotation
     * @param progress
     * @param recyclerViewNomsGroupes
     */
    @UiThread
    public void publishProgress(boolean progress, RecyclerView recyclerViewNomsGroupes) {
        if (progress) {
            progressDialog = ProgressDialog.show(this, "Chargement",
                    "Récupération de la liste des groupes...", true);
        } else {
            // remove progress dialog
            progressDialog.dismiss();

            //  call the constructor of CustomAdapter to send the reference and data to Adapter
            CustomAdapterFestival customAdapterFestival = new CustomAdapterFestival(MainActivity.this, nomsGroupes);
            recyclerViewNomsGroupes.setAdapter(customAdapterFestival);  // set the Adapter to RecyclerView
        }
    }
}