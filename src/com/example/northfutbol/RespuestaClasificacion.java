package com.example.northfutbol;

import java.io.Serializable;
import java.util.ArrayList;

public class RespuestaClasificacion implements Serializable {
    private boolean exito;
    private ArrayList<EquipoClasificacion> equipos;
    private String mensaje;

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }
    public ArrayList<EquipoClasificacion> getEquipos() { return equipos; }
    public void setEquipos(ArrayList<EquipoClasificacion> equipos) { this.equipos = equipos; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}