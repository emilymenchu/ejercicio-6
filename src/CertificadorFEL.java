import java.util.UUID;

// Simula la generación y consulta de facturas XML, sin validez fiscal
public class CertificadorFEL extends IntegracionAPI implements Documentable, Auditable {
    public CertificadorFEL(String identificador, String nombre, String configuracion, double latenciaPromedio) {
        this(identificador, nombre, configuracion, latenciaPromedio, true, true);
    }

    public CertificadorFEL(String identificador, String nombre, String configuracion, double latenciaPromedio, boolean activa, boolean conectada) {
        super(identificador, nombre, configuracion, latenciaPromedio, activa, conectada);
    }

    @Override
    public String consultarEstado(String referencia) {
        verificarDisponibilidad();
        return "Estado simulado de la factura " + validarTexto(referencia, "Referencia de factura")
                + ": certificada (sin validez fiscal).";
    }

    // Crea una factura XML simulada con los datos recibidos
    @Override
    public String generarDocumento(String datos) {
        verificarDisponibilidad();
        String contenido = validarTexto(datos, "Datos de facturación");
        return "<FacturaSimulada sinValidezFiscal=\"true\" id=\"FEL-" + UUID.randomUUID()
                + "\"><Emisor>" + escaparXML(getNombre()) + "</Emisor><Datos>"
                + escaparXML(contenido) + "</Datos></FacturaSimulada>";
    }

    // Ajusta los caracteres especiales antes de incluir el texto en el XML
    private String escaparXML(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    @Override
    public String generarAuditoria(String accion, String resultado) {
        return describirAuditoria(accion, resultado);
    }
}
