/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.entity.stateful.WalkInReservationSessionBeanRemote;
import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.Employee;
import entity.NormalRate;
import entity.PeakRate;
import entity.PromotionRate;
import entity.PublishedRate;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import entity.WalkInReservation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author casseylow
 */
@Stateful
@Local(WalkInReservationSessionBeanLocal.class)
@Remote(WalkInReservationSessionBeanRemote.class)
public class WalkInReservationSessionBean implements WalkInReservationSessionBeanRemote, WalkInReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private RoomControllerLocal roomControllerLocal;
    
    @EJB 
    private ReservationControllerLocal reservationControllerLocal;
    
    @EJB 
    private RoomTypeControllerLocal roomTypeControllerLocal;
    
    
    @Override
    public String chooseRoomRate(String roomType, Date checkInDate,Date checkOutDate)
    {
        Query query=em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name=:inName");
        query.setParameter("inName", roomType);
        RoomType reserveRoomType=(RoomType) query.getSingleResult();
        
        Boolean publishedRateInNeeded=false;
        
        for(RoomRate roomRate:reserveRoomType.getRoomRates())
        {
            if(roomRate instanceof PublishedRate)
            {
                publishedRateInNeeded=true;
            }
        }
        
        String chooseRomeRate="";
        if(publishedRateInNeeded)
        {
            for(RoomRate roomRate: reserveRoomType.getRoomRates())
            {
                if(roomRate instanceof PublishedRate)
                {
                    chooseRomeRate="PublishedRate";
                }
                break;
            }
        }
        
        return chooseRomeRate;
    }
        
    
    
    @Override
    public Long totalAmount(String roomType, Integer numberOfRooms,Date checkInDate,Date checkOutDate)  
    {
            Query query=em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name=:inName");
            query.setParameter("inName", roomType);
            RoomType reserveRoomType=(RoomType) query.getSingleResult();
            Long totalAmount=new Long(0);
            BigDecimal price=BigDecimal.ZERO;
            
            String chooseRoomRate=chooseRoomRate(roomType, checkInDate, checkOutDate);
            if(chooseRoomRate.equals("PublishedRate"))
            {
                for(RoomRate roomRate:reserveRoomType.getRoomRates())
                {
                    if(roomRate instanceof PublishedRate)
                    {
                        price=roomRate.getRatePerNight();
                    }
                    break;
                }
            }
            
            if(numberOfRooms>reserveRoomType.getRooms().size())
            {
                totalAmount=new Long(0);
            }
            else
            {
                if(checkOutDate.getTime()==checkInDate.getTime())
                {
                    totalAmount=price.longValue()*numberOfRooms;
                }
                else
                {
                totalAmount=((checkOutDate.getTime()-checkInDate.getTime())*price.longValue()*numberOfRooms);
                }
            }  
            
            return totalAmount;  
    }
    
    
}
