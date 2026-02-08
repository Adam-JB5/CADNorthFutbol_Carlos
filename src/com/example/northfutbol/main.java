/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.util.ArrayList;
import pojosnorthfutbol.Comentario;
import pojosnorthfutbol.Equipo;
import pojosnorthfutbol.ExcepcionNF;
import pojosnorthfutbol.Jugador;
import pojosnorthfutbol.Usuario;

/**
 *
 * @author adamj
 */
public class main {
    public static void main(String[] args) throws ExcepcionNF {

        //====leerEquipos()=======
//        try {
//            CADNorthFutbol cad = new CADNorthFutbol();
//            ArrayList<Equipo> equipos = cad.leerEquipos();
//            System.out.println(equipos);
//            
//        } catch (ExcepcionNF e) {
//            System.out.println(e);
//        }

        //====eliminarEquipo()=======
//        try {
//            CADNorthFutbol cad = new CADNorthFutbol();
//            Integer registrosAfectados = cad.eliminarEquipo(5);
//            System.out.println(registrosAfectados);
//        } catch (ExcepcionNF e) {
//            System.out.println(e);
//        }
        //====modificarEquipo()=======
//        try {
//            CADNorthFutbol cad = new CADNorthFutbol();
//            Equipo equipo = new Equipo();
//            equipo.setNombre("kk");
//            equipo.setCiudad("kk");
//            equipo.setEntrenador("kk");
//            equipo.setGrupo("6");
//            Integer registros = cad.modificarEquipo(1, equipo);
//            System.out.println(registros);
//        } catch (ExcepcionNF e) {
//            System.out.println(e);
//        }
        
        //====insertarEquipo()=======
//        try {
//            CADNorthFutbol cad = new CADNorthFutbol();
//            Equipo equipo = new Equipo();
//            
//            equipo.setNombre("kkIns23");
//            equipo.setCiudad("kk");
//            equipo.setEntrenador("kk");
//            equipo.setGrupo("6");
//            Integer registros = cad.insertarEquipo(equipo);
//            System.out.println(registros);
//        } catch (ExcepcionNF e) {
//            System.out.println(e);
//        }
        //==== insertarUsuario()======
//        try {
//            CADNorthFutbol cad = new CADNorthFutbol();
//            Usuario u = new Usuario();
//            u.setNombre("Juanrgegeo");
//            u.setEmail("juan@email.com");
//            u.setRol("Q");
//            u.setContrasenna("1234");
//            u.setFotoPerfil("juan.png");
//            Integer registros = cad.insertarUsuario(u);
//            System.out.print(registros);
//        } catch (ExcepcionNF e) {
//            System.out.print(e);
//        }
        //==== modificarUsuario() =====
//        try {
//            CADNorthFutbol cad = new CADNorthFutbol();
//            Usuario u = new Usuario();
//            u.setNombre("Mario Ruiz");
//            u.setEmail("mario@email.com");
//            u.setRol("USER");
//            u.setContrasenna("pass123");
//            u.setFotoPerfil("mario.png");
//
//            Integer registros = cad.modificarUsuario(4, u);
//        } catch (ExcepcionNF e) {
//            System.out.print(e);
//        }
        //===== eliminarUsuario() ======
//        try {
//            CADNorthFutbol cad = new CADNorthFutbol();
//            Integer registrosAfectados = cad.eliminarUsuario(5);
//            System.out.println(registrosAfectados);
//        } catch (ExcepcionNF e) {
//            System.out.println(e);
//        }
        //====== leerUsuario() =====
//        try {
//            CADNorthFutbol cad = new CADNorthFutbol();
//            ArrayList<Usuario> usuarios = cad.leerUsuarios();
//            System.out.println(usuarios);
//            
//        } catch (ExcepcionNF e) {
//            System.out.println(e);
//        }

        //==== leerJugadores() =======
//        try {
//             CADNorthFutbol cad = new CADNorthFutbol();
//            ArrayList<Jugador> jugadores = cad.leerJugadores();
//             System.out.println(jugadores);
// 
//         } catch (ExcepcionNF e) {
//             System.out.println(e);
//         }
          
        //==== eliminarJugador() =======
//         try {
//             CADNorthFutbol cad = new CADNorthFutbol();
//            Integer registrosAfectados = cad.eliminarJugador(1);
//            System.out.println(registrosAfectados);
// 
//       } catch (ExcepcionNF e) {
//            System.out.println(e);
//       }
          
        //==== modificarJugador() =======
//         try {
//             CADNorthFutbol cad = new CADNorthFutbol();
// 
//             Jugador jugador = new Jugador();
//             Equipo equipo = new Equipo();
// 
//             equipo.setIdEquipo(1); // ID de equipo existente
//             jugador.setEquipo(equipo);
// 
//             jugador.setNombre("Juan");
//             jugador.setApellido("Pérez");
//             jugador.setPosicion("F");
//             jugador.setFechaNacimiento(new java.util.Date());
//             jugador.setPaisOrigen("España");
//             jugador.setDorsal(10);
//  
//             Integer registros = cad.modificarJugador(1, jugador);
//             System.out.println(registros);
// 
//        } catch (ExcepcionNF e) {
//              System.out.println(e);
//        }
        
      //==== insertarJugador() =======
//        try {
//             CADNorthFutbol cad = new CADNorthFutbol();
// 
//             Jugador jugador = new Jugador();
//             Equipo equipo = new Equipo();
// 
//             equipo.setIdEquipo(1); // ID de equipo existente
//             jugador.setEquipo(equipo);
// 
//             jugador.setNombre("Luis");
//             jugador.setApellido("Gómez");
//             jugador.setPosicion("D");
//             jugador.setFechaNacimiento(new java.util.Date());
//             jugador.setPaisOrigen("Argentina");
//             jugador.setDorsal(8);
// 
//             Integer registros = cad.insertarJugador(jugador);
//             System.out.println(registros);
// 
//         } catch (ExcepcionNF e) {
//             System.out.println(e);
//         }
    }
}
