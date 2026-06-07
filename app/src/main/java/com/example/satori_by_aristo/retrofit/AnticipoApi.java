package com.example.satori_by_aristo.retrofit;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface AnticipoApi {

     @GET("Aristo/api/anticipo")
    Call<List<AnticipoDto>> getAllAnticipos();

     @GET("Aristo/api/anticipo/{idFolio}")
    Call<AnticipoDto> getAnticipoById(@Path("idFolio") Integer idFolio);

     @POST("Aristo/api/anticipo")
    Call<AnticipoDto> saveAnticipo(@Body AnticipoDto anticipo);

     @PUT("Aristo/api/anticipo/{idFolio}")
    Call<AnticipoDto> updateAnticipo(@Path("idFolio") Integer idFolio,
                                     @Body AnticipoDto anticipo);

    @GET("anticipo/bitacora/{bitacoraId}")
    Call<List<AnticipoDto>> getAnticiposByBitacora(@Path("bitacoraId") int bitacoraId);


    @DELETE("Aristo/api/anticipo/{idFolio}")
    Call<Void> deleteAnticipo(@Path("idFolio") Integer idFolio);
}
