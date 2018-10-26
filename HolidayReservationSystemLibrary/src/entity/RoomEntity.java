/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author casseylow
 */
@Entity
public class RoomEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(length = 4)
    private Integer roomNumber; // room floor + room number
    @Column(nullable = false)
    private String roomStatus;
    @Temporal(TemporalType.DATE)
    private Date checkInDate;
    @Temporal(TemporalType.DATE)
    private Date checkOutDate;
    

    public RoomEntity() {
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomNumber != null ? roomNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomNumber fields are not set
        if (!(object instanceof RoomEntity)) {
            return false;
        }
        RoomEntity other = (RoomEntity) object;
        if ((this.roomNumber == null && other.roomNumber != null) || (this.roomNumber != null && !this.roomNumber.equals(other.roomNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomEntity[ id=" + roomNumber + " ]";
    }
    
}
