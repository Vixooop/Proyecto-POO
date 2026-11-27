// Autor Vicente Navarrete //
// 19/11/2026 //

package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class VentanaListadoAlertas extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNAS = {"Fecha", "Hora", "Sensor", "Fenómeno", "Valor", "Nivel"};
    private static final int[] ANCHOS_COLUMNAS = {85, 55, 65, 170, 90, 75};
    private static final String PATRON_FECHA_HORA = "dd/MM/yyyy HH:mm";
    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private final InstitutoMeteorologia instituto = InstitutoMeteorologia.getInstance();
    private final JComboBox<String> cmbEstaciones = new JComboBox<String>(instituto.getCodigosEstaciones());
    private final JTextField txtInicio = new JTextField(14);
    private final JTextField txtFin = new JTextField(14);
    private final JLabel lblResumen = new JLabel(" ");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    public VentanaListadoAlertas(JFrame owner) {
        super(owner, "Alertas de una estación", true);
        txtInicio.setToolTipText(PATRON_FECHA_HORA);
        txtFin.setToolTipText(PATRON_FECHA_HORA);

        JButton btnListar = new JButton("Listar");
        JButton btnCerrar = new JButton("Cerrar");
        btnListar.addActionListener(e -> listar());
        btnCerrar.addActionListener(e -> dispose());

        JTable tabla = new JTable(modeloTabla);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setAutoCreateColumnsFromModel(false);
        for (int i = 0; i < ANCHOS_COLUMNAS.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(ANCHOS_COLUMNAS[i]);
        }
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setPreferredSize(new Dimension(640, 220));

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(lblResumen, BorderLayout.CENTER);
        inferior.add(btnCerrar, BorderLayout.EAST);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        contenido.add(construyeCriterios(btnListar), BorderLayout.NORTH);
        contenido.add(desplazamiento, BorderLayout.CENTER);
        contenido.add(inferior, BorderLayout.SOUTH);
        setContentPane(contenido);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(btnListar);
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel construyeCriterios(JButton btnListar) {
        JPanel criterios = new JPanel(new GridBagLayout());
        criterios.setBorder(BorderFactory.createTitledBorder("Criterios de búsqueda"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 5, 3, 5);
        c.gridy = 0;
        c.gridx = 0;
        criterios.add(new JLabel("Estación:"), c);
        c.gridx = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        criterios.add(cmbEstaciones, c);
        c.gridy = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0;
        criterios.add(new JLabel("Inicio:"), c);
        c.gridx = 1;
        criterios.add(txtInicio, c);
        c.gridx = 2;
        criterios.add(new JLabel("Fin:"), c);
        c.gridx = 3;
        criterios.add(txtFin, c);
        c.gridx = 4;
        criterios.add(btnListar, c);
        return criterios;
    }

    private void listar() {
        String estacion = (String) cmbEstaciones.getSelectedItem();
        LocalDateTime inicio = leeFechaHora(txtInicio.getText());
        if (inicio == null) {
            return;
        }
        LocalDateTime fin = leeFechaHora(txtFin.getText());
        if (fin == null) {
            return;
        }
        try {
            String[][] alertas = instituto.listaAlertas(estacion, inicio, fin);
            Object[][] filas = Arrays.stream(alertas).map(a -> new Object[]{a[0], a[1], a[2], a[3], a[4] + " " + a[5], a[6]}).toArray(Object[][]::new);
            modeloTabla.setDataVector(filas, COLUMNAS);
            lblResumen.setText("Periodo: " + inicio.format(FORMATO_FECHA_HORA) + " a " + fin.format(FORMATO_FECHA_HORA) + " — " + alertas.length + (alertas.length == 1 ? " alerta." : " alertas."));
            if (alertas.length == 0) {
                mostrarInformacion("La estación " + estacion + " no registra alertas en el Periodo indicado.");
            }
        } catch (MeteorologiaException e) {
            modeloTabla.setDataVector(new Object[0][], COLUMNAS);
            lblResumen.setText(" ");
            mostrarError(e.getMessage());
        }
    }

    private LocalDateTime leeFechaHora(String texto) {
        try {
            return LocalDateTime.parse(texto.trim(), FORMATO_FECHA_HORA);
        } catch (DateTimeParseException e) {
            mostrarError("La fecha y hora tienen que tener el formato " + PATRON_FECHA_HORA + ".");
            return null;
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInformacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }
}