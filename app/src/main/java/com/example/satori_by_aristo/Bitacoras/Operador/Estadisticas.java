package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Estadisticas extends Fragment {

    private ListView listaOperadores;
    private OperadorAdapter adapter;
    private List<Perfil> operadores;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        listaOperadores = view.findViewById(R.id.listaOperadores);
        operadores = new ArrayList<>();

        cargarOperadores();

        return view;
    }

    private void cargarOperadores() {
        String url = getString(R.string.base_url) + "perfil";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    operadores.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Perfil perfil = new Perfil();
                            perfil.setNombre(obj.optString("nombre"));
                            perfil.setCorreo(obj.optString("correo"));
                            perfil.setTelefono(obj.optString("telefono"));
                            operadores.add(perfil);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter = new OperadorAdapter(requireContext(), operadores);
                    listaOperadores.setAdapter(adapter);
                },
                error -> error.printStackTrace());

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
