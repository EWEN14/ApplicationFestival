package com.example.applicationfestival;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JsonApiGroupe {

    @GET("liste")
    Call<ListeGroupe> getListe();

    @GET("info/{nom-groupe}")
    Call<InfosGroupe> getFilterList(@Path("nom-groupe") String pathNomGroupe);
}
