// Guarda los datos que comparten las integraciones
public abstract class IntegracionAPI implements Comparable<IntegracionAPI> {
    private final String identificador;
    private final String nombre;
    private String configuracion;
    private boolean activa;
    private boolean conectada;
    private double latenciaPromedio;

    // Crea la integración activa y conectada
    protected IntegracionAPI(String identificador, String nombre, String configuracion, double latenciaPromedio) {
        this(identificador, nombre, configuracion, latenciaPromedio, true, true);
    }

    // Permite elegir los estados iniciales de la integración
    protected IntegracionAPI(String identificador, String nombre, String configuracion, double latenciaPromedio, boolean activa, boolean conectada) {
        this.identificador = validarTexto(identificador, "Identificador");
        this.nombre = validarTexto(nombre, "Nombre");
        setConfiguracion(configuracion);
        setLatenciaPromedio(latenciaPromedio);
        this.activa = activa;
        this.conectada = conectada;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getConfiguracion() {
        return configuracion;
    }

    public boolean isActiva() {
        return activa;
    }

    public boolean isConectada() {
        return conectada;
    }

    public double getLatenciaPromedio() {
        return latenciaPromedio;
    }

    public final void setConfiguracion(String configuracion) {
        this.configuracion = validarTexto(configuracion, "Configuración");
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public void setConectada(boolean conectada) {
        this.conectada = conectada;
    }

    // La latencia debe ser un número válido, mayor o igual a cero
    public final void setLatenciaPromedio(double latenciaPromedio) {
        if (!Double.isFinite(latenciaPromedio) || latenciaPromedio < 0) {
            throw new IllegalArgumentException("La latencia debe ser un número finito mayor o igual a cero.");
        }
        this.latenciaPromedio = latenciaPromedio;
    }

    // Cada servicio define cómo consultar el estado de su operación
    public abstract String consultarEstado(String referencia);

    // Este método sirve para quitar espacios de los extremos y revisar que el texto no esté vacío
    protected static String validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return texto.trim();
    }

    // Revisa que el monto o peso sea válido y mayor que cero
    protected static void validarPositivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser un número finito mayor que cero.");
        }
    }

    // El servicio debe estar activo y conectado
    protected void verificarDisponibilidad() {
        if (!activa || !conectada) {
            throw new IllegalStateException("La integración debe estar activa y conectada.");
        }
    }

    protected String describirAuditoria(String accion, String resultado) {
        return "Auditoría [" + identificador + "] " + validarTexto(accion, "Acción")
                + ": " + validarTexto(resultado, "Resultado");
    }

    // Ordena las integraciones de menor a mayor latencia
    @Override
    public int compareTo(IntegracionAPI otra) {
        return Double.compare(latenciaPromedio, otra.latenciaPromedio);
    }

    // Compara las integraciones por su identificador
    @Override
    public final boolean equals(Object objeto) {
        if (this == objeto) { return true; }
        if (!(objeto instanceof IntegracionAPI)) { return false; }
        return identificador.equals(((IntegracionAPI) objeto).identificador);
    }

    @Override
    public final int hashCode() {
        return identificador.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s - %s | activa: %b | conectada: %b | latencia: %s ms", identificador, nombre, activa, conectada, latenciaPromedio);
    }
}
