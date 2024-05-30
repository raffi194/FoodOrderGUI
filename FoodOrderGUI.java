package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.sql.*;

public class FoodOrderGUI extends JFrame {
    private JTextField foodNameField, quantityField, priceField, customerNameField;
    private JButton addButton, displayButton, deleteButton;
    private JTable table;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FoodOrderGUI gui = new FoodOrderGUI();
                gui.createAndShowGUI();
            }
        });
    }

    public void createAndShowGUI() {

        JFrame frame = new JFrame("Food Ordering System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(null);

        JLabel foodNameLabel = new JLabel("Food Name:");
        foodNameLabel.setBounds(20, 20, 100, 25);
        frame.add(foodNameLabel);

        foodNameField = new JTextField();
        foodNameField.setBounds(120, 20, 320, 25);
        frame.add(foodNameField);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(20, 50, 100, 25);
        frame.add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(120, 50, 320, 25);
        frame.add(quantityField);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(20, 80, 100, 25);
        frame.add(priceLabel);

        priceField = new JTextField();
        priceField.setBounds(120, 80, 320, 25);
        frame.add(priceField);

        JLabel customerNameLabel = new JLabel("Customer Name:");
        customerNameLabel.setBounds(20, 110, 120, 25);
        frame.add(customerNameLabel);

        customerNameField = new JTextField();
        customerNameField.setBounds(140, 110, 300, 25);
        frame.add(customerNameField);

        addButton = new JButton("Add Order");
        addButton.setBounds(120, 150, 120, 25);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addOrder();
            }
        });
        frame.add(addButton);

        deleteButton = new JButton("Delete Order");
        deleteButton.setBounds(260, 150, 120, 25);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(FoodOrderGUI.this, "Select a row to delete.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    int id = (int) table.getValueAt(selectedRow, 0);
                    deleteOrder(id);
                }
            }
        });
        frame.add(deleteButton);

        displayButton = new JButton("Display Orders");
        displayButton.setBounds(400, 150, 140, 25);
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayOrders();
            }
        });
        frame.add(displayButton);

        String[] columnNames = { "Order ID", "Food Name", "Quantity", "Price", "Customer Name" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 200, 740, 250);
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    private void deleteOrder(int id) {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this order?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM orders WHERE id = ?";
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Order deleted successfully.");
                displayOrders();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting order.");
            }
        }
    }

    private void addOrder() {
        String foodName = foodNameField.getText();
        String quantityText = quantityField.getText();
        String priceText = priceField.getText();
        String customerName = customerNameField.getText();

        int quantity;
        double price;
        try {
            quantity = Integer.parseInt(quantityText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be an integer and price must be a number.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Add order with the following details?\nFood Name: " + foodName + "\nQuantity: " + quantity
                        + "\nPrice: " + price + "\nCustomer Name: " + customerName,
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try {
                    String query = "INSERT INTO gui2 (food_name, quantity, price, customer_name) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, foodName);
                    preparedStatement.setInt(2, quantity);
                    preparedStatement.setDouble(3, price);
                    preparedStatement.setString(4, customerName);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Order added successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error adding order.");
                }
            }
        }
    }

    private void displayOrders() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try {
                String query = "SELECT * FROM gui2";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String foodName = rs.getString("food_name");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    String customerName = rs.getString("customer_name");
                    tableModel.addRow(new Object[] { id, foodName, quantity, price, customerName });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
