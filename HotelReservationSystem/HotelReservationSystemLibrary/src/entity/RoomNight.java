/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author casseylow
 */
@Entity
public class RoomNight implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomNightId;
    @Column(nullable=false)
    private String effectiveDay; //monday or tuesday or wednesday,etc.
    @OneToMany
    private List<RoomRate> roomRates;

    public RoomNight() {
        roomRates = new ArrayList<>();
    }

    public RoomNight(Long roomNightId, String day) {
        this.roomNightId = roomNightId;
        this.effectiveDay = day;
    }

    public String getEffectiveDay() {
        return effectiveDay;
    }

    public void setEffectiveDay(String effectiveDay) {
        this.effectiveDay = effectiveDay;
    }

    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    public Long getRoomNightId() {
        return roomNightId;
    }

    public void setRoomNightId(Long roomNightId) {
        this.roomNightId = roomNightId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomNightId != null ? roomNightId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomNightId fields are not set
        if (!(object instanceof RoomNight)) {
            return false;
        }
        RoomNight other = (RoomNight) object;
        if ((this.roomNightId == null && other.roomNightId != null) || (this.roomNightId != null && !this.roomNightId.equals(other.roomNightId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomNight[ id=" + roomNightId + " ]";
    }
    
}
