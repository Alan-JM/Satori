package com.example.satori_by_aristo.Supervisor;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetalleRegistroFragment extends Fragment {

    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_TELEFONO = "telefono";
    private static final String ARG_CORREO = "correo";

    private String nombre;
    private String telefono;
    private String correo;

    private TextView nombreDetalle, telefonoDetalle, correoDetalle, claveGenerada;
    private RadioGroup radioGroup;
    private RadioButton radioAdmin, radioOperador;
    private Button btnGenerarClave, btnEnviarCorreo;
    private Spinner spinnerOpciones;

    private boolean esAdmin = false;
    private String clave;

    public static DetalleRegistroFragment newInstance(String nombre, String telefono, String correo) {
        DetalleRegistroFragment fragment = new DetalleRegistroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE, nombre);
        args.putString(ARG_TELEFONO, telefono);
        args.putString(ARG_CORREO, correo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombre = getArguments().getString(ARG_NOMBRE);
            telefono = getArguments().getString(ARG_TELEFONO);
            correo = getArguments().getString(ARG_CORREO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_registro, container, false);

        nombreDetalle = view.findViewById(R.id.nombreDetalle);
        telefonoDetalle = view.findViewById(R.id.telefonoDetalle);
        correoDetalle = view.findViewById(R.id.correoDetalle);
        claveGenerada = view.findViewById(R.id.claveGenerada);

        radioGroup = view.findViewById(R.id.radioGroup);
        radioAdmin = view.findViewById(R.id.radioAdmin);
        radioOperador = view.findViewById(R.id.radioOperador);

        btnGenerarClave = view.findViewById(R.id.btnGenerarClave);
        btnEnviarCorreo = view.findViewById(R.id.btnEnviarCorreo);
        spinnerOpciones = view.findViewById(R.id.spinnerOpciones);

        // Mostrar datos
        nombreDetalle.setText(nombre);
        telefonoDetalle.setText(telefono);
        correoDetalle.setText(correo);

        // Cargar administradores en spinner
        cargarAdmins();

        //   Listener del RadioGroup (fuera del botón)
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioAdmin) {
                spinnerOpciones.setVisibility(View.GONE); // ocultar spinner
            } else if (checkedId == R.id.radioOperador) {
                spinnerOpciones.setVisibility(View.VISIBLE); // mostrar spinner
            }
        });

        // Generar clave
        btnGenerarClave.setOnClickListener(v -> {
            clave = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            claveGenerada.setText("Clave generada: " + clave);
        });

//  Enlazar botón de enviar correo
        btnEnviarCorreo.setOnClickListener(v -> enviarCorreo());

        return view;
    }

    private void enviarCorreo() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        esAdmin = (selectedId == R.id.radioAdmin);

         Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{correo});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Notificación");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hola " + nombre + ", tu clave es: " + clave);

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "No hay aplicación para enviar correo", Toast.LENGTH_SHORT).show();
        }

         actualizarRegistro();

         if (esAdmin) {
            guardarAdministrador();
        } else {
            if (spinnerOpciones.getAdapter() != null && spinnerOpciones.getAdapter().getCount() > 1) {
                guardarOperador();
            } else {
                Toast.makeText(requireContext(), "No hay administradores disponibles aún", Toast.LENGTH_SHORT).show();
            }
        }

         requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new Supervisor())
                .commit();
    }

    private void actualizarRegistro() {
        String url = getString(R.string.base_url) + "registro";

        JSONObject registroJson = new JSONObject();
        try {
            registroJson.put("telefono", telefono);
            registroJson.put("nombre", nombre);
            registroJson.put("correo", correo);
            registroJson.put("enviado", 1); // marcar como enviado
        } catch (JSONException e) { e.printStackTrace(); }

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, registroJson,
                response -> Toast.makeText(requireContext(), "Registro actualizado (enviado=1)", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(requireContext(), "Error actualizando registro", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private void guardarAdministrador() {
        String url = getString(R.string.base_url) + "administrador";

        JSONObject adminJson = new JSONObject();
        try {
            adminJson.put("clave", clave);
            adminJson.put("uso", 0); // uso=0
            adminJson.put("telefono", telefono); // teléfono del registro clicado
        } catch (JSONException e) { e.printStackTrace(); }

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, adminJson,
                response -> Toast.makeText(requireContext(), "Administrador guardado", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(requireContext(), "Error guardando admin", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private void guardarOperador() {
        String telefonoAdmin = spinnerOpciones.getSelectedItem().toString();

        if ("Seleccione".equals(telefonoAdmin)) {
            Toast.makeText(requireContext(), "Debe seleccionar un administrador válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = getString(R.string.base_url) + "operador";

        JSONObject operadorJson = new JSONObject();
        try {
            operadorJson.put("clave", clave);
            operadorJson.put("uso", 0); // uso=0
            operadorJson.put("telefonoAdmin", telefonoAdmin);
            operadorJson.put("telefonoP", telefono);
        } catch (JSONException e) { e.printStackTrace(); }

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, operadorJson,
                response -> Toast.makeText(requireContext(), "Operador guardado", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(requireContext(), "Error guardando operador", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }
    private void cargarAdmins() {
        String url = getString(R.string.base_url) + "perfil"; // endpoint del PerfilController

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> admins = new ArrayList<>();

                    admins.add("Seleccione");

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int rol = obj.optInt("rol", 0);
                            if (rol == 2) { // 🔹 solo admins (rol=2)
                                admins.add(obj.optString("telefono"));
                            }
                        }
                    } catch (JSONException e) { e.printStackTrace(); }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, admins);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerOpciones.setAdapter(adapter);
                },
                error -> Toast.makeText(requireContext(), "Error cargando admins", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}