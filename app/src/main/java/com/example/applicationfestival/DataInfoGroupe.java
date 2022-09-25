package com.example.applicationfestival;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataInfoGroupe {

    @SerializedName("artiste")
    @Expose
    private String artiste;
    @SerializedName("texte")
    @Expose
    private String texte;
    @SerializedName("web")
    @Expose
    private String web;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("scene")
    @Expose
    private String scene;
    @SerializedName("jour")
    @Expose
    private String jour;
    @SerializedName("heure")
    @Expose
    private String heure;
    @SerializedName("time")
    @Expose
    private Integer time;

    public DataInfoGroupe() {
    }

    public DataInfoGroupe(String artiste, String texte, String web, String image, String scene, String jour, String heure, Integer time) {
        this.artiste = artiste;
        this.texte = texte;
        this.web = web;
        this.image = image;
        this.scene = scene;
        this.jour = jour;
        this.heure = heure;
        this.time = time;
    }

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DataInfoGroupe{" +
                "artiste='" + artiste + '\'' +
                ", texte='" + texte + '\'' +
                ", web='" + web + '\'' +
                ", image='" + image + '\'' +
                ", scene='" + scene + '\'' +
                ", jour='" + jour + '\'' +
                ", heure='" + heure + '\'' +
                ", time=" + time +
                '}';
    }
}
