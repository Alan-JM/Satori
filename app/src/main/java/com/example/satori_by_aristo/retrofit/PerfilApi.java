package com.example.satori_by_aristo.retrofit;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PerfilApi {
    @GET("perfil")
    Call<List<PerfilDto>> getAll();

    @GET("perfil/{telefono}")
    Call<PerfilDto> getByTelefono(@Path("telefono") String telefono);

    @POST("perfil")
    Call<PerfilDto> save(@Body PerfilDto perfil);
}