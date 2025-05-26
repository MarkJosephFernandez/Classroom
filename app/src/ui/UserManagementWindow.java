package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementWindow extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton;
    private UserDAO userDAO = new UserDAO();

    public UserManagementWindow() {
        setTitle("User Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"User ID", "Full Name", "Username"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Delete button setup
        deleteButton = new JButton("Delete Selected User");
        deleteButton.addActionListener(e -> deleteSelectedUser());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadUsers();
    }

    private void loadUsers() {
        tableModel.setRowCount(0); // Clear table
        List<User> users = userDAO.getUsersWithRoleUser();
        for (User u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getFullName(), u.getUsername()});
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int userId = (int) tableModel.getValueAt(selectedRow, 0);

        if (userDAO.deleteUser(userId)) {
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
            loadUsers(); // Refresh table
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete user.");
        }
    }
}
