package com.example.applicationfestival;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.example.applicationfestival.api.JsonApiGroupe;
import com.example.applicationfestival.model.InfosGroupe;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@EActivity
public class DetailGroupe extends AppCompatActivity {

    TextView nomArtisteTv;
    String nomArtiste;
    TextView star;
    ImageView imgGroupeIv;
    TextView textePresentationTv;
    String scene;
    TextView sceneTv;
    TextView dateTv;
    String nomGroupe;
    String urlWebPage;
    Button webPageBtn;
    Button favoriBtn;
    int time;

    Context context;
    ProgressDialog progressDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    final String CHANNEL_ID = "FavoriteBandChannel";
    NotificationCompat.Builder builder;

    private final String BASE_URL = "https://daviddurand.info/D228/festival/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_groupe);
        context = getApplicationContext();
        createNotificationChannel();

        // get the shared preferences for getting the favorites bands (key:value mechanism)
        pref = getApplicationContext().getSharedPreferences("favoritesBands", 0);
        editor = pref.edit();

        nomArtisteTv = findViewById(R.id.artiste);
        star = findViewById(R.id.star);
        imgGroupeIv = findViewById(R.id.imgGroupe);
        textePresentationTv = findViewById(R.id.texte);
        sceneTv = findViewById(R.id.scene);
        dateTv = findViewById(R.id.date);
        webPageBtn = findViewById(R.id.webPage);
        favoriBtn = findViewById(R.id.favori);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        nomGroupe = extras.getString("nom_groupe");

        // show the star, full or empty depending if the band is in favorite
        showStar();
        // set the text of the favorite button depending if the band is in favorite
        libelleFavoriteBtnOnCreate();
        // Perform a GET request to get the infos about the band
        jsonParseRetrofit();
    }

    /**
     * Task executed in background, in a thread, thanks to the @Background annotation.
     * Retrieve the information of the band with a GET call to the API
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
        Call<InfosGroupe> callableResponse = jsonApiGroupe.getInfosGroupe(nomGroupe);

        try {
            // we try to execute the HTTP GET call to receive the JSON, converted as a ListeGroupe object
            Response<InfosGroupe> response = callableResponse.execute();

            // we get the band's information
            InfosGroupe infosGroupe = response.body();
            if (infosGroupe != null) {
                // get the name of the artist/band
                nomArtiste = infosGroupe.getData().getArtiste();
                // get the type of scene where the band is gonna play
                scene = infosGroupe.getData().getScene();
                // we get the url of the webpage of the band
                urlWebPage = infosGroupe.getData().getWeb();
                // we get the time that represent when the band play
                time = infosGroupe.getData().getTime();
                // display the image and the text infos of the band in the ImageView and TextViews
                loadImageAndText(infosGroupe);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // dismiss the progress dialog
        publishProgress(false);
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
                    "Récupération des informations du groupe...", true);
        } else {
            // remove progress dialog
            progressDialog.dismiss();
        }
    }

    @UiThread
    public void loadImageAndText(InfosGroupe infosGroupe) {
        // url of the image
        String urlImage = "https://daviddurand.info/D228/festival/illustrations/" + nomGroupe + "/image.jpg";
        // we put the image on the ImageView
        Glide.with(context).load(urlImage).into(imgGroupeIv);
        // we set the value of the TextViews with the corresponding key
        nomArtisteTv.setText(infosGroupe.getData().getArtiste());
        // we set the TextView for the info about the band
        textePresentationTv.setText(infosGroupe.getData().getTexte());
        // we set the TextView with info about the scene
        sceneTv.setText("Scène : " + infosGroupe.getData().getScene());
        // we set the TextView with info about when the band play on scene
        dateTv.setText("Le " + infosGroupe.getData().getJour() + " à " + infosGroupe.getData().getHeure());

        // we make the button clickable and visible after we get the url web page, if there is one
        if (!urlWebPage.isEmpty()) {
            webPageBtn.setClickable(true);
            webPageBtn.setVisibility(View.VISIBLE);
        } else { // else we hide the button
            webPageBtn.setVisibility(View.INVISIBLE);
        }
    }

    // go to the web page of the band
    public void goToWebPage(View view) {
        Uri webpage = Uri.parse(urlWebPage);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
    }

    // add or remove the band from the favorite
    public void favoriClick(View view) {
        // if already in favorite, we remove the band from favorite
        if (this.pref.getBoolean(nomGroupe, false)) {
            this.editor.putBoolean(nomGroupe, false);
            // save changes
            editor.commit();
            // change button text and star
            this.favoriBtn.setText("Ajouter en favori");
            this.star.setText("☆");
            // display a toast to inform user the band is no more in favorite
            Toast.makeText(context, nomArtiste+" retiré des favori", Toast.LENGTH_SHORT).show();
        } else {
            // else we put the band in favorite
            this.editor.putBoolean(nomGroupe, true);
            editor.commit();
            this.favoriBtn.setText("Retirer des favoris");
            this.star.setText("⭐");
            Toast.makeText(context, nomArtiste+" ajouté aux favoris", Toast.LENGTH_SHORT).show();

            // Set the Notification information
            builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                    .setContentTitle(nomArtiste+" bientôt en scène!!!")
                    .setContentText("Le groupe " + nomArtiste + " va bientôt monter sur la scène " + scene + " !")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Handler to delay the moment when we send the notification
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                // Launch Notification
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(DetailGroupe.this);
                managerCompat.notify(1, builder.build());
                // show a toaster
                Toast.makeText(context, nomArtiste+" bientôt en scène!!!", Toast.LENGTH_SHORT).show();
            }, time * 1000L);
        }
    }

    // in the beginning of the activity, we decide which star to put depending if
    // the band is in favorite
    private void showStar() {
        if (this.pref.getBoolean(nomGroupe, false)) {
            this.star.setText("⭐");
        } else {
            this.star.setText("☆");
        }
    }

    // method to set the label of the favorite button at the start of the activity, depending on the favorite status
    private void libelleFavoriteBtnOnCreate() {
        if (this.pref.getBoolean(nomGroupe, false)) {
            this.favoriBtn.setText("Retirer des favoris");
        } else {
            this.favoriBtn.setText("Ajouter en favori");
        }
    }

    // necessary creation of a NotificationChannel for using the Notification
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Favori";
            String description = "Channel destiné à afficher les notifications concernant les prochains concerts des groupes en favori";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}