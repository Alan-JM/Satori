package com.example.satori_by_aristo.Bitacoras.Operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.satori_by_aristo.R;
import com.example.satori_by_aristo.ViajeDto;

import java.util.List;

public class ViajeopAdapter extends ArrayAdapter<ViajeDto> {
    private Context context;
    private List<ViajeDto> viajes;

    public ViajeopAdapter(Context context, List<ViajeDto> viajes) {
        super(context, R.layout.linersito_dos, viajes);
        this.context = context;
        this.viajes = viajes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Obtener el objeto de datos para esta posición
        ViajeDto viaje = getItem(position);

        // Comprobar si una vista existente se está reutilizando, de lo contrario inflar la vista
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.linersito_dos, parent, false);
        }

        // Referencias a los TextViews del layout linersito_dos
        TextView tvFolio = convertView.findViewById(R.id.tvFolioOp);
        TextView tvContra = convertView.findViewById(R.id.tvContraOp);
        TextView tvFecha = convertView.findViewById(R.id.tvFechaEntregaOp);

        // Referencias a los ImageViews para los estados
        ImageView imgEnviado = convertView.findViewById(R.id.imgEnviadoOp);
        ImageView imgProgreso = convertView.findViewById(R.id.imgProgresoOp);

        // Llenar los datos
        if (viaje != null) {
            tvFolio.setText("Folio: " + viaje.getFolio());
            tvContra.setText("Contraseña: " + viaje.getPassword());

            // Formatear la fecha (reemplaza la T del LocalDateTime por un espacio)
            String fechaOriginal = viaje.getFecha();
            if (fechaOriginal != null && !fechaOriginal.isEmpty()) {
                String fechaLimpia = fechaOriginal.replace("T", " ");
                tvFecha.setText(fechaLimpia);
            } else {
                tvFecha.setText("Sin fecha");
            }

            // --- LÓGICA DE ICONOS SEGÚN ESTADOS (PDF) ---

            // Estado ENVIADO: Si es 2, mostrar doble círculo
            if (viaje.getEnviado() != null && viaje.getEnviado() == 2) {
                imgEnviado.setImageResource(R.drawable.doblecirculo);
            } else {
                // Por defecto o estado 1
                imgEnviado.setImageResource(R.drawable.triangulor);
            }

            // Estado INICIADO: Lógica de 3 estados
            if (viaje.getIniciado() != null) {
                switch (viaje.getIniciado()) {
                    case 1:
                        // Iniciado etapa 1
                        imgProgreso.setImageResource(R.drawable.triangulo);
                        break;
                    case 2:
                        // Iniciado etapa 2 (completado/en camino)
                        imgProgreso.setImageResource(R.drawable.doblecirculo);
                        break;
                    case 3:
                        // Error o cancelación
                        imgProgreso.setImageResource(R.drawable.tache);
                        break;
                    default:
                        imgProgreso.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        }

        return convertView;
    }
}
