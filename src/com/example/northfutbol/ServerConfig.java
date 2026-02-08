/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

/**
 *
 * @author DAM209
 */
public class ServerConfig {
    public static final Environment ENTORNO_ACTUAL = Environment.CLASE;
    
    public enum Environment {
        CASA, CLASE;
    }
    
    public static String getDbIp() {
        switch(ENTORNO_ACTUAL) {
            case CASA:
                return "192.168.1.209";
                
            case CLASE:
                return "172.16.209.1";
                
            default:
                return "172.16.209.1";
        }
    }
    
    //COnfiguracion fija de la DB
    public static final String DB_PORT = "1521";
    public static final String DB_SID = "test";
    public static final String DB_USER = "nf";
    public static final String DB_PASS = "kk";
    
    // Puerto de escucha del servidor
    public static final int SERVER_PORT = 5000;
}
