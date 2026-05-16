/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import pojosnorthfutbol.Partido;

/**
 * PETICIÓN
 * ====================
 * Es el sobre que envía el cliente. Representa la información que viaja desde
 * la app Android hacia el servidor
 * 
 * IMPORTANTE_ es serializable para poder viajar por la red convertida en bytes.
 * @author DAM209
 */
public class PeticionPartido implements Serializable{
    // 1. IDENTIFICACIÓN: creamos un identificador único de versión
    // de serializacioón   
    private static final long serialVersionUID = 1L;
    
    // 2. CREAMOS EN ENUM DE TIPOOPERACION
    public enum TipoOperacion {
        CREATE,     // Crea un partido
        READ,       // Lee un partido
        READ_ALL,   // Lee todos los partido
        UPDATE,     // Modifica un partido
        READ_BY_FOLLOWED,
        READ_BY_TEAM
    }
    
    // 3. ¿QUÉ QUIERES HACER?
    private TipoOperacion tipoOperacion;
    
    // 4. ¿CON QUÉ DATOS?
    private Partido partido;
    
    // 5. ¿CON QUÉ ID? (Read/Delete) (Partido/Usuario)
    private int id;
    
    // 6. CONSTRUCTORES
    // 6.1. Vacío
    public PeticionPartido() {
        // Nada
    }
    
    // 6.2. Contructor para Read_all
    public PeticionPartido(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    
    // 6.3. Contructor para Read/Delete
    public PeticionPartido(TipoOperacion tipoOperacion, int idPartidoUsuario) {
        this.tipoOperacion = tipoOperacion;
        this.id = idPartidoUsuario;
    }
    
    // 6.4. Contructor para Create/Update
    public PeticionPartido(TipoOperacion tipoOperacion, int idPartido, Partido partido) {
        this.tipoOperacion = tipoOperacion;
        this.id = idPartido;
        this.partido = partido;
    }
    
    // Constructor para Login/Register
    public PeticionPartido(TipoOperacion tipoOperacion, Partido partido) {
        this.tipoOperacion = tipoOperacion;
        this.partido = partido;
    }

    // 7. Getters y Setters

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public int getId() {
        return id;
    }

    public void setId(int idPartido) {
        this.id = idPartido;
    }
    
    
    
}
