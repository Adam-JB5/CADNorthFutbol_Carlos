/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import java.util.List;
import pojosnorthfutbol.Comentario;

/**
 * RESPUESTA
 * ==========================
 * Es el paquete que nos devuelve el servidor. Contiene el resultado 
 * de la operacion (exito/fracaso) y los datos solicitados
 * @author DAM209
 */
public class RespuestaComentario implements Serializable{
    // 1. IDENTIFICACIÓN: creamos un identificador único de versión de
    // serialización
    private static final long serialVersionUID = 1L;
    
    // 2. ¿SALIÖ BIEN LA OPERACIÓN?
    private boolean exito;
    
    // 3. ¿HAY INFORMACIÓN PARA EL CLIENTE? ("Comentario no encontrado", "Guardado", "Actualizado con éxito")
    // Delete/Update/Create
    private String mensaje;
    
    // 4. ¿QUË INFORMACIÓN DE EMPLEADO (Objeto) TIENES? Create/Read/Upadte
    private Comentario comentario;
    
    // 5. ¿Y SI HEMOS PEDIDO MUCHOS EMPLEADOS? (Read_all)
   private List<Comentario> comentarios;
   
   // 6. CONSTRUCTORES
   // 6.1. Constructor vacío
   public RespuestaComentario() {
       
   }
   
   // 6.2. Constructor completo
   public RespuestaComentario(boolean exito, String mensaje, Comentario comentario, List<Comentario> comentarios) {
       super();
       this.exito = exito;
       this.mensaje = mensaje;
       this.comentario = comentario;
       this.comentarios = comentarios;
   }
   
   // 6.3. GETTERS Y SETTERS

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }
   
   
   
   
    
    
}
