/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import pojosnorthfutbol.EventoPartido;

/**
 * PETICIÓN
 * ====================
 * Es el sobre que envía el cliente. Representa la información que viaja desde
 * la app Android hacia el servidor
 * 
 * IMPORTANTE_ es serializable para poder viajar por la red convertida en bytes.
 * @author DAM209
 */
public class PeticionEventoPartido implements Serializable{
    // 1. IDENTIFICACIÓN: creamos un identificador único de versión
    // de serializacioón   
    private static final long serialVersionUID = 1L;
    
    // 2. CREAMOS EN ENUM DE TIPOOPERACION
    public enum TipoOperacion {
        CREATE,     // Crea un eventoPartido
        READ,       // Lee un eventoPartido
        READ_ALL,   // Lee todos los eventoPartido
        READ_BY_TEAM,
        UPDATE,     // Modifica un eventoPartido
        DELETE,     //
        PING,       //
        READ_BY_PARTIDO,
    }
    
    // 3. ¿QUÉ QUIERES HACER?
    private TipoOperacion tipoOperacion;
    
    // 4. ¿CON QUÉ DATOS?
    private EventoPartido eventoPartido;
    
    // 5. ¿CON QUÉ ID? (Read/Delete)
    private int id;
    
    // 6. CONSTRUCTORES
    // 6.1. Vacío
    public PeticionEventoPartido() {
        // Nada
    }
    
    // 6.2. Contructor para Read_all
    public PeticionEventoPartido(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    
    // 6.3. Contructor para Read/Delete
    public PeticionEventoPartido(TipoOperacion tipoOperacion, int idEventoPartido) {
        this.tipoOperacion = tipoOperacion;
        this.id = idEventoPartido;
    }
    
    // 6.4. Contructor para Create/Update
    public PeticionEventoPartido(TipoOperacion tipoOperacion, int idEventoPartido, EventoPartido eventoPartido) {
        this.tipoOperacion = tipoOperacion;
        this.id = idEventoPartido;
        this.eventoPartido = eventoPartido;
    }
    
    // 7. Getters y Setters

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public EventoPartido getEventoPartido() {
        return eventoPartido;
    }

    public void setEventoPartido(EventoPartido eventoPartido) {
        this.eventoPartido = eventoPartido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
}
