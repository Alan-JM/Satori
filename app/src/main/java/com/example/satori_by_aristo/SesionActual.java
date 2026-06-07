package com.example.satori_by_aristo;

public class SesionActual {
    private static SesionActual instancia;
    private Rol rolActual;
    private String nombreUsuario;
    private String telefono;
    private String correoUsuario; // 🔹 nuevo campo

    // Constructor privado para patrón Singleton
    private SesionActual() { }

    // Obtener la instancia única
    public static SesionActual obtenerInstancia() {
        if (instancia == null) {
            instancia = new SesionActual();
        }
        return instancia;
    }

    // Iniciar sesión con datos completos
    public void iniciarSesion(String telefono, String nombre, String correo, Rol rol) {
        this.telefono = telefono;
        this.nombreUsuario = nombre;
        this.correoUsuario = correo;
        this.rolActual = rol;
    }

    // Cerrar sesión y limpiar datos
    public void cerrarSesion() {
        this.telefono = null;
        this.nombreUsuario = null;
        this.correoUsuario = null;
        this.rolActual = null;
    }

    // Getters
    public Rol getRolActual() {
        return rolActual;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    // Métodos de conveniencia para roles
    public boolean esAdministrador() {
        return rolActual == Rol.ADMINISTRADOR;
    }

    public boolean esOperador() {
        return rolActual == Rol.OPERADOR;
    }

    public boolean esSupervisor() {
        return rolActual == Rol.JEFE; // Supervisor se maneja como rol JEFE
    }

    public boolean haySesionActiva() {
        return rolActual != null;
    }
}