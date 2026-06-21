package com.example.satori_by_aristo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class crearviaje extends Fragment {
    private EditText etDestino, etCliente, etFecha, etHora, etPass;
    private TextView tvFolio;
    private Spinner spinnerOperadores;
    private Map<String, String> mapaOperadores = new HashMap<>();
    private ViajeDto viajeEditar;
    private RequestQueue queue;

    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crearviaje, container, false);

        queue = Volley.newRequestQueue(requireContext());
        etDestino = v.findViewById(R.id.etDestino);
        etCliente = v.findViewById(R.id.etCliente);
        etFecha = v.findViewById(R.id.etFecha);
        etHora = v.findViewById(R.id.etHora);
        etPass = v.findViewById(R.id.etPassword);
        tvFolio = v.findViewById(R.id.tvFolioAutollenado);
        spinnerOperadores = v.findViewById(R.id.spinnerOperador);

        etFecha.setOnClickListener(view -> mostrarCalendario());
        etHora.setOnClickListener(view -> mostrarReloj());

        llenarSpinnerOperadores();

        if (getArguments() != null) {
            viajeEditar = (ViajeDto) getArguments().getSerializable("viaje");
            cargarDatosEdicion();
        }

        v.findViewById(R.id.atras).setOnClickListener(view ->
                getParentFragmentManager().popBackStack());

        v.findViewById(R.id.crear).setOnClickListener(view -> guardarViaje());
        return v;
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            fechaSeleccionada = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", dayOfMonth);
            etFecha.setText(fechaSeleccionada);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void mostrarReloj() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            horaSeleccionada = String.format("%02d:%02d:00", hourOfDay, minute);
            etHora.setText(horaSeleccionada);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void llenarSpinnerOperadores() {
        String miTelefono = SesionActual.obtenerInstancia().getTelefono();
        String urlPerfiles = getString(R.string.base_url) + "perfil";
        String urlOperadores = getString(R.string.base_url) + "operador";

        JsonArrayRequest requestPerfiles = new JsonArrayRequest(Request.Method.GET, urlPerfiles, null,
                responsePerfiles -> {
                    Map<String, String> nombresPorTelefono = new HashMap<>();
                    try {
                        for (int i = 0; i < responsePerfiles.length(); i++) {
                            JSONObject p = responsePerfiles.getJSONObject(i);
                            nombresPorTelefono.put(p.getString("telefono"), p.getString("nombre"));
                        }

                        JsonArrayRequest requestOps = new JsonArrayRequest(Request.Method.GET, urlOperadores, null,
                                responseOps -> {
                                    ArrayList<String> listaParaSpinner = new ArrayList<>();
                                    mapaOperadores.clear();

                                    try {
                                        for (int j = 0; j < responseOps.length(); j++) {
                                            JSONObject op = responseOps.getJSONObject(j);
                                            if (op.optString("telefonoAdmin").equals(miTelefono)) {
                                                String telP = op.optString("telefonoP");
                                                String nombreReal = nombresPorTelefono.get(telP);
                                                if (nombreReal != null) {
                                                    listaParaSpinner.add(nombreReal);
                                                    mapaOperadores.put(nombreReal, telP);
                                                }
                                            }
                                        }
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                                                android.R.layout.simple_spinner_dropdown_item, listaParaSpinner);
                                        spinnerOperadores.setAdapter(adapter);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, null);
                        queue.add(requestOps);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, null);
        queue.add(requestPerfiles);
    }

    private void guardarViaje() {
        if (fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = getString(R.string.base_url) + "viaje";
        int metodo = (viajeEditar == null) ? Request.Method.POST : Request.Method.PUT;
        if (viajeEditar != null) url += "/" + viajeEditar.getFolio();
        try {
            JSONObject body = new JSONObject();
            String nombreSel = spinnerOperadores.getSelectedItem().toString();
            body.put("operador", mapaOperadores.get(nombreSel));
            body.put("enviado", 1);
            body.put("iniciado", 1);
            body.put("fecha", fechaSeleccionada + "T" + horaSeleccionada);
            body.put("password", etPass.getText().toString());
            body.put("destino", etDestino.getText().toString());
            body.put("cliente", etCliente.getText().toString());

            // Aquí guardamos el teléfono de la sesión actual en la columna administrador
            String telefonoSesion = SesionActual.obtenerInstancia().getTelefono();
            body.put("administrador", telefonoSesion);

            JsonObjectRequest request = new JsonObjectRequest(metodo, url, body,
                    response -> {
                        Toast.makeText(getContext(), "Viaje Guardado", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    },
                    error -> Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show());
            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarDatosEdicion() {
        tvFolio.setText("Folio: " + viajeEditar.getFolio());
        etPass.setText(viajeEditar.getPassword());
        if (viajeEditar.getFecha() != null && viajeEditar.getFecha().contains("T")) {
            String[] partes = viajeEditar.getFecha().split("T");
            fechaSeleccionada = partes[0];
            horaSeleccionada = partes[1];
            etFecha.setText(fechaSeleccionada);
            etHora.setText(horaSeleccionada);
        }
        if (viajeEditar.getDestino() != null) {
            etDestino.setText(viajeEditar.getDestino());
        }
        if (viajeEditar.getCliente() != null) {
            etCliente.setText(viajeEditar.getCliente());
        }
    }

    private void seleccionarOperadorEnSpinner(String telefono) {
        for (int i = 0; i < spinnerOperadores.getCount(); i++) {
            String nombre = spinnerOperadores.getItemAtPosition(i).toString();
            if (mapaOperadores.get(nombre).equals(telefono)) {
                spinnerOperadores.setSelection(i);
                break;
            }
        }
    }
}
