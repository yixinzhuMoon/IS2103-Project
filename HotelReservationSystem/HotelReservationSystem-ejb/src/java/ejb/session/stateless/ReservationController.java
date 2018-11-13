/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationLineItem;
import entity.RoomType;
import java.util.Date;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(ReservationControllerLocal.class)
@Remote(ReservationControllerRemote.class)
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public ReservationLineItem createReservationLineItem(Date checkInDate,Date checkOutDate,int roomType)
    {
        ReservationLineItem reservationLineItem=new ReservationLineItem();
        reservationLineItem.setCheckInDate(checkInDate);
        reservationLineItem.setCheckOutDate(checkOutDate);
        
        RoomType roomTypeItem=new RoomType();
        if(roomType==1)
        {
            roomTypeItem.setName("Deluxe Room");
            reservationLineItem.setRoomType(roomTypeItem);
        }
        else if(roomType==2)
        {
            roomTypeItem.setName("Permier Room");
            reservationLineItem.setRoomType(roomTypeItem);
        }
        else if(roomType==3)
        {
            roomTypeItem.setName("Family Room");
            reservationLineItem.setRoomType(roomTypeItem);
        }
        else if(roomType==4)
        {
            roomTypeItem.setName("Junior Suite");
            reservationLineItem.setRoomType(roomTypeItem);
        }
        else if(roomType==5)
        {
            roomTypeItem.setName("Grand Suite");
            reservationLineItem.setRoomType(roomTypeItem);
        }
                
        em.persist(reservationLineItem);
        em.flush();
        
        return reservationLineItem;
    }

}
