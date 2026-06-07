package com.example.satori_by_aristo.retrofit;

import java.math.BigDecimal;
import java.sql.Date;

public class AnticipoDto {
    private Integer idFolio;
    private Date fecha;
    private String unidadTrans;
    private String operador;
    private BigDecimal importe;
    private String concepto;
    private String observaciones;
    private Integer confirmacion;
    private String telefono;
    private Integer bitacoraid;

    public Integer getBitacoraid() { return bitacoraid; }
    public void setBitacoraid(Integer bitacoraid) { this.bitacoraid = bitacoraid; }

    public Integer getIdFolio() { return idFolio; }
    public void setIdFolio(Integer idFolio) { this.idFolio = idFolio; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getUnidadTrans() { return unidadTrans; }
    public void setUnidadTrans(String unidadTrans) { this.unidadTrans = unidadTrans; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getConfirmacion() { return confirmacion; }
    public void setConfirmacion(Integer confirmacion) { this.confirmacion = confirmacion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
