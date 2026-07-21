package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/negocio";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() {
        try {
            // Conectamos primero sin especificar la BD para crearla si no existe
            Connection tempConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
            tempConn.createStatement().execute("CREATE DATABASE IF NOT EXISTS negocio");
            tempConn.close();

            // Ahora sí conectamos a la BD negocio
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (Exception e) {
            System.out.println("Error de conexión");
            e.printStackTrace();
            return null;
        }
    }
}