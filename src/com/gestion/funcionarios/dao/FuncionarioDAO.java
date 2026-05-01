package com.gestion.funcionarios.dao;

import com.gestion.funcionarios.conexion.Conexion;
import com.gestion.funcionarios.modelo.EstadoCivil;
import com.gestion.funcionarios.modelo.Funcionario;
import com.gestion.funcionarios.modelo.TipoDocumento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {


    // LISTAR todos los funcionarios

    public List<Funcionario> listar() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT f.id, f.nombres, f.apellidos, " +
                     "f.numero_documento, f.fecha_nacimiento, " +
                     "f.email, f.telefono, f.cargo, f.fecha_ingreso, f.activo, " +
                     "td.id AS td_id, td.codigo, td.descripcion AS td_desc, " +
                     "ec.id AS ec_id, ec.descripcion AS ec_desc " +
                     "FROM funcionarios f " +
                     "JOIN tipo_documento td ON f.id_tipo_documento = td.id " +
                     "JOIN estado_civil ec ON f.id_estado_civil = ec.id " +
                     "ORDER BY f.apellidos, f.nombres";

        try (Statement st = Conexion.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearFuncionario(rs));
            }
        }
        return lista;
    }

    // BUSCAR por ID
    
    public Funcionario buscarPorId(int id) throws SQLException {
        String sql = "SELECT f.id, f.nombres, f.apellidos, " +
                     "f.numero_documento, f.fecha_nacimiento, " +
                     "f.email, f.telefono, f.cargo, f.fecha_ingreso, f.activo, " +
                     "td.id AS td_id, td.codigo, td.descripcion AS td_desc, " +
                     "ec.id AS ec_id, ec.descripcion AS ec_desc " +
                     "FROM funcionarios f " +
                     "JOIN tipo_documento td ON f.id_tipo_documento = td.id " +
                     "JOIN estado_civil ec ON f.id_estado_civil = ec.id " +
                     "WHERE f.id = ?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearFuncionario(rs);
                }
            }
        }
        return null;
    }

    // CREAR funcionario

    public boolean crear(Funcionario f) throws SQLException {
        String sql = "INSERT INTO funcionarios " +
                     "(nombres, apellidos, id_tipo_documento, numero_documento, " +
                     "fecha_nacimiento, id_estado_civil, email, telefono, cargo, fecha_ingreso, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, f.getNombres());
            ps.setString(2, f.getApellidos());
            ps.setInt   (3, f.getTipoDocumento().getId());
            ps.setString(4, f.getNumeroDocumento());
            ps.setDate  (5, Date.valueOf(f.getFechaNacimiento()));
            ps.setInt   (6, f.getEstadoCivil().getId());
            ps.setString(7, f.getEmail());
            ps.setString(8, f.getTelefono());
            ps.setString(9, f.getCargo());
            ps.setDate  (10, Date.valueOf(f.getFechaIngreso()));
            ps.setInt   (11, f.isActivo() ? 1 : 0);

            return ps.executeUpdate() > 0;
        }
    }


    // EDITAR funcionario

    public boolean editar(Funcionario f) throws SQLException {
        String sql = "UPDATE funcionarios SET " +
                     "nombres = ?, apellidos = ?, id_tipo_documento = ?, " +
                     "numero_documento = ?, fecha_nacimiento = ?, id_estado_civil = ?, " +
                     "email = ?, telefono = ?, cargo = ?, fecha_ingreso = ?, activo = ? " +
                     "WHERE id = ?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1,  f.getNombres());
            ps.setString(2,  f.getApellidos());
            ps.setInt   (3,  f.getTipoDocumento().getId());
            ps.setString(4,  f.getNumeroDocumento());
            ps.setDate  (5,  Date.valueOf(f.getFechaNacimiento()));
            ps.setInt   (6,  f.getEstadoCivil().getId());
            ps.setString(7,  f.getEmail());
            ps.setString(8,  f.getTelefono());
            ps.setString(9,  f.getCargo());
            ps.setDate  (10, Date.valueOf(f.getFechaIngreso()));
            ps.setInt   (11, f.isActivo() ? 1 : 0);
            ps.setInt   (12, f.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ELIMINAR funcionario
    
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM funcionarios WHERE id = ?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Mapear ResultSet a objeto Funcionario

    private Funcionario mapearFuncionario(ResultSet rs) throws SQLException {
        TipoDocumento td = new TipoDocumento(
                rs.getInt("td_id"),
                rs.getString("codigo"),
                rs.getString("td_desc")
        );
        EstadoCivil ec = new EstadoCivil(
                rs.getInt("ec_id"),
                rs.getString("ec_desc")
        );
        Funcionario f = new Funcionario();
        f.setId              (rs.getInt("id"));
        f.setNombres         (rs.getString("nombres"));
        f.setApellidos       (rs.getString("apellidos"));
        f.setTipoDocumento   (td);
        f.setNumeroDocumento (rs.getString("numero_documento"));
        f.setFechaNacimiento (rs.getDate("fecha_nacimiento").toLocalDate());
        f.setEstadoCivil     (ec);
        f.setEmail           (rs.getString("email"));
        f.setTelefono        (rs.getString("telefono"));
        f.setCargo           (rs.getString("cargo"));
        f.setFechaIngreso    (rs.getDate("fecha_ingreso").toLocalDate());
        f.setActivo          (rs.getInt("activo") == 1);
        return f;
    }
}
