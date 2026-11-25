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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class VentanaListadoAlertas extends JDialog {

    private final JComboBox<String> cmbEstaciones = new JComboBox<String>();
    private final JTextField txtInicio = new JTextField(14);
    private final JTextField txtFin = new JTextField(14);
    private final JLabel lblResumen = new JLabel(" ");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(new String[]{"Fecha", "Hora", "Sensor", "Fenómeno", "Valor", "Nivel"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    public VentanaListadoAlertas(JFrame owner) {
        super(owner, "Alertas de una estación", true);
        VentanaPrincipal.llenaCombo(cmbEstaciones, InstitutoMeteorologia.getInstance().getCodigosEstaciones());
        txtInicio.setToolTipText("dd/MM/yyyy HH:mm");
        txtFin.setToolTipText("dd/MM/yyyy HH:mm");

        JButton btnListar = new JButton("Listar");
        JButton btnCerrar = new JButton("Cerrar");
        btnListar.addActionListener(e -> listar());
        btnCerrar.addActionListener(e -> dispose());

        JTable tabla = new JTable(modeloTabla);
        tabla.getTableHeader().setReorderingAllowed(false);
        int[] anchos = {85, 55, 65, 170, 90, 75};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
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
        VentanaPrincipal.configuraDialogo(this, owner, btnListar);
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
        LocalDateTime inicio = VentanaPrincipal.parseaFechaHora(this, txtInicio.getText());
        if (inicio == null) {
            return;
        }
        LocalDateTime fin = VentanaPrincipal.parseaFechaHora(this, txtFin.getText());
        if (fin == null) {
            return;
        }
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            String[][] alertas = InstitutoMeteorologia.getInstance().listaAlertas(estacion, inicio, fin);
            modeloTabla.setRowCount(0);
            for (String[] alerta : alertas) {
                modeloTabla.addRow(new Object[]{alerta[0], alerta[1], alerta[2], alerta[3], alerta[4] + " " + alerta[5], alerta[6]});
            }
            lblResumen.setText("Período: " + inicio.format(formato) + " a "
                    + fin.format(formato) + " — " + alertas.length
                    + (alertas.length == 1 ? " alerta." : " alertas."));
            if (alertas.length == 0) {
                VentanaPrincipal.mostrarInformacion(this, "Alertas de una estación", "La estación " + estacion + " no registra alertas en el período indicado.");
            }
        } catch (MeteorologiaException e) {
            modeloTabla.setRowCount(0);
            lblResumen.setText(" ");
            VentanaPrincipal.mostrarError(this, e.getMessage());
        }
    }
}