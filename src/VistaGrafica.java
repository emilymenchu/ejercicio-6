import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

// Muestra las integraciones y permite ingresar datos para ejecutar operaciones
public final class VistaGrafica implements ActionListener, ListSelectionListener {
    private JFrame ventana;
    private JTable tablaIntegraciones;
    private JTextField campoIdentificador;
    private JButton botonBuscar;
    private JButton botonListar;
    private JButton botonOrdenar;
    private JComboBox<String> selectorOperacion;
    private JPanel panelDatosOperacion;
    private JButton botonEjecutar;
    private JTextArea areaDetalles;
    private JTextArea areaResultados;
    // Cada nombre corresponde al cuadro de texto en la misma posición
    private final ArrayList<String> nombresCampos = new ArrayList<>();
    private final ArrayList<JTextField> camposOperacion = new ArrayList<>();

    public VistaGrafica() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        ventana = new JFrame("Catálogo de integraciones API - Simulación");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout(10, 10));
        campoIdentificador = new JTextField(12);
        botonBuscar = new JButton("Buscar / configuración");
        botonListar = new JButton("Listar activas");
        botonOrdenar = new JButton("Ordenar por latencia");
        JPanel busqueda = new JPanel();
        busqueda.add(new JLabel("Identificador:"));
        busqueda.add(campoIdentificador);
        busqueda.add(botonBuscar);
        busqueda.add(botonListar);
        busqueda.add(botonOrdenar);
        ventana.add(busqueda, BorderLayout.NORTH);

        ModeloTablaIntegraciones modelo = new ModeloTablaIntegraciones();
        tablaIntegraciones = new JTable(modelo);
        tablaIntegraciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaIntegraciones.setAutoCreateRowSorter(true);
        areaDetalles = new JTextArea(4, 35);
        areaResultados = new JTextArea(8, 60);
        areaDetalles.setEditable(false);
        areaResultados.setEditable(false);
        areaDetalles.setLineWrap(true);
        areaDetalles.setWrapStyleWord(true);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        tablaIntegraciones.getSelectionModel().addListSelectionListener(this);

        JPanel operaciones = new JPanel(new BorderLayout(5, 5));
        selectorOperacion = new JComboBox<>(new String[]{"Cobrar", "Reintegrar", "Generar documento",
                "Consultar estado", "Cotizar envío", "Consultar auditoría"});
        panelDatosOperacion = new JPanel(new GridLayout(0, 2, 5, 5));
        botonEjecutar = new JButton("Ejecutar operación");
        selectorOperacion.addActionListener(this);
        operaciones.add(selectorOperacion, BorderLayout.NORTH);
        operaciones.add(panelDatosOperacion, BorderLayout.CENTER);
        operaciones.add(botonEjecutar, BorderLayout.SOUTH);
        actualizarFormulario();

        JPanel inferior = new JPanel(new GridLayout(1, 2, 10, 10));
        JScrollPane detalles = new JScrollPane(areaDetalles);
        detalles.setBorder(BorderFactory.createTitledBorder("Configuración y detalles"));
        inferior.add(detalles);
        inferior.add(operaciones);
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.add(new JScrollPane(tablaIntegraciones), BorderLayout.CENTER);
        centro.add(inferior, BorderLayout.SOUTH);
        ventana.add(centro, BorderLayout.CENTER);
        JScrollPane resultados = new JScrollPane(areaResultados);
        resultados.setBorder(BorderFactory.createTitledBorder("Resultado y auditoría"));
        ventana.add(resultados, BorderLayout.SOUTH);
        ventana.setMinimumSize(new Dimension(900, 650));
        ventana.pack();
        ventana.setLocationRelativeTo(null);
    }

    // Cambia el formulario cuando el usuario elige otra operación
    @Override
    public void actionPerformed(ActionEvent evento) {
        if (evento.getSource() == selectorOperacion) {
            actualizarFormulario();
        }
    }

    // Copia el identificador de la fila elegida al campo de búsqueda
    @Override
    public void valueChanged(ListSelectionEvent evento) {
        if (!evento.getValueIsAdjusting() && obtenerIdentificadorSeleccionado() != null) {
            campoIdentificador.setText(obtenerIdentificadorSeleccionado());
            areaDetalles.setText("Seleccione Buscar / configuración para consultar los detalles.");
        }
    }

    public void mostrarVentana() {
        ventana.setVisible(true);
    }

    public String obtenerIdentificadorBusqueda() {
        return campoIdentificador.getText().trim();
    }

    // Devuelve el identificador de la fila seleccionada o null
    public String obtenerIdentificadorSeleccionado() {
        int fila = tablaIntegraciones.getSelectedRow();
        return fila < 0 ? null : tablaIntegraciones.getValueAt(fila, 0).toString();
    }

    public String obtenerOperacionSeleccionada() {
        return (String) selectorOperacion.getSelectedItem();
    }

    // Este método sirve para mostrar los campos de la operación elegida y borrar los anteriores
    private void actualizarFormulario() {
        panelDatosOperacion.removeAll();
        nombresCampos.clear();
        camposOperacion.clear();
        String[] nombres;
        switch (obtenerOperacionSeleccionada()) {
            case "Cobrar": nombres = new String[]{"Monto"}; break;
            case "Reintegrar": nombres = new String[]{"Referencia", "Monto"}; break;
            case "Generar documento": nombres = new String[]{"Datos"}; break;
            case "Consultar estado": nombres = new String[]{"Referencia"}; break;
            case "Cotizar envío": nombres = new String[]{"Origen", "Destino", "Peso"}; break;
            default: nombres = new String[]{"Acción", "Resultado"};
        }
        for (String nombre : nombres) {
            JTextField campo = new JTextField(20);
            nombresCampos.add(nombre);
            camposOperacion.add(campo);
            panelDatosOperacion.add(new JLabel(nombre + ":"));
            panelDatosOperacion.add(campo);
        }
        panelDatosOperacion.revalidate();
        panelDatosOperacion.repaint();
    }

    // Lee el campo por su nombre y devuelve un texto vacío si no existe
    public String leerDatoOperacion(String nombreCampo) {
        int posicion = nombresCampos.indexOf(nombreCampo);
        return posicion < 0 ? "" : camposOperacion.get(posicion).getText().trim();
    }

    public void limpiarTabla() {
        // Quita el orden elegido en las columnas para mostrar el orden del catálogo
        tablaIntegraciones.getRowSorter().setSortKeys(null);
        ((DefaultTableModel) tablaIntegraciones.getModel()).setRowCount(0);
    }

    public void agregarFila(IntegracionAPI integracion) {
        ((DefaultTableModel) tablaIntegraciones.getModel()).addRow(new Object[]{
                integracion.getIdentificador(), integracion.getNombre(), integracion.isActiva(),
                integracion.isConectada(), integracion.getLatenciaPromedio()});
    }

    public void mostrarDetalles(IntegracionAPI integracion) {
        areaDetalles.setText(integracion + "\nConfiguración: " + integracion.getConfiguracion());
        areaDetalles.setCaretPosition(0);
    }

    public void mostrarResultado(String resultado) {
        areaResultados.setText(resultado);
        areaResultados.setCaretPosition(0);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje);
    }
    // Aquí el controlador conecta las acciones de los botones
    public void alBuscar(ActionListener accion) {
        botonBuscar.setActionCommand("buscar");
        botonBuscar.addActionListener(accion);
    }

    public void alListar(ActionListener accion) {
        botonListar.setActionCommand("listar");
        botonListar.addActionListener(accion);
    }

    public void alOrdenar(ActionListener accion) {
        botonOrdenar.setActionCommand("ordenar");
        botonOrdenar.addActionListener(accion);
    }

    public void alEjecutar(ActionListener accion) {
        botonEjecutar.setActionCommand("ejecutar");
        botonEjecutar.addActionListener(accion);
    }
}
