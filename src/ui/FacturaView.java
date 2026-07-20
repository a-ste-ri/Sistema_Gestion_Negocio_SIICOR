package ui;

import javax.swing.*;

public class FacturaView extends JFrame {

    public FacturaView(String factura) {

        setTitle("Factura");
        setSize(400, 500);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setText(factura);
        area.setEditable(false);

        add(new JScrollPane(area));
    }
}