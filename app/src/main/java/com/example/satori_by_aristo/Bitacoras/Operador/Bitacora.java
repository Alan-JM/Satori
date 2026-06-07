package com.example.satori_by_aristo.Bitacoras.Operador;

import java.util.List;

public class Bitacora {
    private int id;
    private String fecha;
    private String operador;
    private String eco;
    private int viaje;
    private String cliente;
    private String destino;
    private String ayudantes;
    private String color;
    private String motivoRechazo;
    private int confirmacion;

    // Kilometraje
    private double odometroInicial;
    private double odometroFinal;
    private double distanciaTotal;
    private double combustibleConsumido;

    // Conceptos
    private String concepto1;
    private String fecha1;
    private double importe1;
    private String concepto2;
    private String fecha2;
    private double importe2;
    private String concepto3;
    private String fecha3;
    private double importe3;

    // Gastos Tarjeta
    private double combustibleTarjeta;
    private double casetasTarjeta;
    private double subtotalTarjeta;

    // Gastos Efectivo
    private double combustibleEfectivo;
    private double casetasEfectivo;
    private double comida;
    private double reparaciones;
    private double maniobras;
    private double transitosFederal;
    private double otros;
    private double subtotalEfectivo;
    private List<Integer> anticipos;


    private double anticiposTotal;


    public double getAnticiposTotal() {
        return anticiposTotal;
    }

    public void setAnticiposTotal(double anticiposTotal) {
        this.anticiposTotal = anticiposTotal;
    }

    public List<Integer> getAnticipos() {
        return anticipos;
    }

    public void setAnticipos(List<Integer> anticipos) {
        this.anticipos = anticipos;
    }

    private double granTotal;

    private String telefonoAdmin;
    private String liquidacion;
    public Bitacora(int id, String fecha, String operador, String eco,
                    String cliente, String destino, String ayudantes, String color) {
        this.id = id;
        this.fecha = fecha;
        this.operador = operador;
        this.eco = eco;
        this.cliente = cliente;
        this.destino = destino;
        this.ayudantes = ayudantes;
        this.color = color;
        this.motivoRechazo = "";

    }

    public Bitacora() {

    }

    // Getters y Setters básicos
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public String getEco() { return eco; }
    public void setEco(String eco) { this.eco = eco; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getAyudantes() { return ayudantes; }
    public void setAyudantes(String ayudantes) { this.ayudantes = ayudantes; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getViaje() {
        return viaje;
    }

    public void setViaje(int viaje) {
        this.viaje = viaje;
    }
    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    // Getters y Setters de Kilometraje
    public double getOdometroInicial() { return odometroInicial; }
    public void setOdometroInicial(double odometroInicial) { this.odometroInicial = odometroInicial; }

    public double getOdometroFinal() { return odometroFinal; }
    public void setOdometroFinal(double odometroFinal) { this.odometroFinal = odometroFinal; }

    public double getDistanciaTotal() { return distanciaTotal; }
    public void setDistanciaTotal(double distanciaTotal) { this.distanciaTotal = distanciaTotal; }

    public double getCombustibleConsumido() { return combustibleConsumido; }
    public void setCombustibleConsumido(double combustibleConsumido) { this.combustibleConsumido = combustibleConsumido; }

    // Getters y Setters de Conceptos
    public String getConcepto1() { return concepto1; }
    public void setConcepto1(String concepto1) { this.concepto1 = concepto1; }

    public String getFecha1() { return fecha1; }
    public void setFecha1(String fecha1) { this.fecha1 = fecha1; }

    public double getImporte1() { return importe1; }
    public void setImporte1(double importe1) { this.importe1 = importe1; }

    public String getConcepto2() { return concepto2; }
    public void setConcepto2(String concepto2) { this.concepto2 = concepto2; }

    public String getFecha2() { return fecha2; }
    public void setFecha2(String fecha2) { this.fecha2 = fecha2; }

    public double getImporte2() { return importe2; }
    public void setImporte2(double importe2) { this.importe2 = importe2; }

    public String getConcepto3() { return concepto3; }
    public void setConcepto3(String concepto3) { this.concepto3 = concepto3; }

    public String getFecha3() { return fecha3; }
    public void setFecha3(String fecha3) { this.fecha3 = fecha3; }

    public double getImporte3() { return importe3; }
    public void setImporte3(double importe3) { this.importe3 = importe3; }

    // Getters y Setters de Gastos Tarjeta
    public double getCombustibleTarjeta() { return combustibleTarjeta; }
    public void setCombustibleTarjeta(double combustibleTarjeta) { this.combustibleTarjeta = combustibleTarjeta; }

    public double getCasetasTarjeta() { return casetasTarjeta; }
    public void setCasetasTarjeta(double casetasTarjeta) { this.casetasTarjeta = casetasTarjeta; }

    public double getSubtotalTarjeta() { return subtotalTarjeta; }
    public void setSubtotalTarjeta(double subtotalTarjeta) { this.subtotalTarjeta = subtotalTarjeta; }

    // Getters y Setters de Gastos Efectivo
    public double getCombustibleEfectivo() { return combustibleEfectivo; }
    public void setCombustibleEfectivo(double combustibleEfectivo) { this.combustibleEfectivo = combustibleEfectivo; }

    public double getCasetasEfectivo() { return casetasEfectivo; }
    public void setCasetasEfectivo(double casetasEfectivo) { this.casetasEfectivo = casetasEfectivo; }

    public double getComida() { return comida; }
    public void setComida(double comida) { this.comida = comida; }

    public double getReparaciones() { return reparaciones; }
    public void setReparaciones(double reparaciones) { this.reparaciones = reparaciones; }

    public double getManiobras() { return maniobras; }
    public void setManiobras(double maniobras) { this.maniobras = maniobras; }

    public double getTransitosFederal() { return transitosFederal; }
    public void setTransitosFederal(double transitosFederal) { this.transitosFederal = transitosFederal; }

    public double getOtros() { return otros; }
    public void setOtros(double otros) { this.otros = otros; }

    public double getSubtotalEfectivo() { return subtotalEfectivo; }
    public void setSubtotalEfectivo(double subtotalEfectivo) { this.subtotalEfectivo = subtotalEfectivo; }

    public double getGranTotal() { return granTotal; }
    public void setGranTotal(double granTotal) { this.granTotal = granTotal; }

    public int getConfirmacion() { return confirmacion; }
    public void setConfirmacion(int confirmacion) { this.confirmacion = confirmacion; }
    //   campo teléfono
    private String telefono;

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelefonoAdmin() { return telefonoAdmin; }
    public void setTelefonoAdmin(String telefonoAdmin) { this.telefonoAdmin = telefonoAdmin; }


    public String getLiquidacion() { return liquidacion; }
    public void setLiquidacion(String liquidacion) { this.liquidacion = liquidacion; }


}