package com.example.satori_by_aristo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdministradorFragment extends Fragment implements ViajesAdapter.OnViajeActionListener {

    private ListView listaViajes;
    private List<ViajeDto> listaData = new ArrayList<>();
    private ViajesAdapter adapter;
    private RequestQueue queue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout que contiene el ListView y el botón de crear
        View view = inflater.inflate(R.layout.fragment_administrador, container, false);

        listaViajes = view.findViewById(R.id.listaOperadores);
        queue = Volley.newRequestQueue(requireContext());

        // Cargar los viajes desde el servidor al iniciar
        cargarViajes();

        // Botón para ir al formulario de creación
        View btnNuevo = view.findViewById(R.id.btnCrearViaje);
        if (btnNuevo != null) {
            btnNuevo.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new crearviaje()) // Usando content_frame de Principal.java
                        .addToBackStack(null)
                        .commit();
            });
        }

        return view;
    }

    private void cargarViajes() {
        String url = getString(R.string.base_url) + "viaje";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaData.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            ViajeDto viaje = new ViajeDto();
                            viaje.setFolio(obj.optInt("folio"));
                            viaje.setOperador(obj.optString("operador"));
                            viaje.setEnviado(obj.optInt("enviado"));
                            viaje.setIniciado(obj.optInt("iniciado"));
                            viaje.setFecha(obj.optString("fecha"));
                            viaje.setPassword(obj.optString("password"));

                            listaData.add(viaje);
                        }

                        // Configurar el adapter con la interfaz de acciones
                        adapter = new ViajesAdapter(requireContext(), listaData, this);
                        listaViajes.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error en el formato de datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error de red: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }


    @Override
    public void onEdit(ViajeDto viaje) {
        if (viaje.getEnviado() != null && viaje.getEnviado() == 2) {
            Toast.makeText(getContext(),
                    "No se puede editar un viaje ya enviado",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Si no está enviado, permitir edición
        crearviaje fragment = new crearviaje();
        Bundle bundle = new Bundle();
        bundle.putSerializable("viaje", viaje);
        fragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onStatusChanged() {
        // Se ejecuta cuando el adapter borra un registro o actualiza el estado "enviado"
        cargarViajes();
    }
}
