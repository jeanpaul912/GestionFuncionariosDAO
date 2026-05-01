package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.conexion.Conexion;
import com.gestion.funcionarios.modelo.TipoDocumento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoDAO {

    public List<TipoDocumento> listar() throws SQLException {
        List<TipoDocumento> lista = new ArrayList<>();
        String sql = "SELECT id, codigo, descripcion FROM tipo_documento ORDER BY descripcion";

        try (Statement st = Conexion.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                TipoDocumento td = new TipoDocumento(
                        rs.getInt("id"),
                        rs.getString("codigo"),
                        rs.getString("descripcion")
                );
                lista.add(td);
            }
        }
        return lista;
    }
}