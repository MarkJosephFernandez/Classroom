package model;

import java.sql.Date;
import java.sql.Time;

public class Reservation {
    private int id;             // reservation_id
    private int userId;         // user_id
    private String reserverName; // reserver_name
    private Room room;          // contains room_id internally
    private Date startDate;     // start_date
    private Date endDate;       // end_date
    private Time startTime;     // start_time
    private Time endTime;       // end_time
    private String status;      // status
    private String roomType;    // room_type
    private String department;

    public Reservation() {}

    public Reservation(int id, int userId, String reserverName, Room room,
                       Date startDate, Date endDate, Time startTime, Time endTime,
                       String status, String roomType, String department) {
        this.id = id;
        this.userId = userId;
        this.reserverName = reserverName;
        this.room = room;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.roomType = roomType;
        this.department = department;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReserverName() {
        return reserverName;
    }

    public void setReserverName(String reserverName) {
        this.reserverName = reserverName;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
