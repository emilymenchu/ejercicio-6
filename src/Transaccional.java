// Define las operaciones de cobro y reintegro de los servicios de pago
public interface Transaccional {
    String cobrar(double monto);
    // Simula un reintegro usando la referencia del cobro original
    String reintegrar(String referenciaCobro, double monto);
}
