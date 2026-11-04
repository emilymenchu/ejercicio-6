import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

// Conecta la ventana con el catálogo y el motor
public class Controlador implements ActionListener {
    private final CatalogoIntegraciones catalogo;
    private final MotorCheckout motor;
    private final VistaGrafica vista;
    private boolean iniciado;

    public Controlador(CatalogoIntegraciones catalogo, MotorCheckout motor, VistaGrafica vista) {
        this.catalogo = Objects.requireNonNull(catalogo);
        this.motor = Objects.requireNonNull(motor);
        this.vista = Objects.requireNonNull(vista);
    }

    // Prepara los botones una sola vez y abre la ventana
    public void iniciar() {
        if (!iniciado) {
            registrarEventos();
            iniciado = true;
        }
        listarIntegraciones();
        vista.mostrarVentana();
    }

    // Este método sirve para conectar cada botón con su acción
    private void registrarEventos() {
        vista.alBuscar(this);
        vista.alListar(this);
        vista.alOrdenar(this);
        vista.alEjecutar(this);
    }

    // Cada botón envía un nombre para saber qué acción realizar
    @Override
    public void actionPerformed(ActionEvent evento) {
        switch (evento.getActionCommand()) {
            case "buscar":
                buscarIntegracion();
                break;
            case "listar":
                listarIntegraciones();
                break;
            case "ordenar":
                ordenarIntegraciones();
                break;
            case "ejecutar":
                ejecutarOperacion();
                break;
            default:
                break;
        }
    }

    // Muestra las integraciones activas, aunque estén desconectadas
    public void listarIntegraciones() {
        vista.limpiarTabla();
        for (IntegracionAPI integracion : catalogo.consultarIntegraciones()) {
            if (integracion.isActiva()) { vista.agregarFila(integracion); }
        }
    }

    // Busca el servicio y muestra sus datos, aunque esté inactivo
    public void buscarIntegracion() {
        IntegracionAPI integracion = catalogo.buscarPorIdentificador(vista.obtenerIdentificadorBusqueda());
        if (integracion == null) {
            vista.mostrarMensaje("No existe una integración con ese identificador.");
            return;
        }
        vista.limpiarTabla();
        vista.agregarFila(integracion);
        vista.mostrarDetalles(integracion);
    }

    public void ordenarIntegraciones() {
        catalogo.ordenarPorLatencia();
        listarIntegraciones();
    }

    // Usa la fila seleccionada o el identificador escrito si no hay selección
    public void ejecutarOperacion() {
        String identificador = vista.obtenerIdentificadorSeleccionado();
        if (identificador == null) {
            identificador = vista.obtenerIdentificadorBusqueda();
        }
        String operacion = vista.obtenerOperacionSeleccionada();
        String mensaje = motor.validarOperacion(identificador, operacion);
        if (!mensaje.isEmpty()) {
            vista.mostrarResultado(mensaje);
            return;
        }

        try {
            String resultado;
            switch (operacion) {
                case "Cobrar":
                    resultado = motor.procesarCobro(identificador, leerNumero("Monto"));
                    break;
                case "Reintegrar":
                    resultado = motor.procesarReintegro(identificador, vista.leerDatoOperacion("Referencia"), leerNumero("Monto"));
                    break;
                case "Generar documento":
                    resultado = motor.procesarDocumento(identificador, vista.leerDatoOperacion("Datos"));
                    break;
                case "Consultar estado":
                    resultado = motor.consultarEstado(identificador, vista.leerDatoOperacion("Referencia"));
                    break;
                case "Cotizar envío":
                    resultado = motor.procesarCotizacion(identificador, vista.leerDatoOperacion("Origen"), vista.leerDatoOperacion("Destino"), leerNumero("Peso"));
                    break;
                case "Consultar auditoría":
                    resultado = motor.consultarAuditoria(identificador, vista.leerDatoOperacion("Acción"), vista.leerDatoOperacion("Resultado"));
                    break;
                default:
                    resultado = "Operación no aplicable.";
            }
            vista.mostrarResultado(resultado);
        } catch (IllegalArgumentException ex) {
            vista.mostrarResultado("No se pudo completar la operación: " + ex.getMessage());
        }
    }

    // Convierte el texto del campo antes de enviarlo al motor
    private double leerNumero(String nombreCampo) {
        String texto = vista.leerDatoOperacion(nombreCampo);
        if (texto.isEmpty()) {
            throw new IllegalArgumentException(nombreCampo + " no puede estar vacío.");
        }
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(nombreCampo + " debe ser un número válido (use punto decimal).");
        }
    }
}
