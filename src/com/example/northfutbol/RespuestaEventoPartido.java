/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.Serializable;
import java.util.List;
import pojosnorthfutbol.EventoPartido;

/**
 * RESPUESTA
 * ==========================
 * Es el paquete que nos devuelve el servidor. Contiene el resultado 
 * de la operacion (exito/fracaso) y los datos solicitados
 * @author DAM209
 */
public class RespuestaEventoPartido implements Serializable{
    // 1. IDENTIFICACIÓN: creamos un identificador único de versión de
    // serialización
    private static final long serialVersionUID = 1L;
    
    // 2. ¿SALIÖ BIEN LA OPERACIÓN?
    private boolean exito;
    
    // 3. ¿HAY INFORMACIÓN PARA EL CLIENTE? ("EventoPartido no encontrado", "Guardado", "Actualizado con éxito")
    // Delete/Update/Create
    private String mensaje;
    
    // 4. ¿QUË INFORMACIÓN DE EMPLEADO (Objeto) TIENES? Create/Read/Upadte
    private EventoPartido eventoPartido;
    
    // 5. ¿Y SI HEMOS PEDIDO MUCHOS EMPLEADOS? (Read_all)
   private List<EventoPartido> eventoPartidos;
   
   // 6. CONSTRUCTORES
   // 6.1. Constructor vacío
   public RespuestaEventoPartido() {
       
   }
   
   // 6.2. Constructor completo
   public RespuestaEventoPartido(boolean exito, String mensaje, EventoPartido eventoPartido, List<EventoPartido> eventoPartidos) {
       super();
       this.exito = exito;
       this.mensaje = mensaje;
       this.eventoPartido = eventoPartido;
       this.eventoPartidos = eventoPartidos;
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

    public EventoPartido getEventoPartido() {
        return eventoPartido;
    }

    public void setEventoPartido(EventoPartido eventoPartido) {
        this.eventoPartido = eventoPartido;
    }

    public List<EventoPartido> getEventoPartidos() {
        return eventoPartidos;
    }

    public void setEventoPartidos(List<EventoPartido> eventoPartidos) {
        this.eventoPartidos = eventoPartidos;
    }
   
   
   
   
    
    
}
