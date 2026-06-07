package com.example.satori_by_aristo.retrofit;

public class RechazoDto {
    private Integer id;          // autoincremental, no lo mandas
    private String telefonoOp;   // teléfono del operador
    private String motivo;       // motivo del rechazo

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTelefonoOp() { return telefonoOp; }
    public void setTelefonoOp(String telefonoOp) { this.telefonoOp = telefonoOp; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}