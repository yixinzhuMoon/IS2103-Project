/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.entity.stateful.RoomReservationSessionBeanRemote;
import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.Guest;
import entity.NormalRate;
import entity.OnlineReservation;
import entity.Partner;
import entity.PartnerReservation;
import entity.PeakRate;
import entity.PromotionRate;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import util.exception.GuestNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author zhu yixin
 */
@Stateful
@Remote(RoomReservationSessionBeanRemote.class)
@Local(RoomReservationSessionBeanLocal.class)
public class RoomReservationSessionBean implements RoomReservationSessionBeanRemote, RoomReservationSessionBeanLocal {
    
    
    

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

   
    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private RoomControllerLocal roomControllerLocal;

    @EJB
    private ReservationControllerLocal reservationControllerLocal;
    
    

    private List<ReservationLineItem> reservationLineItems;
    private HashMap<RoomType,BigDecimal> typeAndPrice=new HashMap<>();
    private HashMap<RoomType,Integer> typeAndroomLeft=new HashMap<>();
    

    @Override
    public void searchHotelRoom(Date checkInDate,Date checkOutDate)
    {
        List<RoomType> allRoomTypeAvailable= new ArrayList<>();
        Integer roomLeft=0;
        
        for(RoomType roomType: roomTypeControllerLocal.retrieveAllRoomTypes())
        {
           if(roomType.getStatus().equals("enabled"))
           {
               allRoomTypeAvailable.add(roomType);
           }   
        }
        
        for(RoomType roomType: allRoomTypeAvailable)
        {
            roomLeft=roomType.getRooms().size();
            for(ReservationLineItem reservationLineItem:roomType.getReservationLineItems())
            {
                if(!isWithinRange(reservationLineItem.getCheckInDate(), reservationLineItem.getCheckOutDate(), checkInDate, checkOutDate))
                {
                    roomLeft--;
                }
            }
            
            for(Room room: roomType.getRooms())
            {
                if(!room.getRoomStatus().equals("available"))
                {
                    roomLeft--;
                }
            }
            
            if(roomLeft>0)
            {
                Boolean normalRateInNeeded=false;
                Boolean promotionRateInNeeded=false;
                Boolean peakRateInNeeded=false;
                
                for(RoomRate roomRate: roomType.getRoomRates())
                {
                    if(roomRate instanceof PromotionRate)
                    {
                        promotionRateInNeeded=true;
                    }
                    else if(roomRate instanceof NormalRate)
                    {
                        normalRateInNeeded=true;
                    }
                    else if(roomRate instanceof PeakRate)
                    {
                        peakRateInNeeded=true;
                    }
                }
                
                BigDecimal price=BigDecimal.ZERO;
                
                if(peakRateInNeeded&&promotionRateInNeeded&&normalRateInNeeded)
                {
                    for(RoomRate roomRate: roomType.getRoomRates())
                    {
                        if(roomRate instanceof PromotionRate)
                        {
                            price=roomRate.getRatePerNight();
                        }
                    }
                }
                else if(peakRateInNeeded&&promotionRateInNeeded)
                {
                    for(RoomRate roomRate: roomType.getRoomRates())
                    {
                        if(roomRate instanceof PeakRate)
                        {
                            price=roomRate.getRatePerNight();
                        }
                    }
                }
                else if(peakRateInNeeded&&normalRateInNeeded)
                {
                    for(RoomRate roomRate: roomType.getRoomRates())
                    {
                        if(roomRate instanceof PeakRate)
                        {
                            price=roomRate.getRatePerNight();
                        }
                    }
                }
                else if(normalRateInNeeded&&promotionRateInNeeded)
                {
                    for(RoomRate roomRate: roomType.getRoomRates())
                    {
                        if(roomRate instanceof PromotionRate)
                        {
                            price=roomRate.getRatePerNight();
                        }
                    }
                }
                    else if(normalRateInNeeded)
                {
                    for(RoomRate roomRate: roomType.getRoomRates())
                    {
                        if(roomRate instanceof NormalRate)
                        {
                            price=roomRate.getRatePerNight();
                        }
                    }
                }
                else if(promotionRateInNeeded)
                {
                    for(RoomRate roomRate: roomType.getRoomRates())
                    {
                        if(roomRate instanceof PromotionRate)
                        {
                            price=roomRate.getRatePerNight();
                        }
                    }
                }
                else if(peakRateInNeeded)
                {
                    for(RoomRate roomRate: roomType.getRoomRates())
                    {
                        if(roomRate instanceof PeakRate)
                        {
                            price=roomRate.getRatePerNight();
                        }
                    }
                }
                typeAndPrice.put(roomType, price);
                typeAndroomLeft.put(roomType, roomLeft);
             
                System.out.println(roomType.getName()+ "has "+roomLeft+"rooms left");
            }
        }   
    }
        
     
   
    @Override
    public Long totalAmount(String roomType, Integer roomNumber,Date checkInDate,Date checkOutDate)  
    {
            Query query=em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name=:inName");
            query.setParameter("inName", roomType);
            RoomType reserveRoomType=(RoomType) query.getSingleResult();
            Long totalAmount=new Long(0);
            
            if(roomNumber>typeAndroomLeft.get(roomType))
            {
                totalAmount=new Long(0);
            }
            else
            {
                totalAmount=((checkOutDate.getTime()-checkInDate.getTime())*typeAndPrice.get(roomType).longValue()*roomNumber);
            }  
            
            return totalAmount;
  
    }
    
    @Override
    public PartnerReservation reservePartnerRoom(String email,ReservationLineItem reservationRoom)
    {
        if(email!=null)
        {
            Query query=em.createQuery("SELECT p FROM Partner p WHERE p.email=:inEMail");
            query.setParameter("inEmail", email);
            Partner partner=(Partner) query.getSingleResult();
            
            PartnerReservation partnerReservation=new PartnerReservation();
            partnerReservation.getReservationLineItems().add(reservationRoom);
            partner.getPartnerReservations().add(partnerReservation);
            
            return partnerReservation;
        }
        else 
        {
            return null;
        }

    }

    public void persist(Object object) {
        em.persist(object);
    }
    
    public boolean isWithinRange(Date startDate, Date endDate,Date checkInDate, Date checkOutDate) {
        return !(startDate.after(checkInDate) || endDate.before(checkOutDate));
    }

}
