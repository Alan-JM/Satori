package com.example.satori_by_aristo.Supervisor;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Supervisor extends Fragment {

    private ListView lista;
    private RegistroAdapter adapter;
    private List<Registro> registrosList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor, container, false);

        lista = view.findViewById(R.id.listaRegistros);
        adapter = new RegistroAdapter(requireContext(), registrosList);
        lista.setAdapter(adapter);

        cargarRegistros();

        // Evento de clic en cada item del ListView
        lista.setOnItemClickListener((parent, view1, position, id) -> {
            Registro registro = registrosList.get(position);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame,
                            DetalleRegistroFragment.newInstance(
                                    registro.getNombre(),
                                    registro.getTelefono(),
                                    registro.getCorreo()))
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void cargarRegistros() {
        String url = getString(R.string.base_url) + "registro";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    registrosList.clear();
                    parsearRegistros(response);
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(requireContext(), "Error de conexión: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private void parsearRegistros(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                String telefono = obj.optString("telefono");
                String nombre = obj.optString("nombre");
                String correo = obj.optString("correo");
                int enviado = obj.optInt("enviado", 0);

                 if (enviado == 0) {
                    registrosList.add(new Registro(telefono, nombre, correo, enviado, ""));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}