package com.example.satori_by_aristo.Liquidaciones;

import java.math.BigDecimal;
import java.sql.Date;

public class Liquidacion {
    private String idFolio;
    private Date fecha;
    private String operador;
    private BigDecimal bonoExt;
    private String resumen;
    private String telefonoAdmin;



    // Getters
    public String getIdFolio() {
        return idFolio;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getOperador() {
        return operador;
    }

    public BigDecimal getBonoExt() {
        return bonoExt;
    }

    public String getResumen() {
        return resumen;
    }

    public String getTelefonoAdmin() {
        return telefonoAdmin;
    }

    // Setters
    public void setIdFolio(String idFolio) {
        this.idFolio = idFolio;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public void setBonoExt(BigDecimal bonoExt) {
        this.bonoExt = bonoExt;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public void setTelefonoAdmin(String telefonoAdmin) {
        this.telefonoAdmin = telefonoAdmin;
    }
}