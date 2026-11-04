import java.util.Locale;
import java.util.UUID;

// Simula cobros y reintegros según el medio de pago
public class PasarelaPago extends IntegracionAPI implements Transaccional, Auditable {
    private final String medioPago;

    public PasarelaPago(String identificador, String nombre, String configuracion, double latenciaPromedio, String medioPago) {
        this(identificador, nombre, configuracion, latenciaPromedio, medioPago, true, true);
    }

    public PasarelaPago(String identificador, String nombre, String configuracion, double latenciaPromedio, String medioPago, boolean activa, boolean conectada) {
        super(identificador, nombre, configuracion, latenciaPromedio, activa, conectada);
        this.medioPago = validarTexto(medioPago, "Medio de pago");
    }

    public String getMedioPago() {
        return medioPago;
    }

    // Simula el cobro y genera su referencia
    @Override
    public String cobrar(double monto) {
        verificarDisponibilidad();
        validarPositivo(monto, "El monto");
        return String.format(Locale.ROOT, "Cobro simulado aprobado: COB-%s | Q %.2f | %s",
                UUID.randomUUID(), monto, medioPago);
    }

    // Revisa la referencia, sin consultar un historial de cobros
    @Override
    public String reintegrar(String referenciaCobro, double monto) {
        verificarDisponibilidad();
        String referencia = validarTexto(referenciaCobro, "Referencia del cobro");
        validarPositivo(monto, "El monto");
        return String.format(Locale.ROOT, "Reintegro simulado aprobado: REI-%s | cobro: %s | Q %.2f",
                UUID.randomUUID(), referencia, monto);
    }

    @Override
    public String consultarEstado(String referencia) {
        verificarDisponibilidad();
        return "Estado simulado del pago " + validarTexto(referencia, "Referencia") + ": aprobado.";
    }

    @Override
    public String generarAuditoria(String accion, String resultado) {
        return describirAuditoria(accion, resultado);
    }

    @Override
    public String toString() {
        return super.toString() + " | medio de pago: " + medioPago;
    }
}
