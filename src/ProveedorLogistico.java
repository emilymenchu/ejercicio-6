import java.util.UUID;

// Simula cotizaciones, seguimiento de paquetes y generación de guías de envío
public class ProveedorLogistico extends IntegracionAPI implements Documentable, Auditable {
    public ProveedorLogistico(String identificador, String nombre, String configuracion, double latenciaPromedio) {
        this(identificador, nombre, configuracion, latenciaPromedio, true, true);
    }

    public ProveedorLogistico(String identificador, String nombre, String configuracion, double latenciaPromedio, boolean activa, boolean conectada) {
        super(identificador, nombre, configuracion, latenciaPromedio, activa, conectada);
    }

    // Calcula el precio por peso, sin tomar en cuenta la distancia
    public double cotizarEnvio(String origen, String destino, double peso) {
        verificarDisponibilidad();
        validarTexto(origen, "Origen");
        validarTexto(destino, "Destino");
        validarPositivo(peso, "El peso");
        // Q 25 de base más Q 5 por kilogramo
        double cotizacion = 25.0 + 5.0 * peso;
        if (!Double.isFinite(cotizacion)) {
            throw new IllegalArgumentException("El peso es demasiado grande para cotizar.");
        }
        return cotizacion;
    }

    @Override
    public String consultarEstado(String referencia) {
        verificarDisponibilidad();
        return "Seguimiento simulado del envío " + validarTexto(referencia, "Identificador de envío")
                + ": en tránsito.";
    }

    // Crea una guía simulada con los datos del envío
    @Override
    public String generarDocumento(String datos) {
        verificarDisponibilidad();
        return "Guía de envío simulada GUI-" + UUID.randomUUID() + "\nProveedor: "
                + getNombre() + "\nDatos: " + validarTexto(datos, "Datos de envío");
    }

    @Override
    public String generarAuditoria(String accion, String resultado) {
        return describirAuditoria(accion, resultado);
    }
}
