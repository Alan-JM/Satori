package com.example.satori_by_aristo.retrofit;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class BitacoraDto {

    private Integer idFolio;
    private Date fecha;
    private String operador;
    private String unidadEco;
    private String cliente;
    private String destino;
    private String ayudantes;
    private Integer odometroInicial;
    private Integer odometroFinal;
    private Integer distanciaTotal;
    private BigDecimal combustibleConsumido;
    private BigDecimal gastoTCombustible;
    private BigDecimal gastoTCasetas;
    private BigDecimal subTotalT;
    private BigDecimal gastoECombustible;
    private BigDecimal gastoECasetas;
    private BigDecimal gastoEComida;
    private BigDecimal gastoEReparaciones;
    private BigDecimal gastoEManiobras;
    private BigDecimal gastoETransito;
    private BigDecimal gastoEOtros;
    private BigDecimal subTotalE;
    private BigDecimal granTotal;

    private Integer viaje;
    private List<Integer> anticipos;    //
    private String telefonoAdmin;
    private String telefono;     // este viene del operador

    private Integer confirmacion;
    private String liquidacion;

    // Getters y Setters
    public Integer getIdFolio() {
        return idFolio;
    }

    public void setIdFolio(Integer idFolio) {
        this.idFolio = idFolio;
    }

     public Date getFecha() {
        return fecha;
    }


    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getUnidadEco() {
        return unidadEco;
    }

    public void setUnidadEco(String unidadEco) {
        this.unidadEco = unidadEco;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getAyudantes() {
        return ayudantes;
    }

    public void setAyudantes(String ayudantes) {
        this.ayudantes = ayudantes;
    }

    public Integer getOdometroInicial() {
        return odometroInicial;
    }

    public void setOdometroInicial(Integer odometroInicial) {
        this.odometroInicial = odometroInicial;
    }

    public Integer getOdometroFinal() {
        return odometroFinal;
    }

    public void setOdometroFinal(Integer odometroFinal) {
        this.odometroFinal = odometroFinal;
    }

    public Integer getDistanciaTotal() {
        return distanciaTotal;
    }

    public void setDistanciaTotal(Integer distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
    }


    public BigDecimal getCombustibleConsumido() { return combustibleConsumido; }


    public void setCombustibleConsumido(BigDecimal combustibleConsumido) { this.combustibleConsumido = combustibleConsumido; }

    public BigDecimal getGastoTCombustible() {
        return gastoTCombustible;
    }

    public void setGastoTCombustible(BigDecimal gastoTCombustible) {
        this.gastoTCombustible = gastoTCombustible;
    }

    public BigDecimal getGastoTCasetas() {
        return gastoTCasetas;
    }

    public void setGastoTCasetas(BigDecimal gastoTCasetas) {
        this.gastoTCasetas = gastoTCasetas;
    }

    public BigDecimal getSubTotalT() {
        return subTotalT;
    }

    public void setSubTotalT(BigDecimal subTotalT) {
        this.subTotalT = subTotalT;
    }

    public BigDecimal getGastoECombustible() {
        return gastoECombustible;
    }

    public void setGastoECombustible(BigDecimal gastoECombustible) {
        this.gastoECombustible = gastoECombustible;
    }

    public BigDecimal getGastoECasetas() {
        return gastoECasetas;
    }

    public void setGastoECasetas(BigDecimal gastoECasetas) {
        this.gastoECasetas = gastoECasetas;
    }

    public BigDecimal getGastoEComida() {
        return gastoEComida;
    }

    public void setGastoEComida(BigDecimal gastoEComida) {
        this.gastoEComida = gastoEComida;
    }

    public BigDecimal getGastoEReparaciones() {
        return gastoEReparaciones;
    }

    public void setGastoEReparaciones(BigDecimal gastoEReparaciones) {
        this.gastoEReparaciones = gastoEReparaciones;
    }

    public BigDecimal getGastoEManiobras() {
        return gastoEManiobras;
    }

    public void setGastoEManiobras(BigDecimal gastoEManiobras) {
        this.gastoEManiobras = gastoEManiobras;
    }

    public BigDecimal getGastoETransito() {
        return gastoETransito;
    }

    public void setGastoETransito(BigDecimal gastoETransito) {
        this.gastoETransito = gastoETransito;
    }

    public BigDecimal getGastoEOtros() {
        return gastoEOtros;
    }

    public void setGastoEOtros(BigDecimal gastoEOtros) {
        this.gastoEOtros = gastoEOtros;
    }

    public BigDecimal getSubTotalE() {
        return subTotalE;
    }

    public void setSubTotalE(BigDecimal subTotalE) {
        this.subTotalE = subTotalE;
    }

    public BigDecimal getGranTotal() {
        return granTotal;
    }

    public void setGranTotal(BigDecimal granTotal) {
        this.granTotal = granTotal;
    }

    public String getTelefonoAdmin() {
        return telefonoAdmin;
    }

    public void setTelefonoAdmin(String telefonoAdmin) {
        this.telefonoAdmin = telefonoAdmin;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(Integer confirmacion) {
        this.confirmacion = confirmacion;
    }

    public String getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(String liquidacion) {
        this.liquidacion = liquidacion;
    }
    public Integer getViaje() { return viaje; }
    public void setViaje(Integer viaje) { this.viaje = viaje; }



    public List<Integer> getAnticipos() {
        return anticipos;
    }

    public void setAnticipos(List<Integer> anticipos) {
        this.anticipos = anticipos;
    }

}