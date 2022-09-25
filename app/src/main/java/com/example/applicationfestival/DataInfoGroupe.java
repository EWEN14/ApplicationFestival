package com.example.applicationfestival;

public class DataInfoGroupe {

    String artiste;

    String texte;

    String web;

    String image;

    String scene;

    String jour;

    String heure;

    int time;

    public DataInfoGroupe() {}

    public DataInfoGroupe(String artiste, String texte, String web, String image, String scene, String jour, String heure, int time) {
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
