import javax.swing.table.DefaultTableModel;

// Define las columnas y evita que se puedan editar los datos de la tabla
public class ModeloTablaIntegraciones extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    public ModeloTablaIntegraciones() {
        super(new String[]{"Identificador", "Nombre", "Activa", "Conectada", "Latencia (ms)"}, 0);
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    // Indica el tipo de dato para mostrar casillas y ordenar números correctamente
    @Override
    public Class<?> getColumnClass(int columna) {
        if (columna == 4) {
            return Double.class;
        }
        if (columna == 2 || columna == 3) {
            return Boolean.class;
        }
        return String.class;
    }
}
