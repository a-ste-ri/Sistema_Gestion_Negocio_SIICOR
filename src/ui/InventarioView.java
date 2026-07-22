package ui;

import database.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import services.ProductoService;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Component;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;

public class InventarioView extends javax.swing.JFrame {
    
    private javax.swing.JPopupMenu popup;
    private javax.swing.JList<String> listaSugerencias;
    private javax.swing.JLayeredPane layer;
    private int indiceSeleccion = 0;
    
private void mantenerConfiguracionTabla() {

// Evitar que se pierda el diseño al recargar/buscar
tablaProductos.getTableHeader().setReorderingAllowed(false);
tablaProductos.getTableHeader().setResizingAllowed(false);

tablaProductos.setRowHeight(25);

// Tamaños de columnas
tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(40);
tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(180);
tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(100);
tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(100);
tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(80);

// ==========================================
// SEMÁFORO DE STOCK
// ==========================================

tablaProductos.getColumnModel().getColumn(5).setCellRenderer(
    new DefaultTableCellRenderer() {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            Component c = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
            );

            if (value != null) {

                try {

                    int stock = Integer.parseInt(value.toString());

                    if (stock < 10) {

                        // 🔴 STOCK BAJO
                        c.setBackground(new Color(255, 150, 150));
                        c.setForeground(Color.BLACK);

                    } else if (stock >= 10 && stock <= 19) {

                        // 🟡 STOCK MEDIO
                        c.setBackground(new Color(255, 230, 150));
                        c.setForeground(Color.BLACK);

                    } else {

                        // 🟢 STOCK SUFICIENTE
                        c.setBackground(new Color(170, 240, 170));
                        c.setForeground(Color.BLACK);
                    }

                } catch (NumberFormatException e) {

                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }
            }

            // Mantener selección de la fila
            if (isSelected) {

                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }

            return c;
        }
    }
);

// ==========================================
// OCULTAR ID REAL
// ==========================================

tablaProductos.getColumnModel().getColumn(1).setMinWidth(0);
tablaProductos.getColumnModel().getColumn(1).setMaxWidth(0);
tablaProductos.getColumnModel().getColumn(1).setWidth(0);

}

        
public void cargarProductos() {

Connection conn = null;
PreparedStatement ps = null;
ResultSet rs = null;

try {

    conn = Conexion.conectar();

    String sql = "SELECT * FROM productos ORDER BY nombre ASC";

    ps = conn.prepareStatement(sql);
    rs = ps.executeQuery();

    DefaultTableModel modelo = new DefaultTableModel(
        new Object[]{
            "#",
            "ID",
            "Nombre",
            "Precio Compra",
            "Precio Venta",
            "Stock"
        }, 0
    ) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    int contador = 1;

    while (rs.next()) {

        modelo.addRow(new Object[]{
        contador++,
        rs.getInt("id"),
        rs.getString("nombre"),
        rs.getDouble("precio_compra"),
        rs.getDouble("precio"),
        rs.getInt("stock")
    });
    }

    tablaProductos.setModel(modelo);

    mantenerConfiguracionTabla();

} catch (Exception e) {

    e.printStackTrace();

    JOptionPane.showMessageDialog(
        this,
        "Error al cargar productos: " + e.getMessage()
    );

} finally {

    try {

        if (rs != null) rs.close();
        if (ps != null) ps.close();
        if (conn != null) conn.close();

    } catch (Exception e) {

        e.printStackTrace();
    }
}

}

private boolean validarCampos() {

if (txtNombre.getText().trim().isEmpty() ||
    txtPrecioCompra.getText().trim().isEmpty() ||
    txtPrecio.getText().trim().isEmpty() ||
    txtStock.getText().trim().isEmpty()) {

    JOptionPane.showMessageDialog(
        this,
        "Complete todos los campos"
    );

    return false;
}

try {

    double precioCompra =
        Double.parseDouble(txtPrecioCompra.getText().trim());

    double precioVenta =
        Double.parseDouble(txtPrecio.getText().trim());

    int stock =
        Integer.parseInt(txtStock.getText().trim());

    if (precioCompra < 0 || precioVenta < 0 || stock < 0) {

        JOptionPane.showMessageDialog(
            this,
            "Los valores no pueden ser negativos"
        );

        return false;
    }

} catch (NumberFormatException e) {

    JOptionPane.showMessageDialog(
        this,
        "Precio o stock inválido"
    );

    return false;
}

return true;

}

private void buscarProductos(String texto) {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        conn = Conexion.conectar();

        String sql = "SELECT * FROM productos WHERE nombre LIKE ? ORDER BY nombre ASC";
        ps = conn.prepareStatement(sql);

        ps.setString(1, "%" + texto + "%");

        rs = ps.executeQuery();

        DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{
                    "#",
                    "ID",
                    "Nombre",
                    "Precio Compra",
                    "Precio Venta",
                    "Stock"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        int contador = 1;

        while (rs.next()) {
            modelo.addRow(new Object[]{
                contador++,
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getInt("stock")
            });
        }
        tablaProductos.setModel(modelo);
        mantenerConfiguracionTabla();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void aplicarColoresStock() {

tablaProductos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
    @Override
    public java.awt.Component getTableCellRendererComponent(
            javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {

        java.awt.Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        // Fondo alternado (zebra style)
        if (!isSelected) {
            if (row % 2 == 0) {
                c.setBackground(new java.awt.Color(245, 245, 245));
            } else {
                c.setBackground(java.awt.Color.WHITE);
            }
        }

        // centrar texto
        setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        return c;
    }
});
}

private void mostrarSugerencias(String texto) {
    indiceSeleccion = 0;
    
    if (texto.length() < 2) {
        popup.setVisible(false);
        return;
}

    DefaultListModel<String> modelo = new DefaultListModel<>();

    try (Connection conn = Conexion.conectar()) {

        String sql = "SELECT nombre FROM productos WHERE nombre LIKE ? ORDER BY nombre ASC LIMIT 5";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, texto + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addElement(rs.getString("nombre"));
        }

        listaSugerencias.setModel(modelo);

        // 👉 SOLO mostrar si hay resultados
        if (modelo.getSize() > 0) {
        popup.setLocation(
        txtBuscar.getLocationOnScreen().x,
        txtBuscar.getLocationOnScreen().y + txtBuscar.getHeight()
    );

    popup.setVisible(true);

        // 🔥 SOLUCIÓN: devolver foco al input
     
    } else {
        popup.setVisible(false);
    }

    } catch (Exception e) {
        e.printStackTrace();
    }
    if (modelo.getSize() > 0) {
    listaSugerencias.setSelectedIndex(0);
}

}

    /**
     * Creates new form InventarioView
     */
   public InventarioView() {
    initComponents();
    popup = new javax.swing.JPopupMenu();
    popup.setFocusable(false);
    layer = getLayeredPane();
    layer.add(popup, javax.swing.JLayeredPane.POPUP_LAYER);
    listaSugerencias = new javax.swing.JList<>();

    listaSugerencias.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    listaSugerencias.setFixedCellHeight(25);
listaSugerencias.setFont(new java.awt.Font("Segoe UI", 0, 14));

    // 👉 Click en sugerencia
    listaSugerencias.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            String seleccionado = listaSugerencias.getSelectedValue();

            if (seleccionado != null) {
                txtBuscar.setText(seleccionado);
                popup.setVisible(false);
                buscarProductos(seleccionado); // 🔥 filtra automáticamente
            }
        }
    });
   

    // 👉 agregar scroll al popup
    popup.add(new javax.swing.JScrollPane(listaSugerencias));
    

    cargarProductos();
    mantenerConfiguracionTabla();

    // 🔒 NO permitir mover columnas
    tablaProductos.getTableHeader().setReorderingAllowed(false);

    // 🔒 NO permitir redimensionar columnas
    tablaProductos.getTableHeader().setResizingAllowed(false);

    // 🔒 Selección de fila completa
    tablaProductos.setRowSelectionAllowed(true);
    tablaProductos.setColumnSelectionAllowed(false);
    tablaProductos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

    // 🎯 Tamaños profesionales
    tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(40);  // #
    tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(220); // Nombre
    tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(80);  // Precio
    tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(80);  // Stock

    // 🔒 Altura fija
    tablaProductos.setRowHeight(25);
    tablaProductos.setSelectionBackground(new java.awt.Color(0, 120, 215));
tablaProductos.setSelectionForeground(java.awt.Color.WHITE);
tablaProductos.setGridColor(new java.awt.Color(200, 200, 200));
tablaProductos.setShowGrid(true);

tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {

        int fila = tablaProductos.getSelectedRow();

        if (fila != -1) {
           txtNombre.setText(
tablaProductos.getValueAt(fila, 2).toString()
);

txtPrecioCompra.setText(
tablaProductos.getValueAt(fila, 3).toString()
);

txtPrecio.setText(
tablaProductos.getValueAt(fila, 4).toString()
);

txtStock.setText(
tablaProductos.getValueAt(fila, 5).toString()
);

        }
    }
});
}
   
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtNombre = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        txtStock = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        bntAtras = new javax.swing.JButton();
        txtBuscar = new javax.swing.JTextField();
        txtPrecioCompra = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        txtPrecio.setText("$");
        txtPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioActionPerformed(evt);
            }
        });

        txtStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStockActionPerformed(evt);
            }
        });

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tablaProductos.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(tablaProductos);
        tablaProductos.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tablaProductos.getColumnModel().getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(10);
        }

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        jLabel1.setText("NOMBRE PRODUCTO");

        jLabel2.setText("PRECIO VENTA");

        jLabel3.setText("STOCK");

        bntAtras.setText("Volver");
        bntAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntAtrasActionPerformed(evt);
            }
        });

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        txtPrecioCompra.setText("$");
        txtPrecioCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioCompraActionPerformed(evt);
            }
        });

        jLabel4.setText("PRECIO COMPRA");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bntAtras)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPrecioCompra, javax.swing.GroupLayout.Alignment.LEADING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                    .addComponent(txtBuscar))
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bntAtras))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(13, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActualizar)
                        .addGap(23, 23, 23)
                        .addComponent(btnEliminar)
                        .addGap(80, 80, 80))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStockActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed

if (!validarCampos()) {
    return;
}

try {

    String nombre = txtNombre.getText().trim();

    double precioCompra =
        Double.parseDouble(txtPrecioCompra.getText().trim());

    double precioVenta =
        Double.parseDouble(txtPrecio.getText().trim());

    int stock =
        Integer.parseInt(txtStock.getText().trim());

    ProductoService ps = new ProductoService();

    ps.insertar(
        nombre,
        precioCompra,
        precioVenta,
        stock
    );

    JOptionPane.showMessageDialog(
        this,
        "Producto guardado correctamente"
    );

    cargarProductos();

    // Limpiar campos
    txtNombre.setText("");
    txtPrecioCompra.setText("");
    txtPrecio.setText("");
    txtStock.setText("");

} catch (NumberFormatException e) {

    JOptionPane.showMessageDialog(
        this,
        "Precio o stock inválido"
    );

} catch (Exception e) {

    JOptionPane.showMessageDialog(
        this,
        "Error: " + e.getMessage()
    );
}


// TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed

    // 🔒 VALIDACIÓN AQUÍ (al inicio)
if (tablaProductos.getSelectedRow() == -1) {

    JOptionPane.showMessageDialog(
        this,
        "Seleccione un producto"
    );

    return;
}

if (!validarCampos()) {
    return;
}

try {

    int fila = tablaProductos.getSelectedRow();

    int id = Integer.parseInt(
        tablaProductos.getValueAt(fila, 1).toString()
    );

    String nombre =
        txtNombre.getText().trim();

    double precioCompra =
        Double.parseDouble(txtPrecioCompra.getText().trim());

    double precioVenta =
        Double.parseDouble(txtPrecio.getText().trim());

    int stock =
        Integer.parseInt(txtStock.getText().trim());

    ProductoService ps = new ProductoService();

    ps.actualizar(
        id,
        nombre,
        precioCompra,
        precioVenta,
        stock
    );

    JOptionPane.showMessageDialog(
        this,
        "Producto actualizado correctamente"
    );

    txtNombre.setText("");
    txtPrecioCompra.setText("");
    txtPrecio.setText("");
    txtStock.setText("");

    cargarProductos();

    tablaProductos.clearSelection();

} catch (Exception e) {

    JOptionPane.showMessageDialog(
        this,
        "Error: " + e.getMessage()
    );
}

     // TODO add your handling code here:
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed


    // 🔒 VALIDACIÓN AQUÍ (al inicio)
    if (tablaProductos.getSelectedRow() == -1) {
        javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un producto");
        return;
    }
   

    int fila = tablaProductos.getSelectedRow();
   int id = Integer.parseInt(tablaProductos.getValueAt(fila, 1).toString());

    ProductoService ps = new ProductoService();
    int opcion = JOptionPane.showConfirmDialog(this, "¿Eliminar producto?", "Confirmar", JOptionPane.YES_NO_OPTION);

if (opcion != JOptionPane.YES_OPTION) {
    return;
}
    ps.eliminar(id);
    txtNombre.setText("");
txtPrecio.setText("");
txtStock.setText("");

    cargarProductos();
    mantenerConfiguracionTabla();
 // TODO add your handling code here:
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void bntAtrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntAtrasActionPerformed
    MenuPrincipalView menu = new MenuPrincipalView();
    menu.setLocationRelativeTo(null);
    menu.setVisible(true);
    this.dispose();// TODO add your handling code here:
    }//GEN-LAST:event_bntAtrasActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
            // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        String texto = txtBuscar.getText().trim();

    if (texto.isEmpty()) {
        cargarProductos();
        mantenerConfiguracionTabla();
        popup.setVisible(false);
        return;
    }

    if (texto.length() >= 2) {
        mostrarSugerencias(texto);
    } else {
        popup.setVisible(false);
    }
 // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed

    int size = listaSugerencias.getModel().getSize();

    if (size == 0) return;

    // 🔽 FLECHA ABAJO
    if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {

        if (!popup.isVisible()) return;

        indiceSeleccion++;

        if (indiceSeleccion >= size) {
            indiceSeleccion = 0;
        }

        listaSugerencias.setSelectedIndex(indiceSeleccion);
        listaSugerencias.ensureIndexIsVisible(indiceSeleccion);
    }

    // 🔼 FLECHA ARRIBA
    if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {

        if (!popup.isVisible()) return;

        indiceSeleccion--;

        if (indiceSeleccion < 0) {
            indiceSeleccion = size - 1;
        }

        listaSugerencias.setSelectedIndex(indiceSeleccion);
        listaSugerencias.ensureIndexIsVisible(indiceSeleccion);
    }

    // 🔍 ENTER
    if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {

        if (popup.isVisible() && indiceSeleccion >= 0) {
            String seleccionado = listaSugerencias.getSelectedValue();
            txtBuscar.setText(seleccionado);
            popup.setVisible(false);
            buscarProductos(seleccionado);
        } else {
            buscarProductos(txtBuscar.getText().trim());
        }
    }

    // ❌ ESC
    if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
        popup.setVisible(false);
        indiceSeleccion = -1;
    }
  // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarKeyPressed

    private void txtPrecioCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioCompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioCompraActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InventarioView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InventarioView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InventarioView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InventarioView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InventarioView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntAtras;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtPrecioCompra;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
