package com.example.satori_by_aristo.Anticipos.Administradores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Anticipos.Anticipo;
import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.SesionActual;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VerAnticipos extends Fragment {

    private ListView listView;
    private AnticipoAdapter adapter;
    private final List<Anticipo> anticiposRegistrados = new ArrayList<>();
    private String BASE_URL;

    private Switch switchVerTodos;
    private boolean mostrarProcesados = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ver_anticipos, container, false);

        BASE_URL = getString(R.string.base_url);

        listView = view.findViewById(R.id.anticiposRegistrados);
        adapter = new AnticipoAdapter(requireContext(), anticiposRegistrados);
        listView.setAdapter(adapter);

        switchVerTodos = view.findViewById(R.id.switchVerTodos);
        switchVerTodos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mostrarProcesados = isChecked;
            if (isChecked) {
                switchVerTodos.setThumbResource(R.drawable.ic_ve);   // ojo abierto
            } else {
                switchVerTodos.setThumbResource(R.drawable.ic_nove); // ojo tachado
            }
            cargarAnticiposDesdeServidor();
        });

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Anticipo anticipo = anticiposRegistrados.get(position);
            mostrarAccionesAnticipo(anticipo);
        });

        return view;
    }

    private void mostrarAccionesAnticipo(Anticipo anticipo) {
        AccionesAnticipoAdminFragment fragment = new AccionesAnticipoAdminFragment();
        fragment.setAnticipo(anticipo);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarAnticiposDesdeServidor();
    }

    private void cargarAnticiposDesdeServidor() {
        String url = BASE_URL + "anticipo";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    anticiposRegistrados.clear();
                    String telefonoSesion = SesionActual.obtenerInstancia().getTelefono();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            String telefonoAdmin = obj.optString("telefonoAdmin", "");
                            String telefono = obj.optString("telefono", "");
                            String telefonop = obj.optString("telefonop", "");

                            int confirmacion = obj.optInt("confirmacion", 0);

                            if (telefonoSesion != null &&
                                    (telefonoSesion.equals(telefonoAdmin) || telefonoSesion.equals(telefonop))) {

                                // Solo mostrar confirmación 1 si el switch está apagado
                                if (!mostrarProcesados && confirmacion == 1) {
                                    anticiposRegistrados.add(crearAnticipo(obj, telefonoAdmin, telefono, telefonop, confirmacion));
                                }
                                // Mostrar confirmación 2 y 3 si el switch está encendido
                                else if (mostrarProcesados && (confirmacion == 2 || confirmacion == 3)) {
                                    anticiposRegistrados.add(crearAnticipo(obj, telefonoAdmin, telefono, telefonop, confirmacion));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(getContext(), "Error cargando anticipos", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private Anticipo crearAnticipo(JSONObject obj, String telefonoAdmin, String telefono, String telefonop, int confirmacion) throws JSONException {
        int idFolio = obj.optInt("idFolio", -1);
        return new Anticipo(
                idFolio,
                obj.optString("fecha", null),
                obj.optString("unidadTrans", null),
                obj.optString("operador", null),
                null,
                obj.optDouble("importe", 0.0),
                obj.optString("concepto", null),
                obj.optString("observaciones", null),
                confirmacion,
                telefonoAdmin,
                telefono,
                telefonop
        );
    }
}
