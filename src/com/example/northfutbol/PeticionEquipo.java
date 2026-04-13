/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import pojosnorthfutbol.Equipo;

/**
 * PETICIÓN ==================== Es el sobre que envía el cliente. Representa la
 * información que viaja desde la app Android hacia el servidor
 *
 * IMPORTANTE_ es serializable para poder viajar por la red convertida en bytes.
 *
 * @author DAM209
 */
public class PeticionEquipo implements Serializable {

    // 1. IDENTIFICACIÓN: creamos un identificador único de versión
    // de serializacioón   
    private static final long serialVersionUID = 1L;

    // 2. CREAMOS EN ENUM DE TIPOOPERACION
    public enum TipoOperacion {
        CREATE, // Crea un equipo
        READ, // Lee un equipo
        READ_ALL, // Lee todos los equipo
        UPDATE, // Modifica un equipo
        READ_BY_GROUP,
        DELETE, //
        PING        //
    }

    // 3. ¿QUÉ QUIERES HACER?
    private TipoOperacion tipoOperacion;

    // 4. ¿CON QUÉ DATOS?
    private Equipo equipo;

    // 5. ¿CON QUÉ ID? (Read/Delete)
    private int idEquipo;

    // Numero de grupo
    private char grupo;

    // 6. CONSTRUCTORES
    // 6.1. Vacío
    public PeticionEquipo() {
        // Nada
    }

    // 6.2. Contructor para Read_all
    public PeticionEquipo(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    // 6.3. Contructor para Read/Delete
    public PeticionEquipo(TipoOperacion tipoOperacion, int idEquipo) {
        this.tipoOperacion = tipoOperacion;
        this.idEquipo = idEquipo;
    }

    // 6.4. Contructor para Create/Update
    public PeticionEquipo(TipoOperacion tipoOperacion, int idEquipo, Equipo equipo) {
        this.tipoOperacion = tipoOperacion;
        this.idEquipo = idEquipo;
        this.equipo = equipo;
    }

    // Constructor para Login/Register
    public PeticionEquipo(TipoOperacion tipoOperacion, Equipo equipo) {
        this.tipoOperacion = tipoOperacion;
        this.equipo = equipo;
    }

    // Constructor — ahora no hay ambigüedad con (TipoOperacion, int)
    public PeticionEquipo(TipoOperacion tipoOperacion, char grupo) {
        this.tipoOperacion = tipoOperacion;
        this.grupo = grupo;
    }

    // 7. Getters y Setters
    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public char getGrupo() {
        return grupo;
    }

    public void setGrupo(char grupo) {
        this.grupo = grupo;
    }
    
    

}
