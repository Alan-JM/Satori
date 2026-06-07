package com.example.satori_by_aristo.retrofit;

import android.content.Context;

import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.BitacoraDto;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Obtiene la URL desde strings.xml
            String baseUrl = context.getString(R.string.url_ip);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static BitacoraApi getBitacoraApi(Context context) {
        return getRetrofitInstance(context).create(BitacoraApi.class);
    }
}