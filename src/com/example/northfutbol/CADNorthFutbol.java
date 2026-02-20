/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.northfutbol;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import jdk.internal.org.objectweb.asm.Type;
import pojosnorthfutbol.Equipo;
import pojosnorthfutbol.ExcepcionNF;
import pojosnorthfutbol.Jugador;
import pojosnorthfutbol.Usuario;

/**
 *
 * @author DAM209
 */
public class CADNorthFutbol {

    private Connection conexion;

    //private String HOST = "jdbc:oracle:thin:@172.16.212.1:1521:test";
    private String HOST = "jdbc:oracle:thin:@192.168.1.209:1521:test";
    //private String HOST = "jdbc:oracle:thin:@172.16.209.1:1521:test";
    private String USERBD = "NF";
    private String PASSWORD = "kk";

    public CADNorthFutbol() throws ExcepcionNF {
        try {

            System.out.println("Conexion");
            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorBD(ex.getMessage());
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            throw e;
        }
    }

    private void conectarBD() throws ExcepcionNF {
        try {

            conexion = DriverManager.getConnection(HOST, USERBD, PASSWORD);

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            throw e;
        }
    }

    // Este método privado nos da un objeto 'Connection' listo para usar.
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(HOST, USERBD, PASSWORD);
    }

    // TEST DE CONEXIÓN
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            // isValid(2) intenta hacer un ping a la BD con timeout de 2 segundos.
            return conn.isValid(2);
        } catch (SQLException e) {
            System.out.println("FALLO Conexión BD: " + e.getMessage());
            return false;
        }
    }

    /**
     * Este método hace una consulta recogiendo datos de todos los equipos de la
     * base de datos
     *
     * @return Lista (ArrayList) de equipos leídos
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 22/01/2025 DD/MM/AAAA
     */
    public ArrayList<Equipo> leerEquipos() throws ExcepcionNF {
        ArrayList<Equipo> listaEquipos = new ArrayList<>();
        Equipo eq;
        String dql = "SELECT * FROM equipo";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();

            ResultSet resultado = sentencia.executeQuery(dql);
            while (resultado.next()) {
                eq = new Equipo();
                eq.setIdEquipo(resultado.getInt("ID_EQUIPO"));
                eq.setNombre(resultado.getString("NOMBRE"));
                eq.setCiudad(resultado.getString("CIUDAD"));
                eq.setEntrenador(resultado.getString("ENTRENADOR"));
                eq.setGrupo(resultado.getString("GRUPO"));

                listaEquipos.add(eq);
            }
            resultado.close();

            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {

            ExcepcionNF e = new ExcepcionNF();

            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);

            throw e;
        }
        return listaEquipos;
    }

    /**
     * Este método elimina un registro de la tabla Equipo según un identificador
     * específico
     *
     * @return Cantidad de registros eliminados
     * @param idEquipo Identificador del equipo a eliminar
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 22/01/2025 DD/MM/AAAA
     */
    public Integer eliminarEquipo(Integer idEquipo) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            dml = "DELETE equipo WHERE id_equipo = " + idEquipo;
            registrosAfectados = sentencia.executeUpdate(dml);

            sentencia.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 2292:
                    e.setMensajeErrorUsuario("No se puede eliminar este equipo ya que tiene asociado un jugador, una noticia, un partido o es seguido por algún usuario");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
                    break;
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);

            throw e;
        }

        return registrosAfectados;
    }

    /**
     * Este método modifica un registro de la tabla Equipo según un
     * identificador específico con datos de un objeto Equipo
     *
     * @return Cantidad de registros eliminados
     * @param idEquipo Identificador del equipo a modificar
     * @param equipo Objeto con la información a modificar
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 22/01/2025 DD/MM/AAAA
     */
    public Integer modificarEquipo(Integer idEquipo, Equipo equipo) throws ExcepcionNF {
        int registrosAfectados = 0;
        String sql = "call modificar_equipo(?, ?, ?, ?, ?)";
        try {
            conectarBD();

            CallableStatement sentenciaLlamable = conexion.prepareCall(sql);

            sentenciaLlamable.setString(1, equipo.getNombre());
            sentenciaLlamable.setString(2, equipo.getCiudad());
            sentenciaLlamable.setString(3, equipo.getEntrenador());
            sentenciaLlamable.setString(4, equipo.getGrupo());
            sentenciaLlamable.setObject(5, idEquipo, java.sql.Types.INTEGER);

            registrosAfectados = sentenciaLlamable.executeUpdate();

            sentenciaLlamable.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Ya existe un equipo con el mismo nombre");
                    break;
                case 1407:
                    e.setMensajeErrorUsuario("Todos los campos son obligatorios");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("El grupo solamente puede ser: 1, 2, 3, 4 o 5");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
                    break;
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(sql);

            throw e;
        }

        return registrosAfectados;
    }

    /**
     * Este metodo inserta un registro en la tabla Equipo de la base de datos
     *
     * @param equipo Objeto que contiene toda la información a insertar
     * @return Cantidad de registros insertados
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 23/01/2026 DD/MM/AAAA
     */
    public Integer insertarEquipo(Equipo equipo) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "INSERT INTO equipo (id_equipo, nombre, ciudad, entrenador, grupo) VALUES (SEQ_EQUIPO.nextval, ?, ?, ?, ?)";

        try {
            conectarBD();
            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

            sentenciaPreparada.setString(1, equipo.getNombre());
            sentenciaPreparada.setString(2, equipo.getCiudad());
            sentenciaPreparada.setString(3, equipo.getEntrenador());
            sentenciaPreparada.setString(4, equipo.getGrupo());

            registrosAfectados = sentenciaPreparada.executeUpdate();

            sentenciaPreparada.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Ya existe un equipo con el mismo nombre");
                    break;
                case 1400:
                    e.setMensajeErrorUsuario("Todos los campos son obligatorios");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("El grupo solamente puede ser: 1, 2, 3, 4 o 5");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
                    break;
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);

            throw e;
        }

        return registrosAfectados;
    }

    public Integer insertarUsuario(Usuario usuario) throws ExcepcionNF {
        int registrosAfectados = 0;

        String dml = "INSERT INTO usuario (id_usuario, nombre, email, rol, contrasenna, foto_perfil) VALUES (SEQ_USUARIO.nextval, ?, ?, ?, ?, ?)";

        try {
            conectarBD();
            PreparedStatement ps = conexion.prepareStatement(dml);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getRol());
            ps.setString(4, usuario.getContrasenna());
            ps.setString(5, usuario.getFotoPerfil());

            registrosAfectados = ps.executeUpdate();

            ps.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Ya existe un usuario con ese nombre o email");
                    break;
                case 1400:
                    e.setMensajeErrorUsuario("Todos los campos obligatorios deben estar rellenos");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("El email o el rol no tienen un formato válido");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);

            throw e;
        }
        return registrosAfectados;
    }

    public Integer modificarUsuario(Integer idUsuario, Usuario usuario) throws ExcepcionNF {
        int registrosAfectados = 0;
        String sql = "call modificar_usuario(?, ?, ?, ?, ?, ?)";

        try {
            conectarBD();

            CallableStatement cs = conexion.prepareCall(sql);

            cs.setString(1, usuario.getNombre());
            cs.setString(2, usuario.getEmail());
            cs.setString(3, usuario.getRol());
            cs.setString(4, usuario.getContrasenna());
            cs.setString(5, usuario.getFotoPerfil());
            cs.setObject(6, idUsuario, java.sql.Types.INTEGER);

            registrosAfectados = cs.executeUpdate();

            cs.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Ya existe un usuario con ese nombre o email");
                    break;
                case 1407:
                    e.setMensajeErrorUsuario("Todos los campos obligatorios deben estar rellenos");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("El email o el rol no tienen un formato válido");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(sql);

            throw e;
        }

        return registrosAfectados;
    }

    public Integer eliminarUsuario(Integer idUsuario) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "";

        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();

            dml = "DELETE FROM usuario WHERE id_usuario = " + idUsuario;
            registrosAfectados = sentencia.executeUpdate(dml);

            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 2292:
                    e.setMensajeErrorUsuario("No se puede eliminar el usuario porque tiene datos asociados");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);

            throw e;
        }

        return registrosAfectados;
    }

    public Usuario leerUsuario(Integer idUsuario) throws ExcepcionNF {
        Usuario u = null;
        // Usamos ? para evitar Inyección SQL
        String dql = "SELECT * FROM usuario WHERE ID_USUARIO = ?";

        try {
            conectarBD();
            // Usar PreparedStatement es más seguro y eficiente para filtros
            PreparedStatement sentencia = conexion.prepareStatement(dql);
            sentencia.setObject(1, idUsuario);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                u = new Usuario();
                u.setIdUsuario(resultado.getInt("ID_USUARIO"));
                u.setNombre(resultado.getString("NOMBRE"));
                u.setEmail(resultado.getString("EMAIL"));
                u.setRol(resultado.getString("ROL"));
                u.setContrasenna(resultado.getString("CONTRASENNA"));
                u.setFotoPerfil(resultado.getString("FOTO_PERFIL"));
            }

            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorUsuario("Error al buscar el usuario. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);
            throw e;
        }

        return u; // Retorna el usuario o null si no lo encuentra
    }

    public ArrayList<Usuario> leerUsuarios() throws ExcepcionNF {
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();
        Usuario u;
        String dql = "SELECT * FROM usuario";

        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();

            ResultSet resultado = sentencia.executeQuery(dql);
            while (resultado.next()) {
                u = new Usuario();
                u.setIdUsuario(resultado.getInt("ID_USUARIO"));
                u.setNombre(resultado.getString("NOMBRE"));
                u.setEmail(resultado.getString("EMAIL"));
                u.setRol(resultado.getString("ROL"));
                u.setContrasenna(resultado.getString("CONTRASENNA"));
                u.setFotoPerfil(resultado.getString("FOTO_PERFIL"));

                listaUsuarios.add(u);
            }

            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);

            throw e;
        }

        return listaUsuarios;
    }

    /**
     * Este método elimina un registro de la tabla NF.JUGADOR de la base de
     * datos.
     *
     * @param idJugador Identificador del jugador que se desea eliminar.
     * @return Cantidad de registros eliminados (debería ser 1 si se elimina
     * correctamente)
     * @throws ExcepcionNF Se lanza si ocurre algún error en la base de datos,
     * por ejemplo, si el jugador está referenciado en otras tablas.
     * @author Hugo Touriño
     * @version 1.0
     * @since 25/01/2026
     */
    public Integer eliminarJugador(Integer idJugador) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();

            dml = "DELETE FROM NF.JUGADOR WHERE ID_JUGADOR = " + idJugador;
            registrosAfectados = sentencia.executeUpdate(dml);

            sentencia.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);

            switch (ex.getErrorCode()) {
                case 2292:
                    e.setMensajeErrorUsuario(
                            "No se puede eliminar el jugador porque está relacionado con otros registros (por ejemplo, partidos o estadísticas)."
                    );
                    break;
                default:
                    e.setMensajeErrorUsuario(
                            "Error general del sistema. Consulte con el administrador."
                    );
                    break;
            }
            throw e;
        }
        return registrosAfectados;
    }

    /**
     * Este método obtiene todos los registros de la tabla NF.JUGADOR de la base
     * de datos.
     *
     * @return Lista de objetos Jugador que contienen toda la información de
     * cada jugador, incluyendo el equipo asociado.
     * @throws ExcepcionNF Se lanza si ocurre algún error al consultar la base
     * de datos.
     * @author Hugo Touriño
     * @version 1.0
     * @since 25/01/2026
     */
    public ArrayList<Jugador> leerJugadores() throws ExcepcionNF {
        ArrayList<Jugador> listaJugadores = new ArrayList<>();
        Jugador j;
        Equipo eq;

        String dql = "SELECT ID_JUGADOR, ID_EQUIPO, NOMBRE, APELLIDO, POSICION, "
                + "FECHA_NACIMIENTO, PAIS_ORIGEN, DORSAL "
                + "FROM NF.JUGADOR";

        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();

            ResultSet resultado = sentencia.executeQuery(dql);

            while (resultado.next()) {
                // Jugador
                j = new Jugador();
                j.setIdJugador(resultado.getInt("ID_JUGADOR"));
                j.setNombre(resultado.getString("NOMBRE"));
                j.setApellido(resultado.getString("APELLIDO"));
                j.setPosicion(resultado.getString("POSICION"));
                j.setFechaNacimiento(resultado.getDate("FECHA_NACIMIENTO"));
                j.setPaisOrigen(resultado.getString("PAIS_ORIGEN"));
                j.setDorsal(resultado.getInt("DORSAL"));

                // Equipo (relación)
                eq = new Equipo();
                eq.setIdEquipo(resultado.getInt("ID_EQUIPO"));

                j.setEquipo(eq);

                listaJugadores.add(j);
            }

            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {

            ExcepcionNF e = new ExcepcionNF();

            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);
            throw e;
        }

        return listaJugadores;
    }

    /**
     * Este método inserta un registro en la tabla NF.JUGADOR de la base de
     * datos.
     *
     * @param jugador Objeto que contiene toda la información del jugador a
     * insertar, incluyendo el equipo asociado.
     * @return Cantidad de registros insertados (debería ser 1 si se inserta
     * correctamente)
     * @throws ExcepcionNF Se lanza si ocurre algún error en la base de datos,
     * como campos nulos o violación de restricciones.
     * @author Hugo Touriño
     * @version 1.0
     * @since 25/01/2026
     */
    public Integer insertarJugador(Jugador jugador) throws ExcepcionNF {
        int registrosAfectados = 0;

        String dml = "INSERT INTO NF.JUGADOR "
                + "(ID_JUGADOR, ID_EQUIPO, NOMBRE, APELLIDO, POSICION, FECHA_NACIMIENTO, PAIS_ORIGEN, DORSAL) "
                + "VALUES (SEQ_JUGADOR.nextval, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conectarBD();
            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

            // Relación con Equipo
            sentenciaPreparada.setObject(1, jugador.getEquipo().getIdEquipo(), Type.INT);
            sentenciaPreparada.setString(2, jugador.getNombre());
            sentenciaPreparada.setString(3, jugador.getApellido());
            sentenciaPreparada.setString(4, jugador.getPosicion());
            sentenciaPreparada.setObject(5, new java.sql.Date(jugador.getFechaNacimiento().getTime()));
            sentenciaPreparada.setString(6, jugador.getPaisOrigen());
            sentenciaPreparada.setObject(7, jugador.getDorsal(), Type.INT);

            registrosAfectados = sentenciaPreparada.executeUpdate();

            sentenciaPreparada.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Ya existe un jugador con el mismo ID");
                    break;
                case 1400:
                    e.setMensajeErrorUsuario("Tiene que rellenar todos los huecos de datos");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("Algún valor no cumple las restricciones de la base de datos");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
                    break;
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);

            throw e;
        }

        return registrosAfectados;
    }

    /**
     * Este método modifica un registro existente en la tabla NF.JUGADOR de la
     * base de datos.
     *
     * @param idJugador Identificador del jugador que se desea modificar.
     * @param jugador Objeto que contiene la nueva información del jugador,
     * incluyendo el equipo asociado.
     * @return Cantidad de registros afectados (debería ser 1 si se modifica
     * correctamente)
     * @throws ExcepcionNF Se lanza si ocurre algún error en la base de datos,
     * como campos nulos o violación de restricciones.
     * @author Hugo Touriño
     * @version 1.0
     * @since 25/01/2026
     */
    public Integer modificarJugador(Integer idJugador, Jugador jugador) throws ExcepcionNF {
        int registrosAfectados = 0;

        String sql = "call modificar_jugador(?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conectarBD();

            CallableStatement sentenciaLlamable = conexion.prepareCall(sql);

            // Parámetros del procedimiento
            sentenciaLlamable.setString(1, jugador.getNombre());
            sentenciaLlamable.setString(2, jugador.getApellido());
            sentenciaLlamable.setString(3, jugador.getPosicion());
            sentenciaLlamable.setObject(4, new java.sql.Date(jugador.getFechaNacimiento().getTime()));
            sentenciaLlamable.setString(5, jugador.getPaisOrigen());
            sentenciaLlamable.setObject(6, jugador.getDorsal(), Type.INT);
            sentenciaLlamable.setObject(7, jugador.getEquipo().getIdEquipo(), Type.INT);
            sentenciaLlamable.setObject(8, idJugador, java.sql.Types.INTEGER);

            registrosAfectados = sentenciaLlamable.executeUpdate();

            sentenciaLlamable.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("No se puede añadir este jugador por que ya existe uno con esta informacion");
                    break;
                case 1407:
                    e.setMensajeErrorUsuario("Tiene que rellenar todos los huecos de datos");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("Algún valor no cumple las restricciones de la base de datos");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
                    break;
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(sql);

            throw e;
        }

        return registrosAfectados;
    }
    
    //=============================//
    //=====MÉTODOS APLICACIÓN======//
    //=============================//
    
    public Usuario validarLogin(String email, String contrasenna) throws ExcepcionNF {
        Usuario u = null;
        //Annadido el trim hasta implementar el hasheo
        String dql = "SELECT * FROM usuario WHERE email = ? AND TRIM(contrasenna) = ?";

        try {
            conectarBD();
            // Usar PreparedStatement es más seguro y eficiente para filtros
            PreparedStatement sentencia = conexion.prepareStatement(dql);
            sentencia.setString(1, email);
            sentencia.setString(2, contrasenna);

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                u = new Usuario();
                u.setIdUsuario(resultado.getInt("ID_USUARIO"));
                u.setNombre(resultado.getString("NOMBRE"));
                u.setEmail(resultado.getString("EMAIL"));
                u.setRol(resultado.getString("ROL"));
                u.setContrasenna(resultado.getString("CONTRASENNA"));
                u.setFotoPerfil(resultado.getString("FOTO_PERFIL"));
            }

            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorUsuario("Error al buscar el usuario. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);
            throw e;
        }

        return u; // Retorna el usuario o null si no lo encuentra
    }
    
    public Integer registrarUsuario(Usuario usuario) throws ExcepcionNF {
        int registrosAfectados = 0;

        String dml = "INSERT INTO usuario (id_usuario, nombre, email, rol, contrasenna, foto_perfil) VALUES (SEQ_USUARIO.nextval, ?, ?, ?, ?, ?)";

        try {
            conectarBD();
            PreparedStatement ps = conexion.prepareStatement(dml);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getRol());
            ps.setString(4, usuario.getContrasenna());
            ps.setString(5, usuario.getFotoPerfil());

            registrosAfectados = ps.executeUpdate();

            ps.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Ya existe un usuario con ese nombre o email");
                    break;
                case 1400:
                    e.setMensajeErrorUsuario("Todos los campos obligatorios deben estar rellenos");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("El email o el rol no tienen un formato válido");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);

            throw e;
        }
        return registrosAfectados;
    }
    
    public Integer modificarNombreEmailUsuario(Integer idUsuario, Usuario usuario) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "UPDATE usuario SET nombre = ?, email = ? WHERE id_usuario = ?";
        
        try {
            conectarBD();

            PreparedStatement ps = conexion.prepareStatement(dml);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setObject(3, idUsuario, java.sql.Types.INTEGER);

            registrosAfectados = ps.executeUpdate();

            ps.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Ya existe un usuario con ese nombre o email");
                    break;
                case 1407:
                    e.setMensajeErrorUsuario("Todos los campos obligatorios deben estar rellenos");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("El email o el rol no tienen un formato válido");
                    break;
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
                    System.out.println(ex.getErrorCode());
                    System.out.println(ex.getMessage());
                    System.out.println(dml);
            }

            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);

            throw e;
        }

        return registrosAfectados;
        
    }
}
