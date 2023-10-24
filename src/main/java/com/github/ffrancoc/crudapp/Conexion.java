/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.ffrancoc.crudapp;

import com.github.ffrancoc.crudapp.models.Producto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author niruc
 */
public class Conexion {
    
    private static Connection conn = null;
    
    private static Connection getConnection() {
        
        if (conn == null) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/crud", "root", "");
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
            }
        }
        return conn;
    }
    
    public static ObservableList<Producto> getProductos() {
        ObservableList<Producto> productos = FXCollections.observableArrayList();
        
        if (conn == null) {
            conn = getConnection();
        }
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM productos;");
            
            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDouble(4)));
            }
        } catch (SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        
        return productos;
    }
    
    public static boolean postProducto(Producto p) {
        if (conn == null) {
            conn = getConnection();
        }
        
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO productos(nombre, cantidad, precio) VALUES(?,?,?);");
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getCantidad());
            ps.setDouble(3, p.getPrecio());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        
        return false;
    }
    
    public static boolean patchProducto(Producto p) {
        if (conn == null) {
            conn = getConnection();
        }
        
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE productos SET nombre=?, cantidad=?, precio=? WHERE id=?;");
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getCantidad());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getId());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        
        return false;
    }
    
    public static boolean deleteProducto(int id) {
        if (conn == null) {
            conn = getConnection();
        }
        
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM productos WHERE id=?;");
            ps.setInt(1, id);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        
        return false;
    }
}
