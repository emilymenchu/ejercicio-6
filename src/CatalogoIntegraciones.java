import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Guarda las integraciones y permite buscarlas y ordenarlas
public class CatalogoIntegraciones {
    // Una sola lista para pagos, logística y facturación
    private final ArrayList<IntegracionAPI> integraciones;

    public CatalogoIntegraciones() {
        integraciones = new ArrayList<>();
    }

    // Este método sirve para cargar los diez servicios iniciales sin duplicarlos
    public void cargarIntegracionesIniciales() {
        // Incluye un servicio inactivo y otro desconectado para hacer pruebas
        registrarIntegracion(new PasarelaPago("PAG-01", "Pago Tarjeta", "entorno=simulado; moneda=GTQ", 120, "Tarjeta"));
        registrarIntegracion(new PasarelaPago("PAG-02", "Pago Transferencia", "entorno=simulado; moneda=GTQ", 95, "Transferencia"));
        registrarIntegracion(new PasarelaPago("PAG-03", "Pago Billetera", "entorno=simulado; moneda=GTQ", 80, "Billetera digital"));
        registrarIntegracion(new PasarelaPago("PAG-04", "Pago Alternativo", "entorno=simulado; moneda=GTQ", 150, "Tarjeta", false, true));
        registrarIntegracion(new ProveedorLogistico("LOG-01", "Envíos Nacionales", "entorno=simulado; cobertura=nacional", 200));
        registrarIntegracion(new ProveedorLogistico("LOG-02", "Mensajería Urbana", "entorno=simulado; cobertura=urbana", 110));
        registrarIntegracion(new ProveedorLogistico("LOG-03", "Carga Regional", "entorno=simulado; cobertura=regional", 300, true, false));
        registrarIntegracion(new CertificadorFEL("FEL-01", "Certificador Uno", "entorno=simulado; formato=XML", 170));
        registrarIntegracion(new CertificadorFEL("FEL-02", "Certificador Dos", "entorno=simulado; formato=XML", 140));
        registrarIntegracion(new CertificadorFEL("FEL-03", "Certificador Tres", "entorno=simulado; formato=XML", 220));
    }

    // Agrega el servicio si existe y su identificador no está repetido
    public boolean registrarIntegracion(IntegracionAPI integracion) {
        if (integracion == null || buscarPorIdentificador(integracion.getIdentificador()) != null) {
            return false;
        }
        integraciones.add(integracion);
        return true;
    }

    // Busca el identificador y devuelve null si no lo encuentra
    public IntegracionAPI buscarPorIdentificador(String identificador) {
        if (identificador == null) { return null; }
        for (IntegracionAPI integracion : integraciones) {
            if (integracion.getIdentificador().equals(identificador.trim())) { return integracion; }
        }
        return null;
    }

    // Devuelve una copia de la lista que no se puede modificar
    public List<IntegracionAPI> consultarIntegraciones() {
        return Collections.unmodifiableList(new ArrayList<>(integraciones));
    }

    public void ordenarPorLatencia() {
        Collections.sort(integraciones);
    }
}
