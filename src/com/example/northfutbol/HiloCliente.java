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
import java.util.ArrayList;
import java.util.List;
import pojosnorthfutbol.Equipo;
import pojosnorthfutbol.ExcepcionNF;
import pojosnorthfutbol.Jugador;
import pojosnorthfutbol.Noticia;
import pojosnorthfutbol.Usuario;
import pojosnorthfutbol.Partido;

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
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

            Object objeto = ois.readObject();

            System.out.println("DEBUG: Objeto recibido tipo: " + objeto.getClass().getName());

            CADNorthFutbol cad = new CADNorthFutbol();

            if (objeto instanceof PeticionUsuario) {
                System.out.println("DEBUG: Es PeticionUsuario");
                manejarUsuarios((PeticionUsuario) objeto, oos, cad);
            } else if (objeto instanceof PeticionNoticia) {
                System.out.println("DEBUG: Es PeticionNoticia");
                manejarNoticias((PeticionNoticia) objeto, oos, cad);
            } else if (objeto instanceof PeticionEquipo) {
                System.out.println("DEBUG: Es PeticionEquipo");
                manejarEquipos((PeticionEquipo) objeto, oos, cad);
            } else if (objeto instanceof PeticionJugador) {
                System.out.println("DEBUG: Es PeticionJugador");
                manejarJugadores((PeticionJugador) objeto, oos, cad);
            } else if (objeto instanceof PeticionPartido) {
                System.out.println("DEBUG: Es PeticionPartido");
                manejarPartidos((PeticionPartido) objeto, oos, cad);
            } else {
                System.out.println("DEBUG: El objeto NO es de ningun tipo conocido");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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

    private void manejarNoticias(PeticionNoticia peticion, ObjectOutputStream oos, CADNorthFutbol cad) throws Exception {
        System.out.println("DEBUG: Entrando en manejarNoticias... Operación: " + peticion.getTipoOperacion());
        RespuestaNoticia respuesta = new RespuestaNoticia();

        try {
            switch (peticion.getTipoOperacion()) {
                case CREATE:
                    Noticia nuevaNoticia = peticion.getNoticia();
                    if (nuevaNoticia != null) {
                        Integer filasAfectadas = cad.insertarNoticia(nuevaNoticia);

                        if (filasAfectadas != null && filasAfectadas > 0) {
                            respuesta.setExito(true);
                            respuesta.setMensaje("Noticia insertada correctamente en Oracle.");
                            System.out.println("DEBUG: Noticia insertada, filas modificadas: " + filasAfectadas);
                        } else {
                            respuesta.setExito(false);
                            respuesta.setMensaje("Error: No se pudo insertar la noticia.");
                        }
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("No se recibió el objeto Noticia.");
                    }
                    break;

                case READ_ALL: // El caso que tenías antes
                    ArrayList<Noticia> lista = cad.leerNoticias();
                    respuesta.setNoticias(lista);
                    respuesta.setExito(lista != null);
                    respuesta.setMensaje("Lista de noticias recuperada.");
                    break;

                default:
                    respuesta.setExito(false);
                    respuesta.setMensaje("Operación de noticia no soportada.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error en manejarNoticias: " + e.getMessage());
            e.printStackTrace();
            respuesta.setExito(false);
            respuesta.setMensaje("Error en el servidor: " + e.getMessage());
        }

        oos.writeObject(respuesta);
        oos.flush();
        System.out.println("DEBUG: Respuesta de noticia enviada a Android");
    }

    private void manejarEquipos(PeticionEquipo peticion, ObjectOutputStream oos, CADNorthFutbol cad) throws Exception {
        System.out.println("DEBUG: Entrando en manejarEquipos... Operación: " + peticion.getTipoOperacion());
        RespuestaEquipo respuesta = new RespuestaEquipo();

        try {
            switch (peticion.getTipoOperacion()) {
                case CREATE:
                    Equipo nuevoEquipo = peticion.getEquipo();
                    if (nuevoEquipo != null) {
                        Integer filasAfectadas = cad.insertarEquipo(nuevoEquipo);

                        if (filasAfectadas != null && filasAfectadas > 0) {
                            respuesta.setExito(true);
                            respuesta.setMensaje("Equipo insertado correctamente en Oracle.");
                            System.out.println("DEBUG: Equipo insertado, filas modificadas: " + filasAfectadas);
                        } else {
                            respuesta.setExito(false);
                            respuesta.setMensaje("Error: No se pudo insertar el equipo.");
                        }
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("No se recibió el objeto Equipo.");
                    }
                    break;

                case READ_ALL:
                    ArrayList<Equipo> lista = cad.leerEquipos();
                    respuesta.setEquipos(lista);
                    respuesta.setExito(lista != null);
                    respuesta.setMensaje("Lista de equipos recuperada.");
                    break;

                case READ:
                    List<Jugador> jugadores = cad.leerJugadoresPorEquipo(peticion.getIdEquipo());
                    respuesta.setJugadores(jugadores);
                    respuesta.setExito(jugadores != null);
                    respuesta.setMensaje("Jugadores del equipo recuperados.");
                    break;
                    
                case READ_BY_GROUP:
                    ArrayList<Equipo> listaPorGrupo = cad.leerEquiposPorGrupo(peticion.getGrupo());
                    respuesta.setEquipos(listaPorGrupo);
                    respuesta.setExito(listaPorGrupo != null);
                    respuesta.setMensaje("Lista de equipos por grupo recuperada.");
                    break;

                default:
                    respuesta.setExito(false);
                    respuesta.setMensaje("Operación de equipo no soportada.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error en manejarEquipos: " + e.getMessage());
            e.printStackTrace();
            respuesta.setExito(false);
            respuesta.setMensaje("Error en el servidor: " + e.getMessage());
        }

        oos.writeObject(respuesta);
        oos.flush();
        System.out.println("DEBUG: Respuesta de equipo enviada a Android");
    }

    private void manejarUsuarios(PeticionUsuario peticion, ObjectOutputStream oos, CADNorthFutbol cad) throws Exception {
        RespuestaUsuario respuesta = new RespuestaUsuario();
        // ... aquí pegas tu switch actual de LOGIN, REGISTER, etc ...
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
                    respuesta.setMensaje("Usuario encontrado.");
                } else {
                    respuesta.setExito(false);
                    respuesta.setMensaje("No existe un usuario con ID: " + peticion.getIdUsuario());
                }
                break;
            case UPDATE:
                break;
            case DELETE:
                Usuario usuarioEliminado = peticion.getUsuario();
                if (usuarioEliminado != null) {
                    try {
                        Integer registros = cad.eliminarUsuario(usuarioEliminado.getIdUsuario());

                        if (registros != null && registros > 0) {
                            respuesta.setExito(true);
                            respuesta.setUsuario(usuarioEliminado);
                            respuesta.setMensaje("Usuario eliminado correctamente.");
                        } else {
                            respuesta.setExito(false);
                            respuesta.setMensaje("No se pudo eliminar el usuario.");
                        }
                    } catch (ExcepcionNF e) {
                        respuesta.setExito(false);
                        respuesta.setMensaje(e.getMensajeErrorUsuario());
                    }
                } else {
                    respuesta.setExito(false);
                    respuesta.setMensaje("No se han recibido datos de registro.");
                }
                break;
            case READ_ALL:
                List<Usuario> usuarios = cad.leerUsuarios();

                if (!usuarios.isEmpty()) {
                    respuesta.setUsuarios(usuarios);
                    respuesta.setExito(true);
                    respuesta.setMensaje("Listado recuperado con: " + usuarios.size() + " usuarios.");
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
                Usuario userLogin = peticion.getUsuario();
                if (userLogin != null) {
                    try {
                        // Validamos usuario
                        Usuario usuarioValidado = cad.validarLogin(userLogin.getEmail(), userLogin.getContrasenna());
                        if (usuarioValidado != null) {
                            respuesta.setUsuario(usuarioValidado);
                            respuesta.setExito(true);
                            respuesta.setMensaje("Login correcto. ¡Bienvenido!");
                        } else {
                            respuesta.setExito(false);
                            respuesta.setMensaje("Email o contraseña incorrectos.");
                        }
                    } catch (ExcepcionNF e) {
                        respuesta.setExito(false);
                        respuesta.setMensaje(e.getMensajeErrorUsuario());
                    }
                } else {
                    respuesta.setExito(false);
                    respuesta.setMensaje("No se han recibido datos de login.");
                }
                break;
            case REGISTER:
                Usuario nuevoUsuario = peticion.getUsuario();
                if (nuevoUsuario != null) {
                    try {
                        Integer registros = cad.registrarUsuario(nuevoUsuario);

                        if (registros != null && registros > 0) {
                            respuesta.setExito(true);
                            respuesta.setUsuario(nuevoUsuario);
                            respuesta.setMensaje("Usuario registrado correctamente.");
                        } else {
                            respuesta.setExito(false);
                            respuesta.setMensaje("No se pudo registrar el usuario.");
                        }
                    } catch (ExcepcionNF e) {
                        respuesta.setExito(false);
                        respuesta.setMensaje(e.getMensajeErrorUsuario());
                    }
                } else {
                    respuesta.setExito(false);
                    respuesta.setMensaje("No se han recibido datos de registro.");
                }
                break;
            case UPDATE_USER_NAME_EMAIL:
                Usuario usuarioModificado = peticion.getUsuario();
                if (usuarioModificado != null) {
                    try {
                        Integer registros = cad.modificarNombreEmailUsuario(usuarioModificado.getIdUsuario(), usuarioModificado);

                        if (registros != null && registros > 0) {
                            respuesta.setExito(true);
                            respuesta.setUsuario(usuarioModificado);
                            respuesta.setMensaje("Usuario registrado correctamente.");
                        } else {
                            respuesta.setExito(false);
                            respuesta.setMensaje("No se pudo registrar el usuario.");
                        }
                    } catch (ExcepcionNF e) {
                        respuesta.setExito(false);
                        respuesta.setMensaje(e.getMensajeErrorUsuario());
                    }
                } else {
                    respuesta.setExito(false);
                    respuesta.setMensaje("No se han recibido datos de modificación");
                }
                break;
            default:
                // Para todo lo demás
                respuesta.setExito(false);
                respuesta.setMensaje("Operacion desconocida (por ahora)");
                break;
        }
        oos.writeObject(respuesta);
        oos.flush();
    }

    private void manejarJugadores(PeticionJugador peticion, ObjectOutputStream oos, CADNorthFutbol cad) throws Exception {
        System.out.println("DEBUG: Entrando en manejarJugadores... Operación: " + peticion.getTipoOperacion());
        RespuestaJugador respuesta = new RespuestaJugador();

        try {
            switch (peticion.getTipoOperacion()) {
                case CREATE:
                    Jugador nuevoJugador = peticion.getJugador();
                    if (nuevoJugador != null) {
                        Integer filasAfectadas = cad.insertarJugador(nuevoJugador);

                        if (filasAfectadas != null && filasAfectadas > 0) {
                            respuesta.setExito(true);
                            respuesta.setMensaje("Jugador insertado correctamente en Oracle.");
                            System.out.println("DEBUG: Jugador insertado, filas modificadas: " + filasAfectadas);
                        } else {
                            respuesta.setExito(false);
                            respuesta.setMensaje("Error: No se pudo insertar el jugador.");
                        }
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("No se recibió el objeto Jugador.");
                    }
                    break;

                case READ_ALL: // El caso que tenías antes
                    ArrayList<Jugador> lista = cad.leerJugadores();
                    respuesta.setJugadores(lista);
                    respuesta.setExito(lista != null);
                    respuesta.setMensaje("Lista de jugadores recuperada.");
                    break;

                default:
                    respuesta.setExito(false);
                    respuesta.setMensaje("Operación de jugador no soportada.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error en manejarJugadores: " + e.getMessage());
            e.printStackTrace();
            respuesta.setExito(false);
            respuesta.setMensaje("Error en el servidor: " + e.getMessage());
        }

        oos.writeObject(respuesta);
        oos.flush();
        System.out.println("DEBUG: Respuesta de jugador enviada a Android");
    }

    private void manejarPartidos(PeticionPartido peticion, ObjectOutputStream oos, CADNorthFutbol cad) throws Exception {
        System.out.println("DEBUG: Entrando en manejarPartidos... Operación: " + peticion.getTipoOperacion());
        RespuestaPartido respuesta = new RespuestaPartido();

        try {
            switch (peticion.getTipoOperacion()) {
                

                case READ_ALL: // El caso que tenías antes
                    ArrayList<Partido> lista = cad.leerPartidos();
                    respuesta.setPartidos(lista);
                    respuesta.setExito(lista != null);
                    respuesta.setMensaje("Lista de partidos recuperada.");
                    break;

                default:
                    respuesta.setExito(false);
                    respuesta.setMensaje("Operación de partido no soportada.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error en manejarPartidos: " + e.getMessage());
            e.printStackTrace();
            respuesta.setExito(false);
            respuesta.setMensaje("Error en el servidor: " + e.getMessage());
        }

        oos.writeObject(respuesta);
        oos.flush();
        System.out.println("DEBUG: Respuesta de partido enviada a Android");
    }
}
