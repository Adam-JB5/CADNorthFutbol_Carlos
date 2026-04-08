/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import pojosnorthfutbol.Jugador;

/**
 * PETICIÓN
 * ====================
 * Es el sobre que envía el cliente. Representa la información que viaja desde
 * la app Android hacia el servidor
 * 
 * IMPORTANTE_ es serializable para poder viajar por la red convertida en bytes.
 * @author DAM209
 */
public class PeticionJugador implements Serializable{
    // 1. IDENTIFICACIÓN: creamos un identificador único de versión
    // de serializacioón   
    private static final long serialVersionUID = 1L;
    
    // 2. CREAMOS EN ENUM DE TIPOOPERACION
    public enum TipoOperacion {
        CREATE,     // Crea un jugador
        READ,       // Lee un jugador
        READ_ALL,   // Lee todos los jugador
        UPDATE,     // Modifica un jugador
        DELETE,     //
        PING        //
    }
    
    // 3. ¿QUÉ QUIERES HACER?
    private TipoOperacion tipoOperacion;
    
    // 4. ¿CON QUÉ DATOS?
    private Jugador jugador;
    
    // 5. ¿CON QUÉ ID? (Read/Delete)
    private int idJugador;
    
    // 6. CONSTRUCTORES
    // 6.1. Vacío
    public PeticionJugador() {
        // Nada
    }
    
    // 6.2. Contructor para Read_all
    public PeticionJugador(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    
    // 6.3. Contructor para Read/Delete
    public PeticionJugador(TipoOperacion tipoOperacion, int idJugador) {
        this.tipoOperacion = tipoOperacion;
        this.idJugador = idJugador;
    }
    
    // 6.4. Contructor para Create/Update
    public PeticionJugador(TipoOperacion tipoOperacion, int idJugador, Jugador jugador) {
        this.tipoOperacion = tipoOperacion;
        this.idJugador = idJugador;
        this.jugador = jugador;
    }
    
    // Constructor para Login/Register
    public PeticionJugador(TipoOperacion tipoOperacion, Jugador jugador) {
        this.tipoOperacion = tipoOperacion;
        this.jugador = jugador;
    }
    
    // 7. Getters y Setters

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }
    
    
    
}
