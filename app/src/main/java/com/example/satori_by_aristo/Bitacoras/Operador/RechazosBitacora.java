package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.retrofit.ApiClient;
import com.example.satori_by_aristo.retrofit.BitacoraApi;
import com.example.satori_by_aristo.retrofit.RechazoDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RechazosBitacora extends Fragment {

    private ListView listaRegistros;
    private RechazoAdapter adapter;
    private List<RechazoDto> rechazosFiltrados = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rechazos_bitacora, container, false);

        listaRegistros = view.findViewById(R.id.listaRegistros);
        adapter = new RechazoAdapter(requireContext(), rechazosFiltrados);
        listaRegistros.setAdapter(adapter);

        cargarRechazosDesdeServidor();

        return view;
    }

    private void cargarRechazosDesdeServidor() {
        BitacoraApi api = ApiClient.getBitacoraApi(requireContext());

        api.getAllRechazos().enqueue(new Callback<List<RechazoDto>>() {
            @Override
            public void onResponse(Call<List<RechazoDto>> call, Response<List<RechazoDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rechazosFiltrados.clear();

                    String telefonoSesion = SesionUsuario.getTelefonoP();

                    for (RechazoDto dto : response.body()) {
                        if (dto.getTelefonoOp() != null && dto.getTelefonoOp().equals(telefonoSesion)) {
                            rechazosFiltrados.add(dto);
                        }
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(),
                            "No se encontraron rechazos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RechazoDto>> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}