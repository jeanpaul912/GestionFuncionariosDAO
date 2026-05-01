package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.conexion.Conexion;
import com.gestion.funcionarios.modelo.EstadoCivil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoCivilDAO {

    public List<EstadoCivil> listar() throws SQLException {
        List<EstadoCivil> lista = new ArrayList<>();
        String sql = "SELECT id, descripcion FROM estado_civil ORDER BY descripcion";

        try (Statement st = Conexion.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                EstadoCivil ec = new EstadoCivil(
                        rs.getInt("id"),
                        rs.getString("descripcion")
                );
                lista.add(ec);
            }
        }
        return lista;
    }
}