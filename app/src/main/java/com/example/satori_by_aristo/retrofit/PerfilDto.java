package com.example.satori_by_aristo.retrofit;

public class PerfilDto {
    private String nombre;
    private String fechaNac; // puedes usar String si no manejas java.sql.Date
    private String curp;
    private String nss;
    private String correo;
    private String telefono;
    private String usuario;
    private String contrasena;
    private Integer inicio;
    private Integer rol;
    private String clave;

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFechaNac() { return fechaNac; }
    public void setFechaNac(String fechaNac) { this.fechaNac = fechaNac; }

    public String getCurp() { return curp; }
    public void setCurp(String curp) { this.curp = curp; }

    public String getNss() { return nss; }
    public void setNss(String nss) { this.nss = nss; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Integer getInicio() { return inicio; }
    public void setInicio(Integer inicio) { this.inicio = inicio; }

    public Integer getRol() { return rol; }
    public void setRol(Integer rol) { this.rol = rol; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
}