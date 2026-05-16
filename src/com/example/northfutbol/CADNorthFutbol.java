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
import pojosnorthfutbol.Equipo;
import pojosnorthfutbol.ExcepcionNF;
import pojosnorthfutbol.Jugador;
import pojosnorthfutbol.Noticia;
import pojosnorthfutbol.Usuario;
import pojosnorthfutbol.Partido;
import pojosnorthfutbol.Jornada;
import pojosnorthfutbol.Comentario;
import pojosnorthfutbol.EventoPartido;

/**
 *
 * @author DAM209
 */
public class CADNorthFutbol {

    private Connection conexion;

    //private String HOST = "jdbc:oracle:thin:@172.16.212.1:1521:test";
    private String HOST = "jdbc:oracle:thin:@192.168.1.209:1521:test";
    //private String HOST = "jdbc:oracle:thin:@172.16.209.1:1521:test";
    //private String HOST = "jdbc:oracle:thin:@10.177.104.210:1521:test";
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
            sentenciaPreparada.setObject(1, jugador.getEquipo().getIdEquipo(), java.sql.Types.INTEGER);
            sentenciaPreparada.setString(2, jugador.getNombre());
            sentenciaPreparada.setString(3, jugador.getApellido());
            sentenciaPreparada.setString(4, jugador.getPosicion());
            sentenciaPreparada.setObject(5, new java.sql.Date(jugador.getFechaNacimiento().getTime()));
            sentenciaPreparada.setString(6, jugador.getPaisOrigen());
            sentenciaPreparada.setObject(7, jugador.getDorsal(), java.sql.Types.INTEGER);

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
            sentenciaLlamable.setObject(6, jugador.getDorsal(), java.sql.Types.INTEGER);
            sentenciaLlamable.setObject(7, jugador.getEquipo().getIdEquipo(), java.sql.Types.INTEGER);
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
        String dml = "UPDATE usuario SET nombre = ?, email = ?, foto_perfil = ? WHERE id_usuario = ?";

        try {
            conectarBD();

            PreparedStatement ps = conexion.prepareStatement(dml);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getFotoPerfil());
            ps.setObject(4, idUsuario, java.sql.Types.INTEGER);

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

    public ArrayList<Noticia> leerNoticias() throws ExcepcionNF {
        ArrayList<Noticia> listaNoticias = new ArrayList<>();
        Noticia n;
        Equipo eq;

        String dql = "SELECT N.*, E.NOMBRE "
                + "FROM NF.NOTICIA N "
                + "INNER JOIN NF.EQUIPO E ON N.ID_EQUIPO = E.ID_EQUIPO "
                + "ORDER BY N.FECHA_CREACION DESC";

        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            while (resultado.next()) {
                n = new Noticia();

                // Mapeo de campos básicos
                n.setIdNoticia(resultado.getInt("ID_NOTICIA"));
                n.setTitulo(resultado.getString("TITULO"));
                n.setSubtitulo(resultado.getString("SUBTITULO")); // Soporta NULL automáticamente
                n.setImagen(resultado.getString("IMAGEN"));
                n.setFechaCreacion(resultado.getDate("FECHA_CREACION"));

                // El campo CLOB se puede leer como String directamente en la mayoría de drivers modernos
                n.setContenido(resultado.getString("CONTENIDO"));

                // Relación con Equipo (ID_EQUIPO)
                eq = new Equipo();
                eq.setIdEquipo(resultado.getInt("ID_EQUIPO"));
                eq.setNombre(resultado.getString("NOMBRE"));
                n.setEquipo(eq);

                listaNoticias.add(n);
            }

            // Cierre de recursos
            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            // Tu gestión de excepciones personalizada
            ExcepcionNF e = new ExcepcionNF();

            e.setMensajeErrorUsuario("Error al cargar las noticias. Inténtelo más tarde.");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);

            throw e;
        }

        return listaNoticias;
    }

    public Noticia leerNoticia(int idNoticia) throws ExcepcionNF {
        Noticia n = null;
        Equipo eq;
        String dql = "SELECT N.*, E.NOMBRE "
                + "FROM NF.NOTICIA N "
                + "INNER JOIN NF.EQUIPO E ON N.ID_EQUIPO = E.ID_EQUIPO "
                + "WHERE N.ID_NOTICIA = ?";
        try {
            conectarBD();
            PreparedStatement ps = conexion.prepareStatement(dql);

            ps.setInt(1, idNoticia); // índice 1, no el valor del id

            ResultSet resultado = ps.executeQuery();

            if (resultado.next()) {
                n = new Noticia();
                // Mapeo de campos básicos
                n.setIdNoticia(resultado.getInt("ID_NOTICIA"));
                n.setTitulo(resultado.getString("TITULO"));
                n.setSubtitulo(resultado.getString("SUBTITULO"));
                n.setImagen(resultado.getString("IMAGEN"));
                n.setFechaCreacion(resultado.getDate("FECHA_CREACION"));
                n.setContenido(resultado.getString("CONTENIDO"));
                // Relación con Equipo
                eq = new Equipo();
                eq.setIdEquipo(resultado.getInt("ID_EQUIPO"));
                eq.setNombre(resultado.getString("NOMBRE"));
                n.setEquipo(eq);
            }
            // Cierre de recursos
            resultado.close();
            ps.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorUsuario("Error al cargar la noticia. Inténtelo más tarde.");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);
            throw e;
        }
        return n; // Devuelve null si no se encontró la noticia
    }

    public Integer insertarNoticia(Noticia noticia) throws ExcepcionNF {
        int registrosAfectados = 0;

        String dml = "INSERT INTO noticia (id_noticia, id_equipo, titulo, subtitulo, imagen, contenido, fecha_creacion) VALUES (SEQ_NOTICIA.nextval, ?, ?, ?, ?, ?, SYSDATE)";

        try {
            conectarBD();
            PreparedStatement ps = conexion.prepareStatement(dml);

            ps.setObject(1, noticia.getEquipo().getIdEquipo(), java.sql.Types.INTEGER);
            ps.setString(2, noticia.getTitulo());
            ps.setString(3, noticia.getSubtitulo());
            ps.setString(4, noticia.getImagen());
            ps.setString(5, noticia.getContenido());

            registrosAfectados = ps.executeUpdate();

            ps.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1400:
                    e.setMensajeErrorUsuario("Todos los campos son obligatorios");
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

    public ArrayList<Jugador> leerJugadoresPorEquipo(int idEquipo) throws ExcepcionNF {
        ArrayList<Jugador> listaJugadores = new ArrayList<>();
        Jugador j;
        Equipo eq;

        String dql = "SELECT J.*, E.NOMBRE AS NOMBRE_EQUIPO "
                + "FROM NF.JUGADOR J "
                + "INNER JOIN NF.EQUIPO E ON J.ID_EQUIPO = E.ID_EQUIPO "
                + "WHERE J.ID_EQUIPO = ? "
                + "ORDER BY J.DORSAL ASC";

        try {
            conectarBD();
            PreparedStatement sentencia = conexion.prepareStatement(dql);
            sentencia.setInt(1, idEquipo);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                j = new Jugador();

                j.setIdJugador(resultado.getInt("ID_JUGADOR"));
                j.setNombre(resultado.getString("NOMBRE"));
                j.setApellido(resultado.getString("APELLIDO"));
                j.setPosicion(resultado.getString("POSICION"));
                j.setFechaNacimiento(resultado.getDate("FECHA_NACIMIENTO"));
                j.setPaisOrigen(resultado.getString("PAIS_ORIGEN"));
                j.setDorsal(resultado.getInt("DORSAL"));

                eq = new Equipo();
                eq.setIdEquipo(resultado.getInt("ID_EQUIPO"));
                eq.setNombre(resultado.getString("NOMBRE"));
                j.setEquipo(eq);

                listaJugadores.add(j);
            }

            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            e.setMensajeErrorUsuario("Error al cargar los jugadores. Inténtelo más tarde.");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);

            throw e;
        }

        return listaJugadores;
    }

    public ArrayList<Equipo> leerEquiposPorGrupo(char grupo) throws ExcepcionNF {
        ArrayList<Equipo> equipos = new ArrayList<>();

        String dql = "SELECT * FROM equipo WHERE grupo = ?";

        try {
            conectarBD();
            // Usar PreparedStatement es más seguro y eficiente para filtros
            PreparedStatement sentencia = conexion.prepareStatement(dql);
            sentencia.setString(1, String.valueOf(grupo));

            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Equipo eq = new Equipo();
                eq.setIdEquipo(resultado.getInt("ID_EQUIPO"));
                eq.setNombre(resultado.getString("NOMBRE"));
                eq.setCiudad(resultado.getString("CIUDAD"));
                eq.setEntrenador(resultado.getString("ENTRENADOR"));
                eq.setGrupo(resultado.getString("GRUPO"));

                equipos.add(eq);
            }

            resultado.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorUsuario("Error. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);
            throw e;
        }

        return equipos;
    }

    /**
     * Este método hace una consulta recogiendo datos de todos los partidos de
     * la base de datos
     *
     * @return Lista (ArrayList) de partidos leídos
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 01/05/2026 DD/MM/AAAA
     */
    public ArrayList<Partido> leerPartidos() throws ExcepcionNF {
        ArrayList<Partido> listaPartidos = new ArrayList<>();
        Partido p;
        String dql = "SELECT p.*, "
                + "el.nombre AS nombre_local, el.ciudad AS ciudad_local, el.entrenador AS entrenador_local, el.grupo AS grupo_local, "
                + "ev.nombre AS nombre_visitante, ev.ciudad AS ciudad_visitante, ev.entrenador AS entrenador_visitante, ev.grupo AS grupo_visitante "
                + "FROM partido p "
                + "JOIN equipo el ON p.id_local = el.id_equipo "
                + "JOIN equipo ev ON p.id_visitante = ev.id_equipo";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();

            ResultSet resultado = sentencia.executeQuery(dql);
            while (resultado.next()) {
                p = new Partido();
                p.setIdPartido(resultado.getInt("ID_PARTIDO"));

                Equipo local = new Equipo();
                local.setIdEquipo(resultado.getInt("ID_LOCAL"));
                local.setNombre(resultado.getString("NOMBRE_LOCAL"));
                local.setCiudad(resultado.getString("CIUDAD_LOCAL"));
                local.setEntrenador(resultado.getString("ENTRENADOR_LOCAL"));
                local.setGrupo(resultado.getString("GRUPO_LOCAL"));
                p.setLocal(local);

                Equipo visitante = new Equipo();
                visitante.setIdEquipo(resultado.getInt("ID_VISITANTE"));
                visitante.setNombre(resultado.getString("NOMBRE_VISITANTE"));
                visitante.setCiudad(resultado.getString("CIUDAD_VISITANTE"));
                visitante.setEntrenador(resultado.getString("ENTRENADOR_VISITANTE"));
                visitante.setGrupo(resultado.getString("GRUPO_VISITANTE"));
                p.setVisitante(visitante);

                Jornada jornada = new Jornada();
                jornada.setIdJornada(resultado.getInt("ID_JORNADA"));
                p.setJornada(jornada);

                p.setFecha(resultado.getDate("FECHA"));
                p.setEstadio(resultado.getString("ESTADIO"));
                p.setEstado(resultado.getString("ESTADO"));
                p.setGolesLocal(resultado.getInt("GOLES_LOCAL"));
                p.setGolesVisitante(resultado.getInt("GOLES_VISITANTE"));

                listaPartidos.add(p);
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
        return listaPartidos;
    }

    /**
     * Este método elimina un registro de la tabla Partido según un
     * identificador específico
     *
     * @param idPartido Identificador del partido a eliminar
     * @return Cantidad de registros eliminados
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 01/05/2026 DD/MM/AAAA
     */
    public Integer eliminarPartido(Integer idPartido) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            dml = "DELETE partido WHERE id_partido = " + idPartido;
            registrosAfectados = sentencia.executeUpdate(dml);

            sentencia.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 2292:
                    e.setMensajeErrorUsuario("No se puede eliminar este partido ya que tiene registros asociados");
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
     * Este método modifica un registro existente en la tabla Partido de la base
     * de datos
     *
     * @param idPartido Identificador del partido que se desea modificar
     * @param partido Objeto que contiene la nueva información del partido,
     * incluyendo los equipos y la jornada asociados
     * @return Cantidad de registros afectados
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 01/05/2026 DD/MM/AAAA
     */
    public Integer modificarPartido(Integer idPartido, Partido partido) throws ExcepcionNF {
        int registrosAfectados = 0;
        String sql = "call modificar_partido(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conectarBD();

            CallableStatement sentenciaLlamable = conexion.prepareCall(sql);

            sentenciaLlamable.setObject(1, partido.getLocal().getIdEquipo(), java.sql.Types.INTEGER);
            sentenciaLlamable.setObject(2, partido.getVisitante().getIdEquipo(), java.sql.Types.INTEGER);
            sentenciaLlamable.setObject(3, partido.getJornada().getIdJornada(), java.sql.Types.INTEGER);
            sentenciaLlamable.setObject(4, new java.sql.Date(partido.getFecha().getTime()));
            sentenciaLlamable.setString(5, partido.getEstadio());
            sentenciaLlamable.setString(6, partido.getEstado());
            sentenciaLlamable.setObject(7, partido.getGolesLocal(), java.sql.Types.INTEGER);
            sentenciaLlamable.setObject(8, partido.getGolesVisitante(), java.sql.Types.INTEGER);
            sentenciaLlamable.setObject(9, idPartido, java.sql.Types.INTEGER);

            registrosAfectados = sentenciaLlamable.executeUpdate();

            sentenciaLlamable.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1407:
                    e.setMensajeErrorUsuario("Todos los campos obligatorios deben estar rellenos");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("El estado del partido solo puede ser: N, C o F");
                    break;
                case 2291:
                    e.setMensajeErrorUsuario("El equipo local, visitante o jornada indicados no existen");
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
     * Este método inserta un registro en la tabla Partido de la base de datos
     *
     * @param partido Objeto que contiene toda la información del partido a
     * insertar, incluyendo los equipos y la jornada asociados
     * @return Cantidad de registros insertados
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 01/05/2026 DD/MM/AAAA
     */
    public Integer insertarPartido(Partido partido) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "INSERT INTO partido (id_partido, id_local, id_visitante, id_jornada, fecha, estadio, estado, goles_local, goles_visitante) "
                + "VALUES (SEQ_PARTIDO.nextval, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conectarBD();
            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

            sentenciaPreparada.setObject(1, partido.getLocal().getIdEquipo(), java.sql.Types.INTEGER);
            sentenciaPreparada.setObject(2, partido.getVisitante().getIdEquipo(), java.sql.Types.INTEGER);
            sentenciaPreparada.setObject(3, partido.getJornada().getIdJornada(), java.sql.Types.INTEGER);
            sentenciaPreparada.setObject(4, new java.sql.Date(partido.getFecha().getTime()));
            sentenciaPreparada.setString(5, partido.getEstadio());
            sentenciaPreparada.setString(6, partido.getEstado());
            sentenciaPreparada.setObject(7, partido.getGolesLocal(), java.sql.Types.INTEGER);
            sentenciaPreparada.setObject(8, partido.getGolesVisitante(), java.sql.Types.INTEGER);

            registrosAfectados = sentenciaPreparada.executeUpdate();

            sentenciaPreparada.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1400:
                    e.setMensajeErrorUsuario("Todos los campos obligatorios deben estar rellenos");
                    break;
                case 2290:
                    e.setMensajeErrorUsuario("El estado del partido solo puede ser: N, C o F");
                    break;
                case 2291:
                    e.setMensajeErrorUsuario("El equipo local, visitante o jornada indicados no existen");
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
     * Este método hace una consulta recogiendo los datos de un partido concreto
     * de la base de datos
     *
     * @param idPartido Identificador del partido a leer
     * @return Objeto Partido con todos sus datos, o null si no existe
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 01/05/2026 DD/MM/AAAA
     */
    public Partido leerPartido(Integer idPartido) throws ExcepcionNF {
        Partido p = null;
        String dql = "SELECT p.*, "
                + "el.nombre AS nombre_local, el.ciudad AS ciudad_local, el.entrenador AS entrenador_local, el.grupo AS grupo_local, "
                + "ev.nombre AS nombre_visitante, ev.ciudad AS ciudad_visitante, ev.entrenador AS entrenador_visitante, ev.grupo AS grupo_visitante "
                + "FROM partido p "
                + "JOIN equipo el ON p.id_local = el.id_equipo "
                + "JOIN equipo ev ON p.id_visitante = ev.id_equipo "
                + "WHERE p.id_partido = " + idPartido;
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            if (resultado.next()) {
                p = new Partido();
                p.setIdPartido(resultado.getInt("ID_PARTIDO"));

                Equipo local = new Equipo();
                local.setIdEquipo(resultado.getInt("ID_LOCAL"));
                local.setNombre(resultado.getString("NOMBRE_LOCAL"));
                local.setCiudad(resultado.getString("CIUDAD_LOCAL"));
                local.setEntrenador(resultado.getString("ENTRENADOR_LOCAL"));
                local.setGrupo(resultado.getString("GRUPO_LOCAL"));
                p.setLocal(local);

                Equipo visitante = new Equipo();
                visitante.setIdEquipo(resultado.getInt("ID_VISITANTE"));
                visitante.setNombre(resultado.getString("NOMBRE_VISITANTE"));
                visitante.setCiudad(resultado.getString("CIUDAD_VISITANTE"));
                visitante.setEntrenador(resultado.getString("ENTRENADOR_VISITANTE"));
                visitante.setGrupo(resultado.getString("GRUPO_VISITANTE"));
                p.setVisitante(visitante);

                Jornada jornada = new Jornada();
                jornada.setIdJornada(resultado.getInt("ID_JORNADA"));
                p.setJornada(jornada);

                p.setFecha(resultado.getDate("FECHA"));
                p.setEstadio(resultado.getString("ESTADIO"));
                p.setEstado(resultado.getString("ESTADO"));
                p.setGolesLocal(resultado.getInt("GOLES_LOCAL"));
                p.setGolesVisitante(resultado.getInt("GOLES_VISITANTE"));
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
        return p;
    }

    /**
     * Este método hace una consulta recogiendo todos los partidos de los
     * equipos que sigue un usuario concreto
     *
     * @param idUsuario Identificador del usuario del que se quieren obtener los
     * partidos
     * @return Lista de partidos de los equipos seguidos por el usuario
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 09/05/2026 DD/MM/AAAA
     */
    public ArrayList<Partido> leerPartidosSeguidos(Integer idUsuario) throws ExcepcionNF {
        ArrayList<Partido> listaPartidos = new ArrayList<>();
        Partido p;
        String dql = "SELECT p.*, "
                + "el.nombre AS nombre_local, el.ciudad AS ciudad_local, el.entrenador AS entrenador_local, el.grupo AS grupo_local, "
                + "ev.nombre AS nombre_visitante, ev.ciudad AS ciudad_visitante, ev.entrenador AS entrenador_visitante, ev.grupo AS grupo_visitante "
                + "FROM partido p "
                + "JOIN equipo el ON p.id_local = el.id_equipo "
                + "JOIN equipo ev ON p.id_visitante = ev.id_equipo "
                + "JOIN usuario_equipos_seguidos ues ON (ues.id_equipo = p.id_local OR ues.id_equipo = p.id_visitante) "
                + "WHERE ues.id_usuario = " + idUsuario;
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            while (resultado.next()) {
                p = new Partido();
                p.setIdPartido(resultado.getInt("ID_PARTIDO"));

                Equipo local = new Equipo();
                local.setIdEquipo(resultado.getInt("ID_LOCAL"));
                local.setNombre(resultado.getString("NOMBRE_LOCAL"));
                local.setCiudad(resultado.getString("CIUDAD_LOCAL"));
                local.setEntrenador(resultado.getString("ENTRENADOR_LOCAL"));
                local.setGrupo(resultado.getString("GRUPO_LOCAL"));
                p.setLocal(local);

                Equipo visitante = new Equipo();
                visitante.setIdEquipo(resultado.getInt("ID_VISITANTE"));
                visitante.setNombre(resultado.getString("NOMBRE_VISITANTE"));
                visitante.setCiudad(resultado.getString("CIUDAD_VISITANTE"));
                visitante.setEntrenador(resultado.getString("ENTRENADOR_VISITANTE"));
                visitante.setGrupo(resultado.getString("GRUPO_VISITANTE"));
                p.setVisitante(visitante);

                Jornada jornada = new Jornada();
                jornada.setIdJornada(resultado.getInt("ID_JORNADA"));
                p.setJornada(jornada);

                p.setFecha(resultado.getDate("FECHA"));
                p.setEstadio(resultado.getString("ESTADIO"));
                p.setEstado(resultado.getString("ESTADO"));
                p.setGolesLocal(resultado.getInt("GOLES_LOCAL"));
                p.setGolesVisitante(resultado.getInt("GOLES_VISITANTE"));

                listaPartidos.add(p);
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
        return listaPartidos;
    }

    public Integer eliminarComentario(Integer idComentario) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            dml = "DELETE comentario WHERE id_comentario = " + idComentario;
            registrosAfectados = sentencia.executeUpdate(dml);

            sentencia.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 2292:
                    e.setMensajeErrorUsuario("No se puede eliminar este comentario ya que tiene registros asociados");
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
     * Este método modifica un registro existente en la tabla Comentario de la
     * base de datos
     *
     * @param idComentario Identificador del comentario que se desea modificar
     * @param comentario Objeto que contiene la nueva información del
     * comentario, incluyendo la noticia y el usuario asociados
     * @return Cantidad de registros afectados
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 10/05/2026 DD/MM/AAAA
     */
    public Integer modificarComentario(Integer idComentario, Comentario comentario) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "UPDATE comentario SET "
                + "id_noticia = ?, "
                + "id_usuario = ?, "
                + "contenido = ?, "
                + "fecha_creacion = ? "
                + "WHERE id_comentario = ?";
        try {
            conectarBD();
            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

            sentenciaPreparada.setObject(1, comentario.getNoticia().getIdNoticia(), java.sql.Types.INTEGER);
            sentenciaPreparada.setObject(2, comentario.getUsuario().getIdUsuario(), java.sql.Types.INTEGER);
            sentenciaPreparada.setString(3, comentario.getContenido());
            sentenciaPreparada.setObject(4, new java.sql.Date(comentario.getFechaCreacion().getTime()));
            sentenciaPreparada.setObject(5, idComentario, java.sql.Types.INTEGER);

            registrosAfectados = sentenciaPreparada.executeUpdate();

            sentenciaPreparada.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1407:
                    e.setMensajeErrorUsuario("Todos los campos obligatorios deben estar rellenos");
                    break;
                case 2291:
                    e.setMensajeErrorUsuario("La noticia o el usuario indicados no existen");
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
     * Este método inserta un registro en la tabla Comentario de la base de
     * datos
     *
     * @param comentario Objeto que contiene toda la información del comentario
     * a insertar, incluyendo la noticia y el usuario asociados
     * @return Cantidad de registros insertados
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 10/05/2026 DD/MM/AAAA
     */
    public Integer insertarComentario(Comentario comentario) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "INSERT INTO comentario (id_comentario, id_noticia, id_usuario, contenido, fecha_creacion) "
                + "VALUES (SEQ_COMENTARIO.nextval, ?, ?, ?, ?)";

        try {
            conectarBD();
            PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

            sentenciaPreparada.setObject(1, comentario.getNoticia().getIdNoticia(), java.sql.Types.INTEGER);
            sentenciaPreparada.setObject(2, comentario.getUsuario().getIdUsuario(), java.sql.Types.INTEGER);
            sentenciaPreparada.setString(3, comentario.getContenido());
            sentenciaPreparada.setObject(4, new java.sql.Date(comentario.getFechaCreacion().getTime()));

            registrosAfectados = sentenciaPreparada.executeUpdate();

            sentenciaPreparada.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();

            switch (ex.getErrorCode()) {
                case 1400:
                    e.setMensajeErrorUsuario("Todos los campos obligatorios deben estar rellenos");
                    break;
                case 2291:
                    e.setMensajeErrorUsuario("La noticia o el usuario indicados no existen");
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
     * Este método hace una consulta recogiendo todos los comentarios asociados
     * a una noticia concreta de la base de datos
     *
     * @param idNoticia Identificador de la noticia cuyos comentarios se desean
     * obtener
     * @return Lista de objetos Comentario con todos sus datos, o lista vacía si
     * no hay ninguno
     * @throws ExcepcionNF
     * @author Adam Janah
     * @version 1.0
     * @since 10/05/2026 DD/MM/AAAA
     */
    public ArrayList<Comentario> leerComentariosPorNoticia(Integer idNoticia) throws ExcepcionNF {
        ArrayList<Comentario> comentarios = new ArrayList<>();
        String dql = "SELECT c.*, "
                + "u.nombre AS nombre_usuario, u.email AS email_usuario, "
                + "u.rol AS rol_usuario, u.foto_perfil AS foto_perfil "
                + "FROM comentario c "
                + "JOIN usuario u ON c.id_usuario = u.id_usuario "
                + "WHERE c.id_noticia = " + idNoticia + " "
                + "ORDER BY c.fecha_creacion ASC";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            while (resultado.next()) {
                Comentario c = new Comentario();
                c.setIdComentario(resultado.getInt("ID_COMENTARIO"));

                Noticia noticia = new Noticia();
                noticia.setIdNoticia(idNoticia);
                c.setNoticia(noticia);

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultado.getInt("ID_USUARIO"));
                usuario.setNombre(resultado.getString("NOMBRE_USUARIO"));
                usuario.setEmail(resultado.getString("EMAIL_USUARIO"));
                usuario.setRol(resultado.getString("ROL_USUARIO"));
                usuario.setFotoPerfil(resultado.getString("FOTO_PERFIL"));
                c.setUsuario(usuario);

                c.setContenido(resultado.getString("CONTENIDO"));
                c.setFechaCreacion(resultado.getDate("FECHA_CREACION"));

                comentarios.add(c);
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
        return comentarios;
    }

    public boolean esSeguidor(Integer idUsuario, Integer idEquipo) throws ExcepcionNF {
        String dql = "SELECT COUNT(*) FROM usuario_equipos_seguidos "
                + "WHERE id_usuario = " + idUsuario + " AND id_equipo = " + idEquipo;
        try {
            conectarBD();
            Statement s = conexion.createStatement();
            ResultSet rs = s.executeQuery(dql);
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            s.close();
            conexion.close();
            return count > 0;
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dql);
            throw e;
        }
    }

    public int seguirEquipo(Integer idUsuario, Integer idEquipo) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "INSERT INTO usuario_equipos_seguidos VALUES (" + idUsuario + ", " + idEquipo + ")";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            registrosAfectados = sentencia.executeUpdate(dml);
            sentencia.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);
            throw e;
        }
        return registrosAfectados;
    }

    public int dejarSeguirEquipo(Integer idUsuario, Integer idEquipo) throws ExcepcionNF {
        int registrosAfectados = 0;
        String dml = "DELETE FROM usuario_equipos_seguidos "
                + "WHERE id_usuario = " + idUsuario + " AND id_equipo = " + idEquipo;
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            registrosAfectados = sentencia.executeUpdate(dml);
            sentencia.close();
            conexion.close();
        } catch (SQLException ex) {
            ExcepcionNF e = new ExcepcionNF();
            e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
            e.setCodigoErrorBD(ex.getErrorCode());
            e.setMensajeErrorBD(ex.getMessage());
            e.setSentenciaSQL(dml);
            throw e;
        }
        return registrosAfectados;
    }

    public Equipo leerEquipo(int idEquipo) throws ExcepcionNF {

        Equipo eq = null;
        String dql = "SELECT * FROM equipo WHERE id_equipo = " + idEquipo;
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();

            ResultSet resultado = sentencia.executeQuery(dql);
            if (resultado.next()) {
                eq = new Equipo();
                eq.setIdEquipo(resultado.getInt("ID_EQUIPO"));
                eq.setNombre(resultado.getString("NOMBRE"));
                eq.setCiudad(resultado.getString("CIUDAD"));
                eq.setEntrenador(resultado.getString("ENTRENADOR"));
                eq.setGrupo(resultado.getString("GRUPO"));
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
        return eq;
    }

    public ArrayList<EventoPartido> leerEventosPorPartido(Integer idPartido) throws ExcepcionNF {
        ArrayList<EventoPartido> eventos = new ArrayList<>();
        String dql = "SELECT e.id_evento, e.id_partido, e.id_jugador, "
                + "e.tipo_evento, e.minuto, "
                + "j.nombre AS nombre_jugador, j.apellido AS apellido_jugador, "
                + "j.dorsal AS dorsal_jugador, "
                + "eq.id_equipo AS id_equipo_jugador "
                + "FROM evento_partido e "
                + "JOIN jugador j ON e.id_jugador = j.id_jugador "
                + "JOIN equipo eq ON j.id_equipo = eq.id_equipo "
                + "WHERE e.id_partido = " + idPartido + " "
                + "ORDER BY e.minuto ASC";

        System.out.println("DEBUG SQL: " + dql);
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            while (resultado.next()) {
                EventoPartido ep = new EventoPartido();
                ep.setIdEvento(resultado.getInt("ID_EVENTO"));

                Partido partido = new Partido();
                partido.setIdPartido(idPartido);
                ep.setPartido(partido);

                Jugador jugador = new Jugador();
                jugador.setIdJugador(resultado.getInt("ID_JUGADOR"));
                jugador.setNombre(resultado.getString("NOMBRE_JUGADOR"));
                jugador.setApellido(resultado.getString("APELLIDO_JUGADOR"));
                jugador.setDorsal(resultado.getInt("DORSAL_JUGADOR"));

                Equipo equipo = new Equipo();
                equipo.setIdEquipo(resultado.getInt("ID_EQUIPO_JUGADOR"));
                jugador.setEquipo(equipo);

                ep.setJugador(jugador);
                ep.setTipoEvento(resultado.getString("TIPO_EVENTO"));
                ep.setMinuto(resultado.getInt("MINUTO"));

                eventos.add(ep);
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
        return eventos;
    }

    public ArrayList<Noticia> leerNoticiasPorEquipo(Integer idEquipo) throws ExcepcionNF {
        ArrayList<Noticia> noticias = new ArrayList<>();
        String dql = "SELECT n.*, e.nombre AS nombre_equipo "
                + "FROM noticia n "
                + "JOIN equipo e ON n.id_equipo = e.id_equipo "
                + "WHERE n.id_equipo = " + idEquipo + " "
                + "ORDER BY n.fecha_creacion DESC";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            while (resultado.next()) {
                Noticia n = new Noticia();
                n.setIdNoticia(resultado.getInt("ID_NOTICIA"));
                n.setTitulo(resultado.getString("TITULO"));
                n.setSubtitulo(resultado.getString("SUBTITULO"));
                n.setImagen(resultado.getString("IMAGEN"));
                n.setContenido(resultado.getString("CONTENIDO"));
                n.setFechaCreacion(resultado.getDate("FECHA_CREACION"));

                Equipo equipo = new Equipo();
                equipo.setIdEquipo(idEquipo);
                equipo.setNombre(resultado.getString("NOMBRE_EQUIPO"));
                n.setEquipo(equipo);

                noticias.add(n);
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
        return noticias;
    }

    public ArrayList<Partido> leerPartidosPorEquipo(Integer idEquipo) throws ExcepcionNF {
        ArrayList<Partido> partidos = new ArrayList<>();
        String dql = "SELECT p.*, "
                + "el.id_equipo AS id_local, el.nombre AS nombre_local, el.ciudad AS ciudad_local, "
                + "el.entrenador AS entrenador_local, el.grupo AS grupo_local, "
                + "ev.id_equipo AS id_visitante, ev.nombre AS nombre_visitante, ev.ciudad AS ciudad_visitante, "
                + "ev.entrenador AS entrenador_visitante, ev.grupo AS grupo_visitante "
                + "FROM partido p "
                + "JOIN equipo el ON p.id_local = el.id_equipo "
                + "JOIN equipo ev ON p.id_visitante = ev.id_equipo "
                + "WHERE p.id_local = " + idEquipo + " OR p.id_visitante = " + idEquipo + " "
                + "ORDER BY p.fecha DESC";
        try {
            conectarBD();
            Statement sentencia = conexion.createStatement();
            ResultSet resultado = sentencia.executeQuery(dql);

            while (resultado.next()) {
                Partido p = new Partido();
                p.setIdPartido(resultado.getInt("ID_PARTIDO"));
                p.setFecha(resultado.getDate("FECHA"));
                p.setEstadio(resultado.getString("ESTADIO"));
                p.setEstado(resultado.getString("ESTADO"));
                p.setGolesLocal(resultado.getInt("GOLES_LOCAL"));
                p.setGolesVisitante(resultado.getInt("GOLES_VISITANTE"));

                Equipo local = new Equipo();
                local.setIdEquipo(resultado.getInt("ID_LOCAL"));
                local.setNombre(resultado.getString("NOMBRE_LOCAL"));
                local.setCiudad(resultado.getString("CIUDAD_LOCAL"));
                local.setEntrenador(resultado.getString("ENTRENADOR_LOCAL"));
                local.setGrupo(resultado.getString("GRUPO_LOCAL"));
                p.setLocal(local);

                Equipo visitante = new Equipo();
                visitante.setIdEquipo(resultado.getInt("ID_VISITANTE"));
                visitante.setNombre(resultado.getString("NOMBRE_VISITANTE"));
                visitante.setCiudad(resultado.getString("CIUDAD_VISITANTE"));
                visitante.setEntrenador(resultado.getString("ENTRENADOR_VISITANTE"));
                visitante.setGrupo(resultado.getString("GRUPO_VISITANTE"));
                p.setVisitante(visitante);

                partidos.add(p);
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
        return partidos;
    }

}
