package com.gestion.funcionarios.vista;

import com.gestion.funcionarios.dao.EstadoCivilDAO;
import com.gestion.funcionarios.dao.FuncionarioDAO;
import com.gestion.funcionarios.dao.TipoDocumentoDAO;
import com.gestion.funcionarios.modelo.EstadoCivil;
import com.gestion.funcionarios.modelo.Funcionario;
import com.gestion.funcionarios.modelo.TipoDocumento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    // DAO
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final TipoDocumentoDAO tipoDocumentoDAO = new TipoDocumentoDAO();
    private final EstadoCivilDAO estadoCivilDAO = new EstadoCivilDAO();

    // Formato de fecha
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Tabla
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    // Campos del formulario
    private JTextField txtNombres, txtApellidos, txtDocumento;
    private JTextField txtFechaNac, txtEmail, txtTelefono, txtCargo, txtFechaIngreso;
    private JComboBox<TipoDocumento> cmbTipoDoc;
    private JComboBox<EstadoCivil> cmbEstadoCivil;
    private JCheckBox chkActivo;

    // Botones
    private JButton btnNuevo, btnGuardar, btnEditar, btnEliminar, btnLimpiar;

    // ID del funcionario seleccionado
    private int idSeleccionado = -1;

    public VentanaPrincipal() {
        initComponents();
        cargarComboBoxes();
        cargarTabla();
    }

    private void initComponents() {
        setTitle("Gestión de Funcionarios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Funcionario"));
        panelForm.setPreferredSize(new Dimension(420, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombres      = new JTextField(18);
        txtApellidos    = new JTextField(18);
        txtDocumento    = new JTextField(18);
        txtFechaNac     = new JTextField(18);
        txtEmail        = new JTextField(18);
        txtTelefono     = new JTextField(18);
        txtCargo        = new JTextField(18);
        txtFechaIngreso = new JTextField(18);
        cmbTipoDoc      = new JComboBox<>();
        cmbEstadoCivil  = new JComboBox<>();
        chkActivo       = new JCheckBox("Activo", true);

        Object[][] campos = {
            {"Nombres:",        txtNombres},
            {"Apellidos:",      txtApellidos},
            {"Tipo Documento:", cmbTipoDoc},
            {"Nº Documento:",   txtDocumento},
            {"Fecha Nac (yyyy-MM-dd):", txtFechaNac},
            {"Estado Civil:",   cmbEstadoCivil},
            {"Email:",          txtEmail},
            {"Teléfono:",       txtTelefono},
            {"Cargo:",          txtCargo},
            {"Fecha Ingreso (yyyy-MM-dd):", txtFechaIngreso},
            {"",                chkActivo}
        };

        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            panelForm.add(new JLabel((String) campos[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            panelForm.add((Component) campos[i][1], gbc);
        }

        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        btnNuevo    = new JButton("Nuevo");
        btnGuardar  = new JButton("Guardar");
        btnEditar   = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar  = new JButton("Limpiar");

        btnNuevo   .setBackground(new Color(52, 152, 219));  btnNuevo   .setForeground(Color.WHITE);
        btnGuardar .setBackground(new Color(39, 174, 96));   btnGuardar .setForeground(Color.WHITE);
        btnEditar  .setBackground(new Color(243, 156, 18));  btnEditar  .setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(231, 76, 60));   btnEliminar.setForeground(Color.WHITE);
        btnLimpiar .setBackground(new Color(149, 165, 166)); btnLimpiar .setForeground(Color.WHITE);

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(panelForm, BorderLayout.CENTER);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        // Tabla
        String[] columnas = {"ID", "Nombres", "Apellidos", "Tipo Doc", "Documento",
                             "Cargo", "Fecha Ingreso", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Lista de Funcionarios"));

        // Layout principal
        add(panelIzquierdo, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        // Eventos
        btnNuevo   .addActionListener(e -> limpiarFormulario());
        btnGuardar .addActionListener(e -> guardarFuncionario());
        btnEditar  .addActionListener(e -> editarFuncionario());
        btnEliminar.addActionListener(e -> eliminarFuncionario());
        btnLimpiar .addActionListener(e -> limpiarFormulario());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarEnFormulario();
        });
    }

    private void cargarComboBoxes() {
        try {
            for (TipoDocumento td : tipoDocumentoDAO.listar()) cmbTipoDoc.addItem(td);
            for (EstadoCivil ec : estadoCivilDAO.listar())     cmbEstadoCivil.addItem(ec);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos auxiliares:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Funcionario> lista = funcionarioDAO.listar();
            for (Funcionario f : lista) {
                modeloTabla.addRow(new Object[]{
                    f.getId(),
                    f.getNombres(),
                    f.getApellidos(),
                    f.getTipoDocumento().getCodigo(),
                    f.getNumeroDocumento(),
                    f.getCargo(),
                    f.getFechaIngreso().toString(),
                    f.isActivo() ? "Sí" : "No"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar funcionarios:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        try {
            Funcionario f = funcionarioDAO.buscarPorId(idSeleccionado);
            if (f == null) return;
            txtNombres     .setText(f.getNombres());
            txtApellidos   .setText(f.getApellidos());
            txtDocumento   .setText(f.getNumeroDocumento());
            txtFechaNac    .setText(f.getFechaNacimiento().toString());
            txtEmail       .setText(f.getEmail());
            txtTelefono    .setText(f.getTelefono());
            txtCargo       .setText(f.getCargo());
            txtFechaIngreso.setText(f.getFechaIngreso().toString());
            chkActivo      .setSelected(f.isActivo());
            seleccionarCombo(cmbTipoDoc,     f.getTipoDocumento().getId());
            seleccionarCombo(cmbEstadoCivil, f.getEstadoCivil().getId());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar funcionario:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarFuncionario() {
        try {
            Funcionario f = obtenerDatosFormulario();
            if (f == null) return;
            funcionarioDAO.crear(f);
            JOptionPane.showMessageDialog(this, "Funcionario guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarFuncionario() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un funcionario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Funcionario f = obtenerDatosFormulario();
            if (f == null) return;
            f.setId(idSeleccionado);
            funcionarioDAO.editar(f);
            JOptionPane.showMessageDialog(this, "Funcionario actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al editar:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarFuncionario() {
        if (idSeleccionado < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un funcionario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de eliminar este funcionario?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            funcionarioDAO.eliminar(idSeleccionado);
            JOptionPane.showMessageDialog(this, "Funcionario eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error al eliminar:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Funcionario obtenerDatosFormulario() {
        String nombres   = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String documento = txtDocumento.getText().trim();
        String fechaNacStr = txtFechaNac.getText().trim();
        String fechaIngStr = txtFechaIngreso.getText().trim();

        if (nombres.isEmpty() || apellidos.isEmpty() || documento.isEmpty()
                || fechaNacStr.isEmpty() || fechaIngStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Los campos Nombres, Apellidos, Documento y Fechas son obligatorios.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        LocalDate fechaNac, fechaIng;
        try {
            fechaNac = LocalDate.parse(fechaNacStr, FORMATO);
            fechaIng = LocalDate.parse(fechaIngStr, FORMATO);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Formato de fecha incorrecto. Use yyyy-MM-dd (ej: 1990-05-20)",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Funcionario f = new Funcionario();
        f.setNombres        (nombres);
        f.setApellidos      (apellidos);
        f.setTipoDocumento  ((TipoDocumento) cmbTipoDoc.getSelectedItem());
        f.setNumeroDocumento(documento);
        f.setFechaNacimiento(fechaNac);
        f.setEstadoCivil    ((EstadoCivil) cmbEstadoCivil.getSelectedItem());
        f.setEmail          (txtEmail.getText().trim());
        f.setTelefono       (txtTelefono.getText().trim());
        f.setCargo          (txtCargo.getText().trim());
        f.setFechaIngreso   (fechaIng);
        f.setActivo         (chkActivo.isSelected());
        return f;
    }

    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombres.setText("");      txtApellidos.setText("");
        txtDocumento.setText("");    txtFechaNac.setText("");
        txtEmail.setText("");        txtTelefono.setText("");
        txtCargo.setText("");        txtFechaIngreso.setText("");
        chkActivo.setSelected(true);
        if (cmbTipoDoc.getItemCount()     > 0) cmbTipoDoc.setSelectedIndex(0);
        if (cmbEstadoCivil.getItemCount() > 0) cmbEstadoCivil.setSelectedIndex(0);
        tabla.clearSelection();
    }

    private void seleccionarCombo(JComboBox<?> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof TipoDocumento && ((TipoDocumento) item).getId() == id) {
                combo.setSelectedIndex(i); return;
            }
            if (item instanceof EstadoCivil && ((EstadoCivil) item).getId() == id) {
                combo.setSelectedIndex(i); return;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new VentanaPrincipal().setVisible(true);
        });
    }
}