package database;

import java.sql.Connection;
import java.sql.Statement;

public class CrearTablas {

    public static void crear() {
        try {
            Connection conn = Conexion.conectar();

            if (conn == null) {
                System.out.println("No se pudo conectar a la BD");
                return;
            }

            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS productos ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "nombre TEXT, "
                    + "precio REAL, "
                    + "stock INTEGER"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS ventas ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "fecha TEXT, "
                    + "total REAL"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS detalle_venta ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "id_venta INTEGER, "
                    + "id_producto INTEGER, "
                    + "cantidad INTEGER"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS fallas ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "descripcion TEXT, "
                    + "fecha TEXT, "
                    + "estado TEXT"
                    + ");");

            System.out.println("Tablas creadas correctamente");

        } catch (Exception e) {
            System.out.println("Error al crear tablas");
            e.printStackTrace();
        }
    }
}
