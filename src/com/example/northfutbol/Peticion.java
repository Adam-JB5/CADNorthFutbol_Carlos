/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import pojosnorthfutbol.Usuario;

/**
 * PETICIÓN
 * ====================
 * Es el sobre que envía el cliente. Representa la información que viaja desde
 * la app Android hacia el servidor
 * 
 * IMPORTANTE_ es serializable para poder viajar por la red convertida en bytes.
 * @author DAM209
 */
public class Peticion implements Serializable{
    // 1. IDENTIFICACIÓN: creamos un identificador único de versión
    // de serializacioón   
    private static final long serialVersionUID = 1L;
    
    // 2. CREAMOS EN ENUM DE TIPOOPERACION
    public enum TipoOperacion {
        CREATE,     // Crea un usuario
        READ,       // Lee un usuario
        READ_ALL,   // Lee todos los usuario
        UPDATE,     // Modifica un usuario
        LOGIN,
        DELETE,     //
        PING        //
    }
    
    // 3. ¿QUÉ QUIERES HACER?
    private TipoOperacion tipoOperacion;
    
    // 4. ¿CON QUÉ DATOS?
    private Usuario usuario;
    
    // 5. ¿CON QUÉ ID? (Read/Delete)
    private int idUsuario;
    
    // 6. CONSTRUCTORES
    // 6.1. Vacío
    public Peticion() {
        // Nada
    }
    
    // 6.2. Contructor para Read_all
    public Peticion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    
    // 6.3. Contructor para Read/Delete
    public Peticion(TipoOperacion tipoOperacion, int idUsuario) {
        this.tipoOperacion = tipoOperacion;
        this.idUsuario = idUsuario;
    }
    
    // 6.4. Contructor para Create/Update
    public Peticion(TipoOperacion tipoOperacion, int idUsuario, Usuario usuario) {
        this.tipoOperacion = tipoOperacion;
        this.idUsuario = idUsuario;
        this.usuario = usuario;
    }
    
    // Constructor para Login
    public Peticion(TipoOperacion tipoOperacion, Usuario usuario) {
        this.tipoOperacion = tipoOperacion;
        this.usuario = usuario;
    }
    
    // 7. Getters y Setters

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    
    
}
