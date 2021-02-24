package com.example.wedo.Chating.model;

public class RoomData {
    private String username;
    private String roomNumber;
    private String roomOrder;

    public RoomData(String username, String roomNumber, String roomOrder) {
        this.username = username;
        this.roomNumber = roomNumber;
        this.roomOrder = roomOrder;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomOrder() {
        return roomOrder;
    }

    public void setRoomOrder(String roomOrder) {
        this.roomOrder = roomOrder;
    }
}
