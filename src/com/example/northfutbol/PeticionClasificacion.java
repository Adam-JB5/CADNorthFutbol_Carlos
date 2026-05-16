package com.example.northfutbol;


import java.io.Serializable;

public class PeticionClasificacion implements Serializable {
    public enum TipoOperacion { READ_BY_GRUPO }

    private TipoOperacion tipoOperacion;
    private String grupo;

    public PeticionClasificacion(TipoOperacion tipoOperacion, String grupo) {
        this.tipoOperacion = tipoOperacion;
        this.grupo = grupo;
    }

    public TipoOperacion getTipoOperacion() { return tipoOperacion; }
    public String getGrupo() { return grupo; }
}