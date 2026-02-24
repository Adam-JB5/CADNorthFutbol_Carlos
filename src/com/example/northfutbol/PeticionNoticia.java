/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import pojosnorthfutbol.Noticia;

/**
 * PETICIÓN
 * ====================
 * Es el sobre que envía el cliente. Representa la información que viaja desde
 * la app Android hacia el servidor
 * 
 * IMPORTANTE_ es serializable para poder viajar por la red convertida en bytes.
 * @author DAM209
 */
public class PeticionNoticia implements Serializable{
    // 1. IDENTIFICACIÓN: creamos un identificador único de versión
    // de serializacioón   
    private static final long serialVersionUID = 1L;
    
    // 2. CREAMOS EN ENUM DE TIPOOPERACION
    public enum TipoOperacion {
        CREATE,     // Crea un noticia
        READ,       // Lee un noticia
        READ_ALL,   // Lee todos los noticia
        UPDATE,     // Modifica un noticia
        LOGIN,
        REGISTER,
        UPDATE_USER_NAME_EMAIL,
        DELETE,     //
        PING        //
    }
    
    // 3. ¿QUÉ QUIERES HACER?
    private TipoOperacion tipoOperacion;
    
    // 4. ¿CON QUÉ DATOS?
    private Noticia noticia;
    
    // 5. ¿CON QUÉ ID? (Read/Delete)
    private int idNoticia;
    
    // 6. CONSTRUCTORES
    // 6.1. Vacío
    public PeticionNoticia() {
        // Nada
    }
    
    // 6.2. Contructor para Read_all
    public PeticionNoticia(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    
    // 6.3. Contructor para Read/Delete
    public PeticionNoticia(TipoOperacion tipoOperacion, int idNoticia) {
        this.tipoOperacion = tipoOperacion;
        this.idNoticia = idNoticia;
    }
    
    // 6.4. Contructor para Create/Update
    public PeticionNoticia(TipoOperacion tipoOperacion, int idNoticia, Noticia noticia) {
        this.tipoOperacion = tipoOperacion;
        this.idNoticia = idNoticia;
        this.noticia = noticia;
    }
    
    // Constructor para Login/Register
    public PeticionNoticia(TipoOperacion tipoOperacion, Noticia noticia) {
        this.tipoOperacion = tipoOperacion;
        this.noticia = noticia;
    }
    
    // 7. Getters y Setters

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }

    public int getIdNoticia() {
        return idNoticia;
    }

    public void setIdNoticia(int idNoticia) {
        this.idNoticia = idNoticia;
    }
    
    
    
}
