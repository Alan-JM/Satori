package com.example.satori_by_aristo.retrofit;

import android.content.Context;

import com.example.satori_by_aristo.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    private static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // Obtiene la URL base desde strings.xml
            String baseUrl = context.getString(R.string.base_url);

            // Configuración de Gson con formato de fecha
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd") // o "yyyy-MM-dd'T'HH:mm:ss"
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    // 🔹 Endpoints de Bitácora
    public static BitacoraApi getBitacoraApi(Context context) {
        return getClient(context).create(BitacoraApi.class);
    }

    // 🔹 Endpoints de Anticipos
    public static AnticipoApi getAnticipoApi(Context context) {
        return getClient(context).create(AnticipoApi.class);
    }

    // 🔹 Endpoints de Liquidaciones
    public static LiquidacionApi getLiquidacionApi(Context context) {
        return getClient(context).create(LiquidacionApi.class);
    }

    // 🔹 Endpoints de Viajes
    public static ViajeApi getViajeApi(Context context) {
        return getClient(context).create(ViajeApi.class);
    }

}
