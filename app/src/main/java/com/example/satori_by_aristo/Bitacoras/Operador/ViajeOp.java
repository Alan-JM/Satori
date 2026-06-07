package com.example.satori_by_aristo.Bitacoras.Operador;

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
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.SesionActual;
import com.example.satori_by_aristo.ViajeDto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra la lista de viajes asignados al operador logueado.
 */
public class ViajeOp extends Fragment {

    private ListView lvViajes;
    private List<ViajeDto> listaViajes = new ArrayList<>();
    private ViajeopAdapter adapter;
    private RequestQueue queue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el layout principal del fragmento
        View view = inflater.inflate(R.layout.fragment_viaje_op, container, false);

        // Inicializamos las vistas y la cola de peticiones Volley
        lvViajes = view.findViewById(R.id.lvViajesDisponibles);
        queue = Volley.newRequestQueue(requireContext());

        // Cargamos los datos desde la API
        cargarViajesOperador();

        return view;
    }

    /**
     * Consume el servicio de viajes y filtra por teléfono de operador y estado 'enviado = 2'.
     */
    private void cargarViajesOperador() {
        // 1. Obtenemos el teléfono de la sesión activa para filtrar
        String miTelefono = SesionActual.obtenerInstancia().getTelefono();
        String url = getString(R.string.base_url) + "viaje";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaViajes.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            // Obtenemos los campos clave para el filtro
                            String telOperadorEnViaje = obj.optString("operador");
                            int estadoEnviado = obj.optInt("enviado");

                            // 2. Filtro Crítico: Solo mis viajes y solo si ya fueron enviados por el admin (estado 2)
                            if (telOperadorEnViaje.equals(miTelefono) && estadoEnviado == 2) {
                                ViajeDto v = new ViajeDto();
                                v.setFolio(obj.getInt("folio"));
                                v.setOperador(telOperadorEnViaje);
                                v.setEnviado(estadoEnviado);
                                v.setIniciado(obj.getInt("iniciado"));
                                v.setFecha(obj.optString("fecha", "---"));
                                v.setPassword(obj.optString("password", "N/A"));

                                listaViajes.add(v);
                            }
                        }

                        // Configuramos el adaptador personalizado
                        adapter = new ViajeopAdapter(requireContext(), listaViajes);
                        lvViajes.setAdapter(adapter);

                        // Notificamos si no hay resultados para el operador
                        if (listaViajes.isEmpty()) {
                            Toast.makeText(getContext(), "Por el momento no tienes viajes asignados.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al procesar la información de viajes.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error de red: No se pudo conectar con el servidor.", Toast.LENGTH_SHORT).show();
                }
        );

        // Agregamos la petición a la cola de Volley
        queue.add(request);
    }
}
