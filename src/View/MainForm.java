package view;

import Controller.FoodController;
import model.Food;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainForm extends JFrame {

    private final FoodController controller = new FoodController();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField = new JTextField(20);
    private final JLabel statusLabel = new JLabel(" ");

    private final String[] columnNames = {
        "Food ID", "Name", "Category",
        "Quantity", "Price", "Expiry Date"
    };

    // Current sorting choice
    private int sortColumn = 0;
    private boolean ascending = true;

    public MainForm() {
        setTitle("Food Inventory Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        // TITLE
        JLabel heading = new JLabel("Food Inventory Management");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        root.add(heading, BorderLayout.NORTH);

        // TABLE
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14));

        // Click a column header to sort. Click again to flip the order.
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());

                if (column < 0) {
                    return;
                }

                if (column == sortColumn) {
                    ascending = !ascending;
                } else {
                    sortColumn = column;
                    ascending = true;
                }

                refreshTable();
            }
        });

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottom = new JPanel(new GridLayout(3, 1, 5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add Food");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        bottom.add(searchPanel);
        bottom.add(actionPanel);
        bottom.add(statusLabel);
        root.add(bottom, BorderLayout.SOUTH);

        // BUTTON ACTIONS
        addButton.addActionListener(e -> showFoodDialog(null));
        editButton.addActionListener(e -> editFood());
        deleteButton.addActionListener(e -> deleteFood());

        searchButton.addActionListener(e -> refreshTable());
        searchField.addActionListener(e -> refreshTable());

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });

        refreshTable();
    }

    // REFRESH TABLE
    private void refreshTable() {
        List<Food> foods = controller.getFoods(
                searchField.getText(), sortColumn, ascending);

        tableModel.setRowCount(0);

        for (Food food : foods) {
            tableModel.addRow(new Object[]{
                food.getFoodID(),
                food.getName(),
                food.getCategory(),
                food.getQuantity(),
                String.format("%.2f", food.getPrice()),
                food.getExpiryDate()
            });
        }

        statusLabel.setText(foods.size() + " item(s)  |  Sorted by "
                + columnNames[sortColumn]
                + (ascending ? " (A-Z)" : " (Z-A)")
                + "  |  Click a column header to sort");
    }

    // GET SELECTED FOOD
    private Food selectedFood() {
        int row = table.getSelectedRow();

        if (row < 0) {
            showError("Please select a food from the table first.");
            return null;
        }

        String foodID = tableModel.getValueAt(row, 0).toString();
        return controller.findFood(foodID);
    }

    // EDIT FOOD
    private void editFood() {
        Food food = selectedFood();

        if (food != null) {
            showFoodDialog(food);
        }
    }

    // ADD OR EDIT FOOD DIALOG
    private void showFoodDialog(Food existing) {
        JTextField foodID = new JTextField();
        JTextField name = new JTextField();
        JComboBox<String> category = new JComboBox<>(new String[]{
            "Fruits", "Vegetables", "Meat", "Dairy",
            "Beverages", "Snacks", "Other"
        });
        category.setEditable(true);
        JTextField quantity = new JTextField();
        JTextField price = new JTextField();
        JTextField expiry = new JTextField();

        // Fill in the old values when editing
        if (existing != null) {
            foodID.setText(existing.getFoodID());
            name.setText(existing.getName());
            category.setSelectedItem(existing.getCategory());
            quantity.setText(String.valueOf(existing.getQuantity()));
            price.setText(String.valueOf(existing.getPrice()));
            expiry.setText(existing.getExpiryDate());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Food ID:"));
        panel.add(foodID);
        panel.add(new JLabel("Name:"));
        panel.add(name);
        panel.add(new JLabel("Category:"));
        panel.add(category);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantity);
        panel.add(new JLabel("Price:"));
        panel.add(price);
        panel.add(new JLabel("Expiry (yyyy-MM-dd):"));
        panel.add(expiry);

        int result = JOptionPane.showConfirmDialog(
                this, panel,
                existing == null ? "Add Food" : "Edit Food",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String idValue = foodID.getText().trim();
            String nameValue = name.getText().trim();
            String categoryValue =
                    String.valueOf(category.getSelectedItem()).trim();
            String expiryValue = expiry.getText().trim();

            if (idValue.isEmpty() || nameValue.isEmpty()
                    || categoryValue.isEmpty() || expiryValue.isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }

            int quantityValue = Integer.parseInt(quantity.getText().trim());
            double priceValue = Double.parseDouble(price.getText().trim());

            if (quantityValue < 0 || priceValue < 0) {
                showError("Quantity and price cannot be negative.");
                return;
            }

            LocalDate.parse(expiryValue); // checks the date format

            Food food = new Food(idValue, nameValue, categoryValue,
                    quantityValue, priceValue, expiryValue);

            if (existing == null) {
                controller.addFood(food);
            } else {
                controller.updateFood(existing.getFoodID(), food);
            }

            refreshTable();

        } catch (NumberFormatException e) {
            showError("Quantity must be a whole number and price must be a number.");
        } catch (DateTimeParseException e) {
            showError("Expiry date must look like 2026-12-31.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    // DELETE FOOD
    private void deleteFood() {
        Food food = selectedFood();

        if (food == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete '" + food.getName() + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                controller.deleteFood(food.getFoodID());
                refreshTable();
            } catch (RuntimeException e) {
                showError(e.getMessage());
            }
        }
    }

    // DISPLAY ERROR
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Food Inventory",
                JOptionPane.ERROR_MESSAGE);
    }
}