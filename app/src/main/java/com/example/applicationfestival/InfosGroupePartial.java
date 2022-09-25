package com.example.applicationfestival;

public class InfosGroupePartial {

    String nomJson;

    String nomComplet;

    String scene;

    String jour;

    public InfosGroupePartial() {}

    public InfosGroupePartial(String nomJson, String nomComplet, String scene, String jour) {
        this.nomJson = nomJson;
        this.nomComplet = nomComplet;
        this.scene = scene;
        this.jour = jour;
    }

    public String getNomJson() {
        return nomJson;
    }

    public void setNomJson(String nomJson) {
        this.nomJson = nomJson;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
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
}
