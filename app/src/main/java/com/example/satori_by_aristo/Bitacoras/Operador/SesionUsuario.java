package com.example.satori_by_aristo.Bitacoras.Operador;

public class SesionUsuario {
    private static String claveOperador;
    private static String telefonoAdmin; // viene de la tabla operadores
    private static String telefonoP;     // viene de la misma tabla operadores (campo telefonop)

    // Clave del operador
    public static String getClaveOperador() {
        return claveOperador;
    }
    public static void setClaveOperador(String claveOperador) {
        SesionUsuario.claveOperador = claveOperador;
    }

    // Teléfono del administrador
    public static String getTelefonoAdmin() {
        return telefonoAdmin;
    }
    public static void setTelefonoAdmin(String telefonoAdmin) {
        SesionUsuario.telefonoAdmin = telefonoAdmin;
    }

    // Teléfono del operador (telefonop en BD)
    public static String getTelefonoP() {
        return telefonoP;
    }
    public static void setTelefonoP(String telefonoP) {
        SesionUsuario.telefonoP = telefonoP;
    }
}