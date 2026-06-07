package com.example.satori_by_aristo;

import java.io.Serializable;

public class ViajeDto implements Serializable {

    private Integer folio;
    private String operador;
    private Integer enviado;
    private Integer iniciado;
    private String fecha;
    private String password;
    private String destino;
    private String cliente;

     public ViajeDto() {
    }

    // Constructor con todos los campos (Builder manual)
    public ViajeDto(Integer folio, String operador, Integer enviado, Integer iniciado,
                    String fecha, String password, String destino) {
        this.folio = folio;
        this.operador = operador;
        this.enviado = enviado;
        this.iniciado = iniciado;
        this.fecha = fecha;
        this.password = password;
        this.destino = destino;
        this.cliente = cliente;
    }

    // Getters y Setters
    public Integer getFolio() { return folio; }
    public void setFolio(Integer folio) { this.folio = folio; }

    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }

    public Integer getEnviado() { return enviado; }
    public void setEnviado(Integer enviado) { this.enviado = enviado; }

    public Integer getIniciado() { return iniciado; }
    public void setIniciado(Integer iniciado) { this.iniciado = iniciado; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

}
