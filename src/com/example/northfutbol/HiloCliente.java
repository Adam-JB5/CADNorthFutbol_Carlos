/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import pojosnorthfutbol.Usuario;

/**
 * HILO CLIENTE ===================== Esta clase representa al recepcionista que
 * atiende a un cliente específico (Android) Extiende "Thread" para poder
 * ejecutarse en paralelo al servidor
 *
 *
 * @author DAM209
 */
class HiloCliente extends Thread {

    // 1. ATRIBUTOS: Declaramos el socket, es decir, el "teléfono" por el que hablamos con este cleinte
    // en particular
    private Socket socket;

    // 2. CONSTRUCTOR
    public HiloCliente(Socket socket) {
        super();
        this.socket = socket;
    }

    // 3. OVERRIDE (Run)
    // Aquí escribimos el script que se ejecuta en paralelo (o concurrente)
    public void run() {
        // 3.1 Declaramos los tuneles
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        // 3.2. Asignamos un canal a cada tunel
        try {
            //"InputStream": oído del servidor (escucha al cliente)
            //"OutputStream": boca del servidor (por la que responde al cliente)
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

            // 3.3 Recibimos una petición (READ)
            // El cliente nos envia un objeto Peticion serializado
            // Nos quedamos bloqueados esperando a que llegue entera
            Peticion peticion = (Peticion) ois.readObject();

            // 3.4. Procesamos la peticon
            // Instanciamos el CAD para hablar con la base de datos
            CADNorthFutbol cad = new CADNorthFutbol();

            // 3.5. preparamos la respuesta
            Respuesta respuesta = new Respuesta();

            // 3.6 Miramos que quiere hacer el cliente (CREATE, READ, UPDATE, DELETE, READ_ALL)
            switch (peticion.getTipoOperacion()) {
                case CREATE:
                    break;
                case READ:
                    // 3.6.1 Leer: busca por ID
                    Usuario usuario = cad.leerUsuario(peticion.getIdUsuario());
                    if (usuario != null) {
                        // 3.6.1.2 Construimos la respuesta
                        respuesta.setUsuario(usuario); // Metemos el empleado en el sobre
                        respuesta.setExito(true); // Hay exito 
                        respuesta.setMensaje("Empleado encontrado.");
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("No existe un empleado con ID: " + peticion.getIdUsuario());
                    }
                    break;
                case UPDATE:
                    break;
                case DELETE:
                    break;
                case READ_ALL:
                    // 3.6.2 Buscar todos los empleados (maximo 50 --> Se modfica en EmpleadosCAD)
                    List<Usuario> usuarios = cad.leerUsuarios();

                    if (!usuarios.isEmpty()) {
                        respuesta.setUsuarios(usuarios);
                        respuesta.setExito(true);
                        respuesta.setMensaje("Listado recuperado con: " + usuarios.size() + " empleados.");
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("La base de datos parece vacía");
                    }
                    break;
                case PING:
                    // 3.6.2. Ping: comprueba si hay conexion (cliente-servidor)
                    respuesta.setExito(true);
                    respuesta.setMensaje("¡PONG! Servidor activo y escuchando");
                    break;
                case LOGIN:
                    // 1. Obtenemos el "usuario temporal" que mandó Android con las credenciales
                    Usuario userLogin = peticion.getUsuario();

                    if (userLogin != null) {
                        // 2. Llamamos al CAD para validar (debes tener este método en tu CAD)
                        // El método debería devolver el Usuario completo si existe, o null si no.
                        Usuario usuarioValidado = cad.validarLogin(userLogin.getEmail());

                        if (usuarioValidado != null) {
                            respuesta.setUsuario(usuarioValidado); // Enviamos el perfil completo (nombre, rol, etc.)
                            respuesta.setExito(true);
                            respuesta.setMensaje("Login correcto. ¡Bienvenido!");
                        } else {
                            respuesta.setExito(false);
                            respuesta.setMensaje("Email o contraseña incorrectos.");
                        }
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("No se han recibido datos de login.");
                    }
                    break;
                default:
                    // Para todo lo demás
                    respuesta.setExito(false);
                    respuesta.setMensaje("Operacion desconocida (por ahora)");
                    break;
            }

            // 4. ENVIAR LA RESPUESTA
            // Devolvemos el objeto respuesta lleno de datos
            oos.writeObject(respuesta);
            oos.flush();

        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
        } finally {
            // 5. CERRAMOS SESIÓN
            //Obligatorio: cerramos el socket para liberar recursos
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar canales: " + e.getMessage());
            }
        }
    }
}
