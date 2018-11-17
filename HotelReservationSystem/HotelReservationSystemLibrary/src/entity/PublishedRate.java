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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author casseylow
 */
@Entity
public class PublishedRate extends RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Temporal(TemporalType.DATE)
    @Column(nullable=true)
    private Date validity; 
    
    public PublishedRate(){
        super();
    }

    public PublishedRate(String name, BigDecimal ratePerNight, RoomType roomType, Date validity, String roomRateStatus) {
        super(name, ratePerNight, roomType, roomRateStatus);
        this.validity = validity;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
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
        if (!(object instanceof PublishedRate)) {
            return false;
        }
        PublishedRate other = (PublishedRate) object;
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
