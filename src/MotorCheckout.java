import java.util.Locale;
import java.util.Objects;

// Coordina las operaciones que permite cada servicio
public class MotorCheckout {
    private final CatalogoIntegraciones catalogo;

    public MotorCheckout(CatalogoIntegraciones catalogo) {
        this.catalogo = Objects.requireNonNull(catalogo, "Se necesita un catálogo.");
    }

    // Solicita el cobro si el servicio implementa Transaccional
    public String procesarCobro(String identificador, double monto) {
        String mensaje = validarOperacion(identificador, "Cobrar");
        if (!mensaje.isEmpty()) {
            return mensaje;
        }
        IntegracionAPI integracion = catalogo.buscarPorIdentificador(identificador);
        try {
            String resultado = ejecutarCobro((Transaccional) integracion, monto);
            return agregarAuditoria(integracion, "Cobro", resultado);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return "No se pudo completar la operación: " + ex.getMessage();
        }
    }

    // Solicita el reintegro si el servicio lo permite
    public String procesarReintegro(String identificador, String referenciaCobro, double monto) {
        String mensaje = validarOperacion(identificador, "Reintegrar");
        if (!mensaje.isEmpty()) {
            return mensaje;
        }
        IntegracionAPI integracion = catalogo.buscarPorIdentificador(identificador);
        try {
            String resultado = ejecutarReintegro((Transaccional) integracion, referenciaCobro, monto);
            return agregarAuditoria(integracion, "Reintegro", resultado);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return "No se pudo completar la operación: " + ex.getMessage();
        }
    }

    // Solicita una guía o factura si el servicio implementa Documentable
    public String procesarDocumento(String identificador, String datos) {
        String mensaje = validarOperacion(identificador, "Generar documento");
        if (!mensaje.isEmpty()) {
            return mensaje;
        }
        IntegracionAPI integracion = catalogo.buscarPorIdentificador(identificador);
        try {
            String resultado = ejecutarGeneracion((Documentable) integracion, datos);
            return agregarAuditoria(integracion, "Documento", resultado);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return "No se pudo completar la operación: " + ex.getMessage();
        }
    }

    public String consultarEstado(String identificador, String referencia) {
        String mensaje = validarOperacion(identificador, "Consultar estado");
        if (!mensaje.isEmpty()) {
            return mensaje;
        }
        IntegracionAPI integracion = catalogo.buscarPorIdentificador(identificador);
        try {
            String resultado = integracion.consultarEstado(IntegracionAPI.validarTexto(referencia, "Referencia"));
            return agregarAuditoria(integracion, "Consulta de estado", resultado);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return "No se pudo completar la operación: " + ex.getMessage();
        }
    }

    public String procesarCotizacion(String identificador, String origen, String destino, double peso) {
        String mensaje = validarOperacion(identificador, "Cotizar envío");
        if (!mensaje.isEmpty()) {
            return mensaje;
        }
        IntegracionAPI integracion = catalogo.buscarPorIdentificador(identificador);
        try {
            String resultado = String.format(Locale.ROOT, "Cotización simulada: Q %.2f", ((ProveedorLogistico) integracion).cotizarEnvio(origen, destino, peso));
            return agregarAuditoria(integracion, "Cotización", resultado);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return "No se pudo completar la operación: " + ex.getMessage();
        }
    }

    // Describe la acción recibida, sin consultar un historial
    public String consultarAuditoria(String identificador, String accion, String resultado) {
        String mensaje = validarOperacion(identificador, "Consultar auditoría");
        if (!mensaje.isEmpty()) {
            return mensaje;
        }
        IntegracionAPI integracion = catalogo.buscarPorIdentificador(identificador);
        try {
            return obtenerAuditoria((Auditable) integracion, accion, resultado);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return "No se pudo completar la operación: " + ex.getMessage();
        }
    }

    // Revisa la capacidad antes de leer los montos o ejecutar la operación
    public String validarOperacion(String identificador, String operacion) {
        if (operacion == null) {
            return "Seleccione una operación.";
        }
        IntegracionAPI integracion = catalogo.buscarPorIdentificador(identificador);
        if (integracion == null) {
            return "No existe una integración con ese identificador.";
        }

        boolean admite;
        switch (operacion) {
            case "Cobrar":
            case "Reintegrar":
                admite = integracion instanceof Transaccional;
                break;
            case "Generar documento":
                admite = integracion instanceof Documentable;
                break;
            case "Cotizar envío":
                admite = integracion instanceof ProveedorLogistico;
                break;
            case "Consultar auditoría":
                admite = integracion instanceof Auditable;
                break;
            case "Consultar estado":
                admite = true;
                break;
            default:
                admite = false;
        }
        if (!admite) {
            return "Operación no aplicable.";
        }
        if (!estaDisponible(integracion)) {
            return "La integración debe estar activa y conectada.";
        }
        // Un texto vacío indica que la operación puede continuar
        return "";
    }

    private boolean estaDisponible(IntegracionAPI integracion) {
        return integracion.isActiva() && integracion.isConectada();
    }

    // Agrega la auditoría solo si el servicio implementa Auditable
    private String agregarAuditoria(IntegracionAPI integracion, String accion, String resultado) {
        if (integracion instanceof Auditable) {
            return resultado + "\n" + obtenerAuditoria((Auditable) integracion, accion, resultado);
        }
        return resultado;
    }

    // Usa Transaccional para cobrar con cualquier pasarela compatible
    private String ejecutarCobro(Transaccional servicio, double monto) {
        IntegracionAPI.validarPositivo(monto, "El monto");
        return servicio.cobrar(monto);
    }

    // Usa Transaccional para realizar el reintegro
    private String ejecutarReintegro(Transaccional servicio, String referenciaCobro, double monto) {
        IntegracionAPI.validarTexto(referenciaCobro, "Referencia del cobro");
        IntegracionAPI.validarPositivo(monto, "El monto");
        return servicio.reintegrar(referenciaCobro, monto);
    }

    // El servicio decide si genera una guía o una factura
    private String ejecutarGeneracion(Documentable servicio, String datos) {
        return servicio.generarDocumento(IntegracionAPI.validarTexto(datos, "Datos del documento"));
    }

    private String obtenerAuditoria(Auditable servicio, String accion, String resultado) {
        return servicio.generarAuditoria(IntegracionAPI.validarTexto(accion, "Acción"), IntegracionAPI.validarTexto(resultado, "Resultado"));
    }
}
