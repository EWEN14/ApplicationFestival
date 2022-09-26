package com.example.applicationfestival;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
    ArrayList<InfosGroupePartial> allInfosGroupesPartial = new ArrayList<>();

    RecyclerView recyclerViewNomsGroupes;
    CustomAdapterFestival customAdapterFestival;
    ProgressDialog progressDialog;
    ListeGroupe listeGroupe;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private final String BASE_URL = "https://daviddurand.info/D228/festival/";
    private static final String TAG_CALL_LISTE = "ListeGroupe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the shared preferences (key:value mechanism)
        pref = getApplicationContext().getSharedPreferences("favoritesBands", 0);
        editor = pref.edit();

        // get the reference of RecyclerView
        recyclerViewNomsGroupes = findViewById(R.id.recyclerViewNomsGroupes);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewNomsGroupes.setLayoutManager(linearLayoutManager);

        // Perform a GET request to parse the JSON
        jsonParseRetrofit();
    }

    /**
     * task executed in background, in a thread, thanks to the @Background annotation
     */
    @Background
    public void jsonParseRetrofit() {
        // start to show the ProgressDialog
        publishProgress(true);

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
        publishProgress(false);
        // retrieve the data from the API or use the cached value if they exists
        initDataOrUseCachedValue();
    }

    /**
     * method executed on the main thread, as a response to what we do in
     * jsonParseRetrofit method which use the @Background annotation
     * @param progress
     */
    @UiThread
    public void publishProgress(boolean progress) {
        if (progress) {
            progressDialog = ProgressDialog.show(this, "Chargement",
                    "Récupération de la liste des groupes...", true);
        } else {
            // remove progress dialog
            progressDialog.dismiss();
        }
    }

    /**
     * Method that determines if we have to make a call to the API to get all the JSON information
     * of the groups, or if we can use the values saved in the SharedPreferences
     */
    @UiThread
    public void initDataOrUseCachedValue() {
        // if the value of this SharedPreference is empty, then we call the API
        if (this.pref.getString("diesel-groove-fullName", "").isEmpty()) {
            jsonParseAllBand();
            // show the list of the musical bands, with a RecyclerView
            showListGroupe();
        } else {
            // else we set the list of infosGroupePartial
            setInfosGroupePartialWithCachedValues();
            showListGroupe();
        }
    }

    /**
     * Retrieves all the info from all the bands.
     * This method will be launched only once, at the first launch of the application, or if the cache has been cleared
     */
    @Background
    public void jsonParseAllBand() {
        // start to show the ProgressDialog
        publishProgress(true);

        // For all the group names retrieved, we will make a GET call to get the individual info
        // of each band, which makes the loading a bit long at the first launch of the application
        for (String nomGroupe : nomsGroupes) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonApiGroupe jsonApiGroupe = retrofit.create(JsonApiGroupe.class);
            Call<InfosGroupe> callableResponse = jsonApiGroupe.getInfosGroupe(nomGroupe);

            try {
                // we try to execute the HTTP GET call to receive the JSON, converted as a ListeGroupe object
                Response<InfosGroupe> response = callableResponse.execute();

                // we get the band's information
                InfosGroupe infosGroupe = response.body();
                if (infosGroupe != null) {
                    // we add the infos on a list containing a part of the infos of all bands
                    allInfosGroupesPartial.add(
                            new InfosGroupePartial(nomGroupe,
                                    infosGroupe.getData().getArtiste(),
                                    infosGroupe.getData().getScene(),
                                    infosGroupe.getData().getJour())
                    );
                    // We create 4 SharedPreferences by groups, which will be useful to not to call again this method
                    // adding the  fullname of the band in a sharedPreference
                    editor.putString(nomGroupe + "-fullName", infosGroupe.getData().getArtiste());
                    // adding the JSON name of the band in a sharedPreference
                    editor.putString(nomGroupe + "-json", nomGroupe);
                    // adding the scene where the band is gonna play in a sharedPreference
                    editor.putString(nomGroupe + "-scene", infosGroupe.getData().getScene());
                    // adding the day when the band is gonna play in a sharedPreference
                    editor.putString(nomGroupe + "-jour", infosGroupe.getData().getJour());
                    // save changes
                    editor.commit();
                }

                // log to check if we getted the info of the band
                Log.i(TAG_CALL_LISTE, infosGroupe.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // stop to show the ProgressDialog and pass the recyclerView
        publishProgress(false);
    }

    @UiThread
    public void showListGroupe() {
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        customAdapterFestival = new CustomAdapterFestival(MainActivity.this, allInfosGroupesPartial);
        recyclerViewNomsGroupes.setAdapter(customAdapterFestival);  // set the Adapter to RecyclerView
    }

    public void setInfosGroupePartialWithCachedValues() {
        for(String nomGroupe : nomsGroupes) {
            allInfosGroupesPartial.add(new InfosGroupePartial(
                    this.pref.getString(nomGroupe+"-json", ""),
                    this.pref.getString(nomGroupe+"-fullName", ""),
                    this.pref.getString(nomGroupe+"-scene", ""),
                    this.pref.getString(nomGroupe+"-jour", ""))
            );
        }
    }
}