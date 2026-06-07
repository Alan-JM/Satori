package com.example.satori_by_aristo.Anticipos;

public class Anticipo {
    private Integer idFolio;
    private String fecha;
    private String unidadTrans;
    private String operador;
    private String nombreOperador;
    private double importe;
    private String concepto;
    private String observaciones;
    private Integer confirmacion;
    private String telefonoAdmin;
    private String telefono;
    private String telefonop;
    private boolean seleccionado = false;

    public Anticipo(Integer idFolio, String fecha, String unidadTrans, String operador,
                    String nombreOperador, double importe, String concepto,
                    String observaciones, Integer confirmacion,
                    String telefonoAdmin, String telefono, String telefonop) {
        this.idFolio = idFolio;
        this.fecha = fecha;
        this.unidadTrans = unidadTrans;
        this.operador = operador;
        this.nombreOperador = nombreOperador;
        this.importe = importe;
        this.concepto = concepto;
        this.observaciones = observaciones;
        this.confirmacion = confirmacion;
        this.telefonoAdmin = telefonoAdmin;
        this.telefono = telefono;
        this.telefonop = telefonop;
    }

    public Integer getIdFolio() { return idFolio; }
    public String getFecha() { return fecha; }
    public String getUnidadTrans() { return unidadTrans; }
    public String getOperador() { return operador; }
    public String getNombreOperador() { return nombreOperador; }
    public double getImporte() { return importe; }
    public String getConcepto() { return concepto; }
    public String getObservaciones() { return observaciones; }
    public Integer getConfirmacion() { return confirmacion; }
    public String getTelefonoAdmin() { return telefonoAdmin; }
    public String getTelefono() { return telefono; }
    public String getTelefonop() { return telefonop; }

    public String getColor() {
        if (confirmacion == null) return "rojo";
        switch (confirmacion) {
            case 0: return "gris";
            case 1: return "amarillo";
            case 2: return "verde";
            case 3: return "rojo";
            default: return "rojo";
        }
    }

    public void setIdFolio(Integer idFolio) { this.idFolio = idFolio; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setUnidadTrans(String unidadTrans) { this.unidadTrans = unidadTrans; }
    public void setOperador(String operador) { this.operador = operador; }
    public void setNombreOperador(String nombreOperador) { this.nombreOperador = nombreOperador; }
    public void setImporte(double importe) { this.importe = importe; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setConfirmacion(Integer confirmacion) { this.confirmacion = confirmacion; }
    public void setTelefonoAdmin(String telefonoAdmin) { this.telefonoAdmin = telefonoAdmin; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setTelefonop(String telefonop) { this.telefonop = telefonop; }

    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
}
