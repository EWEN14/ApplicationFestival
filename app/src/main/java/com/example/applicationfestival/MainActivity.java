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

        // get the shared preferences for getting the favorites bands (key:value mechanism)
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
        //
        initDataOrUseCachedValue();
        // showListGroupe();
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
     * Méthode qui détermine si on doit faire un appel à l'API pour obtenir toutes les infos en JSON
     * des groupes, ou si l'on peut utiliser les valeurs sauvegardés dans les SharedPreferences
     */
    @UiThread
    public void initDataOrUseCachedValue() {
        // si la valeur de cette SharedPreference est vide, alors on fait l'appel à l'API
        if (this.pref.getString("diesel-groove-fullName", "").isEmpty()) {
            jsonParseAllBand();
        } else {
            // sinon on alimente le tableau infosGroupePartial
            // TODO :  alimenter le tableau infosGroupePartial et l'envoyer à la RecyclerView
            showListGroupe();
        }
    }

    /**
     * Récupère toutes les infos de tous les groupes
     * Cette méthode ne sera lancée qu'une seule fois, au premier lancement de l'appli, ou si le cache a été vidé
     */
    @Background
    public void jsonParseAllBand() {
        // start to show the ProgressDialog
        publishProgress(true);

        // Pour tous les noms de groupe récupérés, on va faire un appel GET pour obtenir les infos
        // individuelles de chaque groupe, ce qui fait que le chargement est un peu long au premier
        // lancement de l'application
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

                // on récupère les infos du groupe
                InfosGroupe infosGroupe = response.body();
                if (infosGroupe != null) {
                    // on pousse les infos dans une liste contenant une partie des infos de tous les groupes
                    allInfosGroupesPartial.add(
                            new InfosGroupePartial(nomGroupe,
                                    infosGroupe.getData().getArtiste(),
                                    infosGroupe.getData().getScene(),
                                    infosGroupe.getData().getJour())
                    );
                    // On créé 4 SharedPreferences par groupes, qui nous seront utiles pour ne pas appeler de nouveau cette méthode
                    // ajout du nom complet du groupe dans les sharedPreference
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
        // affiche la liste des groupes de musique
        showListGroupe();
    }

    @UiThread
    public void showListGroupe() {
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        customAdapterFestival = new CustomAdapterFestival(MainActivity.this, nomsGroupes);
        recyclerViewNomsGroupes.setAdapter(customAdapterFestival);  // set the Adapter to RecyclerView
    }

}