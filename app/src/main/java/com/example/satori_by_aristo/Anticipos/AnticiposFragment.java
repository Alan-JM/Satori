package com.example.satori_by_aristo.Anticipos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Bitacoras.Administrador.SesionAdmin;
import com.example.satori_by_aristo.SesionActual;
import com.example.satori_by_aristo.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnticiposFragment extends Fragment
        implements AccionesAnticipoFragment.OnAccionAnticipoListener {

    private ListView listView;
    private MaterialButton btnRegistrar;
    private AnticipoAdapter adapter;

    private final List<Anticipo> anticiposRegistrados = new ArrayList<>();
    private String BASE_URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anticipos, container, false);

        BASE_URL = getString(R.string.base_url);

        listView = view.findViewById(R.id.anticiposRegistrados);
        btnRegistrar = view.findViewById(R.id.RegistrarAnticipo);

        adapter = new AnticipoAdapter(requireContext(), anticiposRegistrados);
        listView.setAdapter(adapter);

        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NuevoAnticipo.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Anticipo anticipo = anticiposRegistrados.get(position);
            mostrarFragmentAcciones(anticipo);
        });

        return view;
    }

    private void mostrarFragmentAcciones(Anticipo anticipo) {
        AccionesAnticipoFragment fragment = new AccionesAnticipoFragment();
        fragment.setAnticipo(anticipo);
        fragment.setOnAccionAnticipoListener(this);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onModificar(Anticipo anticipo) {
        if (anticipo.getConfirmacion() != null && anticipo.getConfirmacion() == 0) {
            Intent intent = new Intent(getActivity(), NuevoAnticipo.class);
            intent.putExtra("anticipo_idFolio", anticipo.getIdFolio());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "No se puede editar un anticipo ya enviado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEnviar(Anticipo anticipo) {
        if (anticipo.getConfirmacion() != null && anticipo.getConfirmacion() == 0) {
            anticipo.setConfirmacion(1);
            anticipo.setTelefonoAdmin(SesionAdmin.getTelefonoAdmin());
            anticipo.setTelefono(SesionAdmin.getTelefonoAdmin());
            anticipo.setTelefonop(SesionActual.obtenerInstancia().getTelefono());

            String url = BASE_URL + "anticipo/" + anticipo.getIdFolio();

            JSONObject anticipoJson = new JSONObject();
            try {
                anticipoJson.put("idFolio", anticipo.getIdFolio());
                anticipoJson.put("fecha", anticipo.getFecha());
                anticipoJson.put("unidadTrans", anticipo.getUnidadTrans());
                anticipoJson.put("operador", anticipo.getOperador());
                anticipoJson.put("importe", anticipo.getImporte());
                anticipoJson.put("concepto", anticipo.getConcepto());
                anticipoJson.put("observaciones", anticipo.getObservaciones());
                anticipoJson.put("confirmacion", anticipo.getConfirmacion());
                anticipoJson.put("telefonoAdmin", anticipo.getTelefonoAdmin());
                anticipoJson.put("telefono", anticipo.getTelefono());
                anticipoJson.put("telefonop", anticipo.getTelefonop());
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    anticipoJson,
                    response -> {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Anticipo actualizado correctamente", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    },
                    error -> Toast.makeText(getContext(), "Error actualizando anticipo", Toast.LENGTH_SHORT).show()
            );
            queue.add(request);
        } else {
            Toast.makeText(getContext(), "Este anticipo ya fue enviado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEliminar(Anticipo anticipo) {
        if (anticipo.getConfirmacion() != null && anticipo.getConfirmacion() == 0) {
            String url = BASE_URL + "anticipo/" + anticipo.getIdFolio();

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            StringRequest request = new StringRequest(
                    Request.Method.DELETE,
                    url,
                    response -> {
                        anticiposRegistrados.remove(anticipo);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Anticipo eliminado en servidor", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    },
                    error -> Toast.makeText(getContext(), "Error eliminando anticipo", Toast.LENGTH_SHORT).show()
            );
            queue.add(request);
        } else {
            Toast.makeText(getContext(), "No se puede eliminar un anticipo ya enviado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onVolver() {
        getParentFragmentManager().popBackStack();
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

                            if (telefonoSesion != null &&
                                    (telefonoSesion.equals(telefonoAdmin) || telefonoSesion.equals(telefonop))) {

                                int idFolio = obj.optInt("idFolio", -1);
                                Anticipo anticipo = new Anticipo(
                                        idFolio,
                                        obj.optString("fecha", null),
                                        obj.optString("unidadTrans", null),
                                        obj.optString("operador", null),
                                        null,
                                        obj.optDouble("importe", 0.0),
                                        obj.optString("concepto", null),
                                        obj.optString("observaciones", null),
                                        obj.optInt("confirmacion", 0),
                                        telefonoAdmin,
                                        telefono,
                                        telefonop
                                );
                                anticiposRegistrados.add(anticipo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(getContext(), "Error cargando anticipos", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}
