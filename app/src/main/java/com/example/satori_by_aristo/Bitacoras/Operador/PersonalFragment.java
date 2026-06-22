package com.example.satori_by_aristo.Bitacoras.Operador;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.SesionActual;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersonalFragment extends Fragment {

    private ListView listOperadores;
    private OperadorAdapter adapter;
    private List<OperadorStats> operadores; // ahora usamos OperadorStats

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("PersonalFragment", "onCreateView: inflando layout");
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        listOperadores = view.findViewById(R.id.listOperadores);
        operadores = new ArrayList<>();

        cargarOperadores();

        // Listener para abrir detalle al tocar un operador
        listOperadores.setOnItemClickListener((parent, view1, position, id) -> {
            OperadorStats seleccionado = operadores.get(position);

            // Crear el nuevo fragmento y pasarle argumentos
            DetalleOperadorFragment detalleFragment = new DetalleOperadorFragment();
            Bundle args = new Bundle();

            // MODIFICACIÓN 1: Extraemos y enviamos el teléfono en lugar del nombre
            args.putString("nombreOperador", seleccionado.getTelefono());

            args.putInt("numViajes", seleccionado.getNumViajes());
            args.putDouble("totalKm", seleccionado.getTotalKm());
            args.putDouble("totalGasto", seleccionado.getTotalGasto());
            detalleFragment.setArguments(args);

            // Reemplazar el fragmento actual con el detalle
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedorDetalle, detalleFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void cargarOperadores() {
        String telSesion = SesionActual.obtenerInstancia().getTelefono();
        Log.d("PersonalFragment", "Teléfono en sesión: " + telSesion);

        String urlOperadores = getString(R.string.base_url) + "operador/admin/" + telSesion;
        Log.d("PersonalFragment", "URL llamada: " + urlOperadores);

        JsonArrayRequest requestOperadores = new JsonArrayRequest(
                Request.Method.GET,
                urlOperadores,
                null,
                response -> {
                    Log.d("PersonalFragment", "Respuesta recibida. Operadores: " + response.length());
                    operadores.clear();

                    if (response.length() == 0) {
                        Log.w("PersonalFragment", "No se encontraron operadores para este admin");
                        return;
                    }

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String nombre = obj.optString("clave"); // nombre real lo obtendrás de perfil
                            String telefono = obj.optString("telefonoP");

                            // Buscar sus bitácoras confirmadas
                            String urlBitacoras = getString(R.string.base_url) + "bitacoras/operador/" + telefono;
                            JsonArrayRequest requestBitacoras = new JsonArrayRequest(
                                    Request.Method.GET,
                                    urlBitacoras,
                                    null,
                                    responseBitacoras -> {
                                        int numViajes = 0;
                                        double totalKm = 0;
                                        double totalGasto = 0;

                                        for (int j = 0; j < responseBitacoras.length(); j++) {
                                            try {
                                                JSONObject bit = responseBitacoras.getJSONObject(j);
                                                if (bit.optInt("confirmacion") == 3) {
                                                    numViajes++;
                                                    totalKm += bit.optDouble("distanciaTotal", 0);
                                                    totalGasto += bit.optDouble("granTotal", 0);
                                                }
                                            } catch (Exception e) {
                                                Log.e("PersonalFragment", "Error parseando bitácora", e);
                                            }
                                        }

                                        // MODIFICACIÓN 2: Agregamos el 'telefono' al momento de instanciar OperadorStats
                                        OperadorStats stats = new OperadorStats(nombre, telefono, numViajes, totalKm, totalGasto);
                                        operadores.add(stats);
                                        adapter.notifyDataSetChanged();
                                    },
                                    error -> Log.e("PersonalFragment", "Error en bitácoras", error)
                            );
                            Volley.newRequestQueue(requireContext()).add(requestBitacoras);

                        } catch (Exception e) {
                            Log.e("PersonalFragment", "Error parseando operador", e);
                        }
                    }

                    adapter = new OperadorAdapter(requireContext(), operadores);
                    listOperadores.setAdapter(adapter);

                    Log.d("PersonalFragment", "Adapter asignado con " + operadores.size() + " operadores");
                },
                error -> Log.e("PersonalFragment", "Error en operadores", error)
        );

        Volley.newRequestQueue(requireContext()).add(requestOperadores);
    }
}