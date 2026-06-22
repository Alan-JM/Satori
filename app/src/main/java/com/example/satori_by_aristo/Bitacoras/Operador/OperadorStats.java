package com.example.satori_by_aristo.Bitacoras.Operador;

public class OperadorStats {
    private String nombre;
    private String telefono;
    private int numViajes;
    private double totalKm;
    private double totalGasto;
    private double promKm;
    private double promGasto;

    public OperadorStats(String nombre, String telefono, int numViajes, double totalKm, double totalGasto) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.numViajes = numViajes;
        this.totalKm = totalKm;
        this.totalGasto = totalGasto;
        this.promKm = numViajes > 0 ? totalKm / numViajes : 0;
        this.promGasto = numViajes > 0 ? totalGasto / numViajes : 0;
    }

    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public int getNumViajes() { return numViajes; }
    public double getTotalKm() { return totalKm; }
    public double getTotalGasto() { return totalGasto; }
    public double getPromKm() { return promKm; }
    public double getPromGasto() { return promGasto; }
}