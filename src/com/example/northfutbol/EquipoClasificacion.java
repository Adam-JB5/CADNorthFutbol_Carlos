package com.example.northfutbol;

import java.io.Serializable;

public class EquipoClasificacion implements Serializable {
    private int idEquipo;
    private String nombre;
    private int pj, pg, pe, pp, gf, gc, gd, puntos;

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getPj() { return pj; }
    public void setPj(int pj) { this.pj = pj; }
    public int getPg() { return pg; }
    public void setPg(int pg) { this.pg = pg; }
    public int getPe() { return pe; }
    public void setPe(int pe) { this.pe = pe; }
    public int getPp() { return pp; }
    public void setPp(int pp) { this.pp = pp; }
    public int getGf() { return gf; }
    public void setGf(int gf) { this.gf = gf; }
    public int getGc() { return gc; }
    public void setGc(int gc) { this.gc = gc; }
    public int getGd() { return gd; }
    public void setGd(int gd) { this.gd = gd; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
}