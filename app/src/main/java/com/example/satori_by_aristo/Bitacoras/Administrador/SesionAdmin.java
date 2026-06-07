package com.example.satori_by_aristo.Bitacoras.Administrador;

public class SesionAdmin {
    private static String telefonoAdmin;

    // Guardar el teléfono del admin en sesión
    public static void iniciarSesion(String telefono) {
        telefonoAdmin = telefono;
    }

    // Obtener el teléfono actual del admin en sesión
    public static String getTelefonoAdmin() {
        return telefonoAdmin;
    }

    // Cerrar sesión del admin
    public static void cerrarSesion() {
        telefonoAdmin = null;
    }
}