package com.example.satori_by_aristo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ViajesAdapter extends ArrayAdapter<ViajeDto> {
    private Context context;
    private List<ViajeDto> viajes;
    private OnViajeActionListener listener;
    private RequestQueue queue;

    // Caché para evitar hacer la misma petición de perfil muchas veces
    private HashMap<String, String> nombresCache = new HashMap<>();

    public interface OnViajeActionListener {
        void onEdit(ViajeDto viaje);
        void onStatusChanged();
    }

    public ViajesAdapter(Context context, List<ViajeDto> viajes, OnViajeActionListener listener) {
        super(context, R.layout.linersito, viajes);
        this.context = context;
        this.viajes = viajes;
        this.listener = listener;
        this.queue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViajeDto viaje = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.linersito, parent, false);
        }

        TextView nombreOperador = convertView.findViewById(R.id.nombreOperador);
        TextView destinoOperador = convertView.findViewById(R.id.destinoOperador); // El nuevo TextView del XML
        ImageView imgEnviado = convertView.findViewById(R.id.enviado);
        ImageView imgProgreso = convertView.findViewById(R.id.oprogreso);
        ImageView btnEdit = convertView.findViewById(R.id.lapiz);
        ImageView btnDelete = convertView.findViewById(R.id.bote);

        // --- INICIO DE LO NUEVO: Destino y Nombre del Perfil ---

        // 1. Mostrar el destino
        if (viaje.getDestino() != null && !viaje.getDestino().isEmpty()) {
            destinoOperador.setText(viaje.getDestino());
        } else {
            destinoOperador.setText("Sin destino");
        }

        // 2. Buscar el nombre en base al teléfono (operador)
        String telefono = viaje.getOperador();

        if (nombresCache.containsKey(telefono)) {
            // Si ya lo tenemos guardado, lo mostramos directo
            nombreOperador.setText(nombresCache.get(telefono));
        } else {
            // Ponemos el número mientras esperamos la respuesta de la API
            nombreOperador.setText(telefono);

            String urlPerfil = context.getString(R.string.base_url) + "perfil/" + telefono;
            JsonObjectRequest requestPerfil = new JsonObjectRequest(Request.Method.GET, urlPerfil, null,
                    response -> {
                        String nombre = response.optString("nombre", telefono);
                        nombresCache.put(telefono, nombre); // Guardar en caché
                        nombreOperador.setText(nombre);     // Actualizar el texto
                    },
                    error -> {
                        nombresCache.put(telefono, telefono); // Si falla, dejamos el número
                    }
            );
            queue.add(requestPerfil);
        }

        // --- FIN DE LO NUEVO ---

        // --- TU LÓGICA DE ICONOS ORIGINAL INTACTA ---

        //  enviado
        if (viaje.getEnviado() != null) {
            if (viaje.getEnviado() == 1) {
                imgEnviado.setImageResource(R.drawable.triangulor);
            } else if (viaje.getEnviado() == 2) {
                imgEnviado.setImageResource(R.drawable.doblecirculo);
            }
        }

        //  iniciado
        if (viaje.getIniciado() != null && viaje.getIniciado() == 1) {
            imgProgreso.setImageResource(R.drawable.triangulo);
        }

        // Acción de enviar (solo si no está enviado)
        nombreOperador.setOnClickListener(v -> {
            if (viaje.getEnviado() != null && viaje.getEnviado() == 2) {
                Toast.makeText(context, "Este viaje ya fue enviado", Toast.LENGTH_SHORT).show();
            } else {
                actualizarEstadoEnviado(viaje);
            }
        });

        //  editar (nota si enviado es 2 no se puede editar ni borrar)
        btnEdit.setOnClickListener(v -> {
            if (viaje.getEnviado() != null && viaje.getEnviado() == 2) {
                Toast.makeText(context, "No se puede editar un viaje enviado", Toast.LENGTH_SHORT).show();
            } else {
                listener.onEdit(viaje);
            }
        });

        //lo mismo de arriba si enviado es 2 no se podra borrar
        btnDelete.setOnClickListener(v -> {
            if (viaje.getEnviado() != null && viaje.getEnviado() == 2) {
                Toast.makeText(context, "No se puede eliminar un viaje enviado", Toast.LENGTH_SHORT).show();
            } else {
                eliminarViaje(viaje.getFolio());
            }
        });

        return convertView;
    }

    private void actualizarEstadoEnviado(ViajeDto viaje) {
        String url = context.getString(R.string.base_url) + "viaje/" + viaje.getFolio();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("operador", viaje.getOperador());
            jsonBody.put("enviado", 2);
            jsonBody.put("iniciado", viaje.getIniciado());
            jsonBody.put("fecha", viaje.getFecha());
            jsonBody.put("password", viaje.getPassword());

            if (viaje.getCliente() != null) {
                jsonBody.put("cliente", viaje.getCliente());
            }
            if (viaje.getDestino() != null) {
                jsonBody.put("destino", viaje.getDestino());
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                    response -> {
                        Toast.makeText(context, "Viaje enviado con éxito", Toast.LENGTH_SHORT).show();
                        listener.onStatusChanged(); // Refrescar lista
                    },
                    error -> Toast.makeText(context, "Error al enviar viaje", Toast.LENGTH_SHORT).show()
            );
            queue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void eliminarViaje(Integer folio) {
        String url = context.getString(R.string.base_url) + "viaje/" + folio;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(context, "Viaje eliminado", Toast.LENGTH_SHORT).show();
                    listener.onStatusChanged(); // Refrescar lista
                },
                error -> Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }
}