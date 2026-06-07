package com.example.satori_by_aristo.retrofit;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface BitacoraApi {

    // Obtener todas las bitácoras
    @GET("/Aristo/api/bitacora")
    Call<List<BitacoraDto>> getAllBitacoras();

    // Obtener una bitácora por ID
    @GET("/Aristo/api/bitacora/{idFolio}")
    Call<BitacoraDto> getBitacoraById(@Path("idFolio") int idFolio);

    // Guardar nueva bitácora
    @POST("/Aristo/api/bitacora")
    Call<BitacoraDto> saveBitacora(@Body BitacoraDto bitacora);

    // Actualizar bitácora existente
    @PUT("/Aristo/api/bitacora/{idFolio}")
    Call<BitacoraDto> updateBitacora(@Path("idFolio") int idFolio, @Body BitacoraDto bitacora);

    // Eliminar bitácora
    @DELETE("/Aristo/api/bitacora/{idFolio}")
    Call<Void> deleteBitacora(@Path("idFolio") int idFolio);

    // Actualizar confirmación
    @PATCH("/Aristo/api/bitacoras/{id}/confirmacion")
    Call<Void> updateConfirmacion(
            @Path("id") int id,
            @Query("confirmacion") int confirmacion
    );

    // Guardar rechazo
    @POST("/Aristo/api/rechazo")
    Call<RechazoDto> saveRechazo(@Body RechazoDto rechazoDto);

    // Obtener todos los rechazos
    @GET("/Aristo/api/rechazo")
    Call<List<RechazoDto>> getAllRechazos();

    // Actualizar liquidación en bitácora
    @PUT("/Aristo/api/bitacora/{id}/liquidacion")
    Call<Void> updateLiquidacion(@Path("id") int id, @Query("liquidacion") String folio);

    // Obtener bitácoras autorizadas
    @GET("/Aristo/api/bitacoras/autorizadas")
    Call<List<BitacoraDto>> getBitacorasAutorizadas();

}
