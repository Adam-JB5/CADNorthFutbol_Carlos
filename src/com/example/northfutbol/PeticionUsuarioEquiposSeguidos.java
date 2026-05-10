package com.example.northfutbol;

import java.io.Serializable;

public class PeticionUsuarioEquiposSeguidos implements Serializable {

    public enum TipoOperacion {
        CHECK,   // ¿sigue el usuario este equipo?
        CREATE,  // seguir equipo
        DELETE   // dejar de seguir
    }

    private TipoOperacion tipoOperacion;
    private Integer idUsuario;
    private Integer idEquipo;

    // Getters y setters
    public TipoOperacion getTipoOperacion() { return tipoOperacion; }
    
    public void setTipoOperacion(TipoOperacion tipoOperacion) { this.tipoOperacion = tipoOperacion; }
    
    public Integer getIdUsuario() { return idUsuario; }
    
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    
    public Integer getIdEquipo() { return idEquipo; }
    
    public void setIdEquipo(Integer idEquipo) { this.idEquipo = idEquipo; }
}