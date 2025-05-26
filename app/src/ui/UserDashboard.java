package ui;

import com.toedter.calendar.JDateChooser;
import dao.ReservationDAO;
import dao.RoomDAO;
import model.Reservation;
import model.Room;
import model.User;
import model.RoomComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.Calendar;

import ui.LoginWindow;

public class UserDashboard extends JFrame {

    private User user;
    private JComboBox<RoomComboItem> roomBox;
    private JComboBox<String> departmentBox;
    private JDateChooser startDateChooser, endDateChooser;
    private JSpinner startTimeSpinner, endTimeSpinner;
    private DefaultTableModel tableModel;

    private JTextField nameField, departmentField;

    public UserDashboard(User user) {
        this.user = user;
        setTitle("User Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // === Header Panel with Welcome and Side Buttons ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanel.getBackground());

        JLabel titleLabel = new JLabel("Welcome, " + user.getFullName(), SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // === Sidebar with Logout + Reserve ===
        JPanel buttonSidebar = new JPanel();
        buttonSidebar.setLayout(new BoxLayout(buttonSidebar, BoxLayout.Y_AXIS));
        buttonSidebar.setBackground(mainPanel.getBackground());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginWindow().setVisible(true);
            }
        });

        JButton reserveBtn = new JButton("Reserve");
        reserveBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        reserveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reserveBtn.setBackground(new Color(0, 123, 255));
        reserveBtn.setForeground(Color.WHITE);
        reserveBtn.setFocusPainted(false);
        reserveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reserveBtn.addActionListener(e -> makeReservation());

        buttonSidebar.add(logoutBtn);
        buttonSidebar.add(Box.createVerticalStrut(10));
        buttonSidebar.add(reserveBtn);

        headerPanel.add(buttonSidebar, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // === Booking Form Panel ===
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(mainPanel.getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        nameField = new JTextField(user.getFullName(), 15);
        departmentBox = new JComboBox<>(new String[]{"CCS", "CHM", "CBA", "CN", "CCRIM", "CAS"});
        departmentBox.setSelectedIndex(-1);

        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("yyyy-MM-dd");

        startTimeSpinner = new JSpinner(new SpinnerDateModel());
        startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
        endTimeSpinner = new JSpinner(new SpinnerDateModel());
        endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));

        roomBox = new JComboBox<>();
        loadAvailableRooms();

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        topPanel.add(nameField, gbc);
        gbc.gridx = 2;
        topPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 3;
        topPanel.add(departmentBox, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Room Type:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(roomBox, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        topPanel.add(startDateChooser, gbc);
        gbc.gridx = 2;
        topPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 3;
        topPanel.add(startTimeSpinner, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 3;
        topPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        topPanel.add(endDateChooser, gbc);
        gbc.gridx = 2;
        topPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 3;
        topPanel.add(endTimeSpinner, gbc);

        mainPanel.add(topPanel, BorderLayout.CENTER);

        // === Reservation History Table ===
        String[] columns = {"Reservation ID", "Name", "Department", "Room", "Room Type", "Start Date", "End Date", "Start Time", "End Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reservation History"));
        scrollPane.setPreferredSize(new Dimension(850, 180));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        loadUserReservations();
    }



    private void loadAvailableRooms() {
        RoomDAO roomDAO = new RoomDAO();
        List<Room> rooms = roomDAO.getAllRooms();
        for (Room r : rooms) {
            roomBox.addItem(new RoomComboItem(r.getId(), r.getType()));
        }
    }

    private void makeReservation() {
        java.util.Date start = startDateChooser.getDate();
        java.util.Date end = endDateChooser.getDate();

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.");
            return;
        }

        if (end.before(start)) {
            JOptionPane.showMessageDialog(this, "End date cannot be before start date.");
            return;
        }

        RoomComboItem selectedRoom = (RoomComboItem) roomBox.getSelectedItem();
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please select a room.");
            return;
        }

        String reserverName = nameField.getText().trim();
        if (reserverName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your full name.");
            return;
        }

        String department = (String) departmentBox.getSelectedItem();
        if (department == null || department.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a department.");
            return;
        }

        String roomType = selectedRoom.getRoomType();
        if (roomType == null || roomType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room type information missing.");
            return;
        }

        java.util.Date startTimeUtil = (java.util.Date) startTimeSpinner.getValue();
        java.util.Date endTimeUtil = (java.util.Date) endTimeSpinner.getValue();

        java.sql.Date sqlStartDate = new java.sql.Date(start.getTime());
        java.sql.Date sqlEndDate = new java.sql.Date(end.getTime());
        java.sql.Time sqlStartTime = new java.sql.Time(startTimeUtil.getTime());
        java.sql.Time sqlEndTime = new java.sql.Time(endTimeUtil.getTime());

        // Optional validation
        if (sqlEndDate.equals(sqlStartDate) && sqlEndTime.before(sqlStartTime)) {
            JOptionPane.showMessageDialog(this, "End time cannot be before start time on the same day.");
            return;
        }

        int roomId = selectedRoom.getId();

        ReservationDAO dao = new ReservationDAO();
        boolean success = dao.createReservation(
                user.getId(), reserverName, department, roomId,
                sqlStartDate, sqlEndDate, sqlStartTime, sqlEndTime, roomType
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "Reservation request submitted.");
            tableModel.setRowCount(0);
            loadUserReservations();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reserve. Room may be booked.");
        }
    }



    private void loadUserReservations() {
        tableModel.setRowCount(0);
        ReservationDAO dao = new ReservationDAO();
        List<Reservation> reservations = dao.getUserReservations(user.getId());

        for (Reservation r : reservations) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getReserverName(),
                    r.getDepartment(),
                    r.getRoom().getRoomNumber(),
                    r.getRoomType(),
                    r.getStartDate(),
                    r.getEndDate(),
                    r.getStartTime(),
                    r.getEndTime(),
                    r.getStatus()
            });
        }
    }

}
