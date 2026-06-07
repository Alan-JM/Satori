package com.example.satori_by_aristo.retrofit;

import com.example.satori_by_aristo.ViajeDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface ViajeApi {

    // 🔹 Solo actualiza el campo 'iniciado'
    @PATCH("/Aristo/api/viaje/{folio}/iniciado")
    Call<ViajeDto> patchIniciado(@Path("folio") int folio, @Body ViajeDto viajeDto);
}
