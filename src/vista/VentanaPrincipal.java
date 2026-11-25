// Autor Vicente Navarrete //
// 19/11/2026 //

package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String[] LISTADOS = {"Regiones", "Comunas", "Estaciones de una comuna", "Sensores de una estación", "Mediciones de un sensor", "Resumen estadístico de un sensor", "Alertas de una estación"};
    private static final int LISTADO_ALERTAS = 6;

    private final InstitutoMeteorologia instituto;
    private final InterfazUsuario interfaz;
    private final JLabel lblEstado = new JLabel();
    private final JComboBox<String> cmbListados = new JComboBox<String>(LISTADOS);

    public VentanaPrincipal() {
        super("Sistema de Información Meteorológica");
        instituto = InstitutoMeteorologia.getInstance();
        interfaz = InterfazUsuario.getInstance();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        JLabel titulo = new JLabel("SISTEMA DE INFORMACIÓN METEOROLÓGICA", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));

        JPanel centro = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 4, 0, 4);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        centro.add(construyePanelRegistro(), c);
        c.gridx = 1;
        c.gridheight = 1;
        centro.add(construyePanelDatos(), c);
        c.gridy = 1;
        centro.add(construyePanelListados(), c);

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> salir());
        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(lblEstado, BorderLayout.CENTER);
        inferior.add(btnSalir, BorderLayout.EAST);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contenido.add(titulo, BorderLayout.NORTH);
        contenido.add(centro, BorderLayout.CENTER);
        contenido.add(inferior, BorderLayout.SOUTH);
        setContentPane(contenido);
        actualizarEstado();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel construyePanelRegistro() {
        JPanel panel = nuevoPanelDeBotones("Registro");
        agregaBoton(panel, "Crear región", e -> enConsola(interfaz::crearRegion));
        agregaBoton(panel, "Crear comuna", e -> enConsola(interfaz::crearComuna));
        agregaBoton(panel, "Crear estación meteorológica", e -> enConsola(interfaz::crearEstacion));
        agregaBoton(panel, "Instalar sensor", e -> abrirDialogo(() -> new VentanaInstalarSensor(this)));
        agregaBoton(panel, "Registrar medición", e -> abrirDialogo(() -> new VentanaRegistrarMedicion(this)));
        agregaBoton(panel, "Cambiar estado de sensor", e -> abrirDialogo(() -> new VentanaCambiarEstadoSensor(this)));
        return panel;
    }

    private JPanel construyePanelDatos() {
        JPanel panel = nuevoPanelDeBotones("Datos");
        agregaBoton(panel, "Cargar datos", e -> cargarDatos());
        agregaBoton(panel, "Importar datos de texto", e -> importarDatosTexto());
        agregaBoton(panel, "Guardar datos", e -> guardarDatos());
        return panel;
    }

    private JPanel construyePanelListados() {
        JPanel panel = nuevoPanelDeBotones("Listados");
        panel.add(new JLabel("Seleccione el listado:"));
        cmbListados.setSelectedIndex(LISTADO_ALERTAS);
        panel.add(cmbListados);
        agregaBoton(panel, "Generar listado", e -> generarListado());
        return panel;
    }

    private JPanel nuevoPanelDeBotones(String titulo) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        return panel;
    }

    private void agregaBoton(JPanel panel, String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        panel.add(boton);
    }

    private void actualizarEstado() {
        if (instituto.existenDatos()) {
            lblEstado.setText("Estado: el sistema tiene datos registrados.");
        } else {
            lblEstado.setText("Estado: el sistema no tiene datos registrados.");
        }
    }

    private void enConsola(Runnable operacion) {
        setVisible(false);
        try {
            operacion.run();
        } finally {
            actualizarEstado();
            setVisible(true);
        }
    }

    private void abrirDialogo(Supplier<JDialog> ventana) {
        if (instituto.getCodigosEstaciones().length == 0) {
            mostrarInformacion("Información", "No existen estaciones, cree una antes de continuar.");
            return;
        }
        ventana.get().setVisible(true);
        actualizarEstado();
    }

    private void generarListado() {
        switch (cmbListados.getSelectedIndex()) {
            case 0:
                enConsola(interfaz::listarRegiones);
                break;
            case 1:
                enConsola(interfaz::listarComunas);
                break;
            case 2:
                enConsola(interfaz::listarEstaciones);
                break;
            case 3:
                enConsola(interfaz::listarSensores);
                break;
            case 4:
                enConsola(interfaz::listarMediciones);
                break;
            case 5:
                enConsola(interfaz::listarResumenMediciones);
                break;
            default:
                abrirDialogo(() -> new VentanaListadoAlertas(this));
                break;
        }
    }

    private void cargarDatos() {
        try {
            instituto.cargaDatos();
            mostrarInformacion("Cargar datos", "Datos cargados desde datos/meteorologia.dat.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
        actualizarEstado();
    }

    private void importarDatosTexto() {
        try {
            instituto.importaDatosTexto();
            mostrarInformacion("Importar datos de texto", "Datos importados desde datos/.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
        actualizarEstado();
    }

    private void guardarDatos() {
        try {
            instituto.guardaDatos();
            mostrarInformacion("Guardar datos", "Datos guardados en datos/meteorologia.dat.");
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
        actualizarEstado();
    }

    private void salir() {
        if (instituto.existenDatos()) {
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea guardar los datos antes de salir?", "Salir",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (respuesta == JOptionPane.CANCEL_OPTION || respuesta == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    instituto.guardaDatos();
                } catch (MeteorologiaException e) {
                    mostrarError(e.getMessage());
                    return;
                }
            }
        }
        dispose();
        System.exit(0);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}