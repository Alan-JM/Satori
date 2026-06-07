package com.example.satori_by_aristo.Supervisor;

public class Registro {
    private String telefono;
    private String nombre;
    private String correo;
    private int uso;
    private String rol; // "admin" o "operador"

    public Registro(String telefono, String nombre, String correo, int uso, String rol) {
        this.telefono = telefono;
        this.nombre = nombre;
        this.correo = correo;
        this.uso = uso;
        this.rol = rol;
    }

    public String getTelefono() { return telefono; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public int getUso() { return uso; }
    public String getRol() { return rol; }
}