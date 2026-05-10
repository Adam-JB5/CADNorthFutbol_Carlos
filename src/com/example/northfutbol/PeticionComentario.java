/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import pojosnorthfutbol.Comentario;

/**
 * PETICIÓN
 * ====================
 * Es el sobre que envía el cliente. Representa la información que viaja desde
 * la app Android hacia el servidor
 * 
 * IMPORTANTE_ es serializable para poder viajar por la red convertida en bytes.
 * @author DAM209
 */
public class PeticionComentario implements Serializable{
    // 1. IDENTIFICACIÓN: creamos un identificador único de versión
    // de serializacioón   
    private static final long serialVersionUID = 1L;
    
    // 2. CREAMOS EN ENUM DE TIPOOPERACION
    public enum TipoOperacion {
        CREATE,     // Crea un comentario
        READ,       // Lee un comentario
        READ_ALL,   // Lee todos los comentario
        READ_BY_NOTICIA,
        UPDATE,     // Modifica un comentario
        DELETE,     //
        PING,       //
    }
    
    // 3. ¿QUÉ QUIERES HACER?
    private TipoOperacion tipoOperacion;
    
    // 4. ¿CON QUÉ DATOS?
    private Comentario comentario;
    
    // 5. ¿CON QUÉ ID? (Read/Delete)
    private int idComentario;
    
    private int idNoticia;
    
    // 6. CONSTRUCTORES
    // 6.1. Vacío
    public PeticionComentario() {
        // Nada
    }
    
    // 6.2. Contructor para Read_all
    public PeticionComentario(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    
    // 6.3. Contructor para Read/Delete
    public PeticionComentario(TipoOperacion tipoOperacion, int idComentario) {
        this.tipoOperacion = tipoOperacion;
        this.idComentario = idComentario;
    }
    
    // 6.4. Contructor para Create/Update
    public PeticionComentario(TipoOperacion tipoOperacion, int idComentario, Comentario comentario) {
        this.tipoOperacion = tipoOperacion;
        this.idComentario = idComentario;
        this.comentario = comentario;
    }
    
    // 7. Getters y Setters

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public int getIdNoticia() {
        return idNoticia;
    }

    public void setIdNoticia(int idNoticia) {
        this.idNoticia = idNoticia;
    }
    
    
    
}
