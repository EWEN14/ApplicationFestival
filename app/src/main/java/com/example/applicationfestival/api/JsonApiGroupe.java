package com.example.applicationfestival.api;

import com.example.applicationfestival.model.InfosGroupe;
import com.example.applicationfestival.model.ListeGroupe;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonApiGroupe {

    @GET("liste")
    Call<ListeGroupe> getListe();

    @GET("info/{nom-groupe}")
    Call<InfosGroupe> getInfosGroupe(@Path("nom-groupe") String pathNomGroupe);
}
