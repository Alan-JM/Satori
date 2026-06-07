package com.example.satori_by_aristo.retrofit;

import com.example.satori_by_aristo.Liquidaciones.Liquidacion;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LiquidacionApi {

     @POST("liquidacion")
    Call<Liquidacion> guardarLiquidacion(@Body Liquidacion liquidacion);

     @GET("liquidacion")
    Call<List<Liquidacion>> getLiquidaciones();

     @GET("liquidacion/{idFolio}")
    Call<Liquidacion> getLiquidacionPorFolio(@Path("idFolio") String idFolio);

    @GET("Aristo/api/liquidacion/maxFolio")
    Call<Integer> getMaxFolio();
}