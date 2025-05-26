package dao;

import model.Room;
import model.Reservation;
import model.ReservationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean createReservation(int userId, String reserverName, String department, int roomId,
                                     Date startDate, Date endDate, Time startTime, Time endTime,
                                     String roomType) {

        String checkSql = "SELECT COUNT(*) FROM reservation " +
                "WHERE room_id=? AND status='approved' AND " +
                "((start_date < ? OR (start_date = ? AND start_time <= ?)) " +
                " AND (end_date > ? OR (end_date = ? AND end_time >= ?)))";

        String insertSql = "INSERT INTO reservation " +
                "(user_id, reserver_name, department, room_id, start_date, end_date, start_time, end_time, status, room_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            // Conflict check parameters
            checkStmt.setInt(1, roomId);
            checkStmt.setDate(2, endDate);
            checkStmt.setDate(3, endDate);
            checkStmt.setTime(4, endTime);
            checkStmt.setDate(5, startDate);
            checkStmt.setDate(6, startDate);
            checkStmt.setTime(7, startTime);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Room is already booked
            }

            // Insert parameters
            insertStmt.setInt(1, userId);
            insertStmt.setString(2, reserverName);
            insertStmt.setString(3, department);
            insertStmt.setInt(4, roomId);
            insertStmt.setDate(5, startDate);
            insertStmt.setDate(6, endDate);
            insertStmt.setTime(7, startTime);
            insertStmt.setTime(8, endTime);
            insertStmt.setString(9, "pending"); // default status
            insertStmt.setString(10, roomType);

            return insertStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Updated getUserReservations to read start_time and end_time
    public List<Reservation> getUserReservations(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.user_id, r.reserver_name, r.department, r.room_id, r.start_date, r.end_date, r.start_time, r.end_time, r.status, r.room_type, rm.room_number " +
                "FROM reservation r " +
                "JOIN room rm ON r.room_id = rm.room_id " +
                "WHERE r.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));

                r.setId(rs.getInt("reservation_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setReserverName(rs.getString("reserver_name"));
                r.setDepartment(rs.getString("department"));
                r.setRoom(room);
                r.setRoomType(rs.getString("room_type"));
                r.setStartDate(rs.getDate("start_date"));
                r.setEndDate(rs.getDate("end_date"));
                r.setStartTime(rs.getTime("start_time"));
                r.setEndTime(rs.getTime("end_time"));
                r.setStatus(rs.getString("status"));

                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Updated getAllReservations to read start_time and end_time
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.reserver_name, r.department, r.room_id, rm.room_number, r.start_date, r.end_date, r.start_time, r.end_time, r.status, r.room_type " +
                "FROM reservation r " +
                "JOIN room rm ON r.room_id = rm.room_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReservationDTO r = new ReservationDTO();
                r.setId(rs.getInt("reservation_id"));
                r.setReserverName(rs.getString("reserver_name"));
                r.setDepartment(rs.getString("department"));
                r.setStartDate(rs.getDate("start_date"));
                r.setEndDate(rs.getDate("end_date"));
                r.setStartTime(rs.getTime("start_time"));
                r.setEndTime(rs.getTime("end_time"));
                r.setStatus(rs.getString("status"));
                r.setRoomType(rs.getString("room_type"));

                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                r.setRoom(room);

                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    public boolean updateReservationStatus(int reservationId, String status) {
        String sql = "UPDATE reservation SET status = ? WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean createReservation(int userId, int roomId, String roomType, Date startDate, Date endDate) {
    String query = "INSERT INTO reservation (user_id, room_id, room_type, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, 'Pending')";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, userId);
        stmt.setInt(2, roomId);
        stmt.setString(3, roomType);
        stmt.setDate(4, startDate);
        stmt.setDate(5, endDate);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}
