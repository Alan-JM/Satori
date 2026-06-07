package com.example.satori_by_aristo;

import android.os.Bundle;
import android.view.Menu;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.satori_by_aristo.Anticipos.Administradores.VerAnticipos;
import com.example.satori_by_aristo.Anticipos.AnticiposFragment;
import com.example.satori_by_aristo.Bitacoras.Administrador.BitacoraAdminFragment;
import com.example.satori_by_aristo.Bitacoras.Operador.BitacoraFragment;
import com.example.satori_by_aristo.Bitacoras.Operador.Estadisticas;
import com.example.satori_by_aristo.Bitacoras.Operador.Liquidaciones;
import com.example.satori_by_aristo.Bitacoras.Operador.RechazosBitacora;
import com.example.satori_by_aristo.Bitacoras.Operador.ViajeOp;
import com.example.satori_by_aristo.Liquidaciones.LiquidacionFragment;
import com.example.satori_by_aristo.Supervisor.Supervisor;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class Principal extends AppCompatActivity {

    private DrawerLayout cajonDeNavegacion;
    private NavigationView vistaDeNavegacion;
    private MaterialToolbar barraDeHerramientas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        // Inicializar vistas
        cajonDeNavegacion = findViewById(R.id.drawer_layout);
        vistaDeNavegacion = findViewById(R.id.nav_view);
        barraDeHerramientas = findViewById(R.id.top_app_bar);

        // Ajustar insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(cajonDeNavegacion, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(barraDeHerramientas);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ActionBarDrawerToggle alternador = new ActionBarDrawerToggle(
                this,
                cajonDeNavegacion,
                barraDeHerramientas,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        cajonDeNavegacion.addDrawerListener(alternador);
        alternador.syncState();

        configurarMenuSegunRol();

        vistaDeNavegacion.setNavigationItemSelectedListener(item -> {
            Fragment fragmento = null;
            String titulo = "";

            int idElemento = item.getItemId();

            if (idElemento == R.id.nav_bitacora) {
                if (SesionActual.obtenerInstancia().esAdministrador()) {
                    fragmento = new BitacoraAdminFragment();
                    titulo = "Revisar Bitácoras";
                } else {
                    fragmento = new BitacoraFragment();
                    titulo = "Mis Bitácoras";
                }
            } else if (idElemento == R.id.nav_supervisor) {
                fragmento = new Supervisor();
                titulo = getString(R.string.menu_supervisor);
            } else if (idElemento == R.id.nav_anticipos) {
                if (SesionActual.obtenerInstancia().esAdministrador()) {
                    fragmento = new VerAnticipos();
                    titulo = "Revisar Anticipos";
                } else {
                    fragmento = new AnticiposFragment();
                    titulo = "Mis Anticipos";
                }
            } else if (idElemento == R.id.nav_perfil) {
                fragmento = new PerfilFragment();
                titulo = getString(R.string.menu_perfil);
            } else if (idElemento == R.id.nav_liquidacion) {
                fragmento = new LiquidacionFragment();
                titulo = getString(R.string.menu_liquidacion);
            } else if (idElemento == R.id.nav_administrador) {
                fragmento = new AdministradorFragment();
                titulo = getString(R.string.menu_admin);
            } else if (idElemento == R.id.nav_rechazo) {
                fragmento = new RechazosBitacora();
                titulo = getString(R.string.menu_rechazo);
            } else if (idElemento == R.id.nav_viajesop) {
                fragmento = new ViajeOp();
                titulo = getString(R.string.menu_viajesop);
            } else if (idElemento == R.id.nav_Liquidaciones){
                fragmento = new Liquidaciones();
                titulo = getString(R.string.menu_liquidacionop);
            }else if (idElemento ==R.id.nav_estadisticas){
                fragmento = new Estadisticas();
                titulo = getString(R.string.menu_estadistacas);
            }

            if (fragmento != null) {
                reemplazarFragmento(fragmento, titulo);
            }
            cajonDeNavegacion.closeDrawer(GravityCompat.START);
            return true;
        });

        // Fragmento inicial
        if (savedInstanceState == null) {
            String openFragment = getIntent().getStringExtra("open_fragment");
            if ("bitacora".equals(openFragment)) {
                vistaDeNavegacion.setCheckedItem(R.id.nav_bitacora);
                if (SesionActual.obtenerInstancia().esAdministrador()) {
                    reemplazarFragmento(new BitacoraAdminFragment(), "Revisar Bitácoras");
                } else {
                    reemplazarFragmento(new BitacoraFragment(), "Mis Bitácoras");
                }
            } else {
                vistaDeNavegacion.setCheckedItem(R.id.nav_perfil);
                reemplazarFragmento(new PerfilFragment(), getString(R.string.menu_perfil));
            }
        }

    }

    private void configurarMenuSegunRol() {
        Menu menu = vistaDeNavegacion.getMenu();

        if (SesionActual.obtenerInstancia().esAdministrador()) {
            menu.findItem(R.id.nav_bitacora).setTitle("Revisar Bitácoras");
            menu.findItem(R.id.nav_anticipos).setTitle("Revisar anticipos");
            menu.findItem(R.id.nav_liquidacion).setVisible(true);
            menu.findItem(R.id.nav_administrador).setVisible(true);
            menu.findItem(R.id.nav_rechazo).setVisible(false);
            menu.findItem(R.id.nav_estadisticas).setVisible(true);
            menu.findItem(R.id.nav_supervisor).setVisible(false);
            menu.findItem(R.id.nav_viajesop).setVisible(false);
            menu.findItem(R.id.nav_Liquidaciones).setVisible(false);
        } else if (SesionActual.obtenerInstancia().esOperador()) {
            menu.findItem(R.id.nav_bitacora).setTitle("Mis Bitácoras");
            menu.findItem(R.id.nav_anticipos).setTitle("Mis Anticipos");
            menu.findItem(R.id.nav_liquidacion).setVisible(false);
            menu.findItem(R.id.nav_administrador).setVisible(false);
            menu.findItem(R.id.nav_supervisor).setVisible(false);
            menu.findItem(R.id.nav_rechazo).setVisible(true);
            menu.findItem(R.id.nav_viajesop).setVisible(true);
            menu.findItem(R.id.nav_estadisticas).setVisible(false);
            menu.findItem(R.id.nav_Liquidaciones).setVisible(true);

        } else if (SesionActual.obtenerInstancia().esSupervisor()) {
            menu.findItem(R.id.nav_supervisor).setVisible(true);
            menu.findItem(R.id.nav_anticipos).setVisible(false);
            menu.findItem(R.id.nav_liquidacion).setVisible(false);
            menu.findItem(R.id.nav_administrador).setVisible(false);
            menu.findItem(R.id.nav_bitacora).setVisible(false);
            menu.findItem(R.id.nav_rechazo).setVisible(false);
            menu.findItem(R.id.nav_estadisticas).setVisible(false);
            menu.findItem(R.id.nav_viajesop).setVisible(false);
            menu.findItem(R.id.nav_Liquidaciones).setVisible(false);

        }
    }

    private void reemplazarFragmento(Fragment fragmento, String titulo) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmento)
                .commit();
        setTitle(titulo);
    }
}
