package com.example.northfutbol;

import java.io.Serializable;

public class RespuestaUsuarioEquiposSeguidos implements Serializable {

    private boolean exito;
    private boolean siguiendo;  // para el CHECK
    private String mensaje;

    // Getters y setters
    public boolean isExito() { return exito; }
    
    public void setExito(boolean exito) { this.exito = exito; }
    
    public boolean isSiguiendo() { return siguiendo; }
    
    public void setSiguiendo(boolean siguiendo) { this.siguiendo = siguiendo; }
    
    public String getMensaje() { return mensaje; }
    
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}