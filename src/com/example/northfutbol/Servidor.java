/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import pojosnorthfutbol.ExcepcionNF;


/**
 * CLASE PRINCIPAL DEL SERVIDOR
 * ===============================
 * Esta clase es el punto de entradfa (main) de nuestro servidor
 * Su función es:
 * 1. Abrir un puerto para escuchar pticiones
 * 2. Verificar que la base de datos es accesible
 * 3. Quedarse en un bucle infinito esperando a que los clientes de Andorid de conecten.
 * 4. Cunado un cliente se conecta, le asigna un "Hilo Ciente" para atenderle de forma exclusiva.
 * 
 */

public class Servidor {
    
    public static void main(String[] args) throws ExcepcionNF{
        // 1. DEFINIMOS EL PUERTO
        //El puerto 5000 (es arbitrario, pero tiene que el mismo que pongamos en el cliente.)
        int port = ServerConfig.SERVER_PORT;
        
        //2. INICIALIZAMOS EL SERVIDOR (ServerSocket)
        // 
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("=========================");
            System.out.println("SERVIDOR INICIALIZADO CORREECTAMENTE");
            System.out.println("ESCUCHANDO EN EL PUERTO " + port);
            System.out.println("=========================");
            System.out.println("=========================");
            
            //3. VERIFICAMOS LA CONEXION CON ORACLE
            String ipDb = ServerConfig.getDbIp();
            System.out.println("--> Verificamos conexión en: " + ipDb);
            
            
            CADNorthFutbol cadTest = new CADNorthFutbol();
            
            if (cadTest.testConnection()) {
                System.out.println("[OK] Conexión exitosa con la Base de Datos");
            } else {
                System.out.println("[ERROR] No se pudo conectar a la Base de Datos");
                System.out.println("[AYUDA] Revisar ServerConfig.java y asegurar que: " 
                        + "\n 1. Que la VM esté encendida"
                        + "\n 2. Que la IP sea correcta en la VM"
                        + "\n 3. Que hay ping entre el anfitrión y la MV");
                
            }
            
            // 4. BULCE INFINITO DE ESCUCHA
            // EL servidor nunca termina por sí mismo. Siempre queda a la espera
            // de clientes.
            System.out.println("Esperando cliente....");
            
            while(true) {
                // 4.1 EL BLOQUEO: accept()
                // Esta linea congela el programa hasta que un cliente intente conectarse
                Socket socket = serverSocket.accept();
                System.out.println("¡Nuevo cliente conectado! Desde la IP: " + socket.getInetAddress().getHostAddress());
            
                // 4.2 DELEGAMOS EN UN HILO
                // Si atendieramos al cliente aquí, nadie más podría conectarse
                // mientras tanto
                //Por eso, crear im trabajador (HiloCliente) dedicado sollo a este usuario
                HiloClienteUsuario hilo = new HiloClienteUsuario(socket);
                
                // 4.3 ARRANCAR EL HILO
                hilo.start();
            }
      
        } catch (IOException e) {
            System.out.println("[ERROR FATAL] Servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
}
