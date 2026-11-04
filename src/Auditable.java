// Define la descripción de auditoría de los servicios
public interface Auditable {
    String generarAuditoria(String accion, String resultado);
}
