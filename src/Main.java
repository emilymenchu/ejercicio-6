import javax.swing.SwingUtilities;

// Prepara la aplicación y abre la interfaz gráfica
public class Main implements Runnable {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }

    @Override
    public void run() {
        CatalogoIntegraciones catalogo = new CatalogoIntegraciones();
        catalogo.cargarIntegracionesIniciales();
        MotorCheckout motor = new MotorCheckout(catalogo);
        VistaGrafica vista = new VistaGrafica();
        Controlador controlador = new Controlador(catalogo, motor, vista);
        controlador.iniciar();
    }
}
