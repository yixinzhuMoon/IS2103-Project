/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author casseylow
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)

public abstract class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long roomRateId;
    @Column(nullable=false)
    protected String name;
//    private String rateType; //removed since we already have inheritance classes
    @Column(nullable=false)
    protected BigDecimal ratePerNight;
    @Column(nullable = false)
    protected String roomRateStatus;
    @ManyToOne(optional=false)
    @JoinColumn(nullable=false)
    protected RoomType roomType;

    public RoomRate() {
    }

    public RoomRate(String name, BigDecimal ratePerNight, RoomType roomType, String roomRateStatus) {
        this();
        this.name = name;
        this.ratePerNight = ratePerNight;
        this.roomType = roomType;
        this.roomRateStatus = roomRateStatus;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public String getRoomRateStatus() {
        return roomRateStatus;
    }

    public void setRoomRateStatus(String roomRateStatus) {
        this.roomRateStatus = roomRateStatus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRateEntity[ id=" + roomRateId + " ]";
    }
    
}