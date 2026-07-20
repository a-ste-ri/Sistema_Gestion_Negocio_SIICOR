package services;

import database.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class FallaService {

    public static void registrar(String descripcion) {

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexion.conectar();

            String sql = "INSERT INTO fallas(fecha, estado) VALUES (NOW(), ?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, descripcion);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}