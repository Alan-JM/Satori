package com.example.satori_by_aristo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.satori_by_aristo.Bitacoras.Operador.SesionUsuario;
import com.example.satori_by_aristo.Bitacoras.Administrador.SesionAdmin;

public class PerfilFragment extends Fragment {

     TextView rolito, nombreito, telefonoito, correito;
     CardView cardOperadores, cardAdmin;


     TextView telefonoOperadorito, nombreOperadorito;
     TextView adminito, telefonoAdminito;
    Button botoncito;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        rolito = view.findViewById(R.id.rolito);
        nombreito = view.findViewById(R.id.nombreito);
        telefonoito = view.findViewById(R.id.telefonoito);
        correito = view.findViewById(R.id.correito);

        cardOperadores = view.findViewById(R.id.card_operadores);
        cardAdmin = view.findViewById(R.id.card_admin);

        telefonoOperadorito = view.findViewById(R.id.telefono_operadorito);
        nombreOperadorito = view.findViewById(R.id.nombre_operadorito);

        adminito = view.findViewById(R.id.adminito);
        telefonoAdminito = view.findViewById(R.id.telefono_adminito);

        botoncito = view.findViewById(R.id.btnCerrarSesion);
        botoncito.setOnClickListener(v -> {
            SesionActual.obtenerInstancia().cerrarSesion();
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        // Control de visibilidad y llenado según rol
        if (SesionActual.obtenerInstancia().esOperador()) {
            rolito.setText("Operador");

            // Recuadro principal: datos del operador (sesión actual)
            nombreito.setText("Nombre: " + SesionActual.obtenerInstancia().getNombreUsuario());
            telefonoito.setText("Teléfono: " + SesionActual.obtenerInstancia().getTelefono());
            correito.setText("Correo: " + SesionActual.obtenerInstancia().getCorreoUsuario());

            // Tabla: mostrar admin en turno
            String telAdmin = SesionUsuario.getTelefonoAdmin();
            telefonoOperadorito.setText(telAdmin);
            cargarPerfilPorTelefono(telAdmin, nombreOperadorito);

            cardOperadores.setVisibility(View.VISIBLE);
            cardAdmin.setVisibility(View.GONE);

        } else if (SesionActual.obtenerInstancia().esAdministrador()) {
            rolito.setText("Administrador");

            // Recuadro principal: datos del admin (sesión actual)
            nombreito.setText("Nombre: " + SesionActual.obtenerInstancia().getNombreUsuario());
            telefonoito.setText("Teléfono: " + SesionActual.obtenerInstancia().getTelefono());
            correito.setText("Correo: " + SesionActual.obtenerInstancia().getCorreoUsuario());

            // Tabla: mostrar operadores (ejemplo fijo por ahora)
            adminito.setText("Operador X");
            telefonoAdminito.setText("555-9999");

            cardAdmin.setVisibility(View.VISIBLE);
            cardOperadores.setVisibility(View.GONE);

        } else if (SesionActual.obtenerInstancia().esSupervisor()) {
            rolito.setText("Supervisor");

            // Recuadro principal: datos del supervisor
            nombreito.setText("Nombre: " + SesionActual.obtenerInstancia().getNombreUsuario());
            telefonoito.setText("Teléfono: " + SesionActual.obtenerInstancia().getTelefono());
            correito.setText("Correo: " + SesionActual.obtenerInstancia().getCorreoUsuario());

            cardOperadores.setVisibility(View.GONE);
            cardAdmin.setVisibility(View.GONE);
        }

        return view;
    }

    private void cargarPerfilPorTelefono(String telefono, TextView destinoNombre) {
        if (telefono == null || telefono.isEmpty()) {
            Toast.makeText(requireContext(), "Teléfono no disponible en sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = getString(R.string.base_url) + "perfil/" + telefono;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String nombre = response.optString("nombre", "N/A");
                    destinoNombre.setText(nombre);
                },
                error -> {
                    Toast.makeText(requireContext(),
                            "Error cargando perfil: " + error.toString(),
                            Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
}