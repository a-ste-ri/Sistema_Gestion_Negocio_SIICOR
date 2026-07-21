package services;

import database.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ProductoService {

    public void insertar(String nombre, double precio, int stock) {
    Connection conn = null;

    try {
        conn = Conexion.conectar();

        String sql = "INSERT INTO productos(nombre, precio, stock) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, nombre);
        ps.setDouble(2, precio);
        ps.setInt(3, stock);

        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
    } finally {
        try {
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    public void actualizar(int id, String nombre, double precio, int stock) {

    Connection conn = null;
    PreparedStatement ps = null;

    try {
        conn = Conexion.conectar();

        String sql = "UPDATE productos SET nombre=?, precio=?, stock=? WHERE id=?";
        ps = conn.prepareStatement(sql);

        ps.setString(1, nombre);
        ps.setDouble(2, precio);
        ps.setInt(3, stock);
        ps.setInt(4, id);

        ps.executeUpdate();

        System.out.println("Producto actualizado");

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e.getMessage());
    } finally {
        try {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    public void eliminar(int id) {

    Connection conn = null;
    PreparedStatement ps = null;

    try {
        conn = Conexion.conectar();

        String sql = "DELETE FROM productos WHERE id=?";
        ps = conn.prepareStatement(sql);

        ps.setInt(1, id);
        ps.executeUpdate();

        System.out.println("Producto eliminado");

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try{
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    }
    
    
