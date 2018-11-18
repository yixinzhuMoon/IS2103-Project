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


   
    
    @Override
    public String chooseRoomRate(String roomType, Date checkInDate,Date checkOutDate)
    {
        
        Query query=em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name=:inName");
        query.setParameter("inName", roomType);
        RoomType reserveRoomType=(RoomType) query.getSingleResult();
        
        Boolean normalRateInNeeded=false;
        Boolean promotionRateInNeeded=false;
        Boolean peakRateInNeeded=false;
        
        for(RoomRate roomRate:reserveRoomType.getRoomRates())
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
        
        String chooseRomeRate="";
        if(peakRateInNeeded&&promotionRateInNeeded&&normalRateInNeeded)
                {
                    for(RoomRate roomRate: reserveRoomType.getRoomRates())
                    {
                        if(roomRate instanceof PromotionRate)
                        {
                            chooseRomeRate="PromotionRate";
                        }
                        break;
                    }
                }
                else if(peakRateInNeeded&&promotionRateInNeeded)
                {
                    for(RoomRate roomRate: reserveRoomType.getRoomRates())
                    {
                        if(roomRate instanceof PeakRate)
                        {
                            chooseRomeRate="PeakRate";
                        }
                        break;
                    }
                }
                else if(peakRateInNeeded&&normalRateInNeeded)
                {
                    for(RoomRate roomRate: reserveRoomType.getRoomRates())
                    {
                        if(roomRate instanceof PeakRate)
                        {
                            chooseRomeRate="PeakRate";
                        }
                        break;
                    }
                }
                else if(normalRateInNeeded&&promotionRateInNeeded)
                {
                    for(RoomRate roomRate: reserveRoomType.getRoomRates())
                    {
                        if(roomRate instanceof PromotionRate)
                        {
                            chooseRomeRate="PromotionRate";
                        }
                        break;
                    }
                }
                    else if(normalRateInNeeded)
                {
                    for(RoomRate roomRate: reserveRoomType.getRoomRates())
                    {
                        if(roomRate instanceof NormalRate)
                        {
                            chooseRomeRate="NormalRate";
                        }
                        break;
                    }
                }
                else if(promotionRateInNeeded)
                {
                    for(RoomRate roomRate: reserveRoomType.getRoomRates())
                    {
                        if(roomRate instanceof PromotionRate)
                        {
                            chooseRomeRate="PromotionRate";
                        }
                        break;
                    }
                }
                else if(peakRateInNeeded)
                {
                    for(RoomRate roomRate: reserveRoomType.getRoomRates())
                    {
                        if(roomRate instanceof PeakRate)
                        {
                            chooseRomeRate="PeakRate";
                        }
                        break;
                    }
                }
        
        return chooseRomeRate;
    }
        
     
   
    @Override
    public Long totalAmount(String roomType, Integer roomNumber,Date checkInDate,Date checkOutDate)  
    {

            Query query=em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name=:inName");
            query.setParameter("inName", roomType);
            RoomType reserveRoomType=(RoomType) query.getSingleResult();
            Long totalAmount=new Long(0);
            BigDecimal price=BigDecimal.ZERO;
            
            String chooseRoomRate=chooseRoomRate(roomType, checkInDate, checkOutDate);
            if(chooseRoomRate.equals("PromotionRate"))
            {
                for(RoomRate roomRate:reserveRoomType.getRoomRates())
                {
                    if(roomRate instanceof PromotionRate)
                    {
                        price=roomRate.getRatePerNight();
                    }
                    break;
                }
            }
            else if(chooseRoomRate.equals("PeakRate"))
            {
                for(RoomRate roomRate:reserveRoomType.getRoomRates())
                {
                    if(roomRate instanceof PeakRate)
                    {
                        price=roomRate.getRatePerNight();
                    }
                    break;
                }
            }
            else if(chooseRoomRate.equals("NormalRate"))
            {
                for(RoomRate roomRate:reserveRoomType.getRoomRates())
                {
                    if(roomRate instanceof NormalRate)
                    {
                        price=roomRate.getRatePerNight();
                    }
                    break;
                }
            }
            
            if(roomNumber>reserveRoomType.getRooms().size())
            {
                totalAmount=new Long(0);
            }
            else
            {
                if(checkOutDate.getTime()==checkInDate.getTime())
                {
                    totalAmount=price.longValue()*roomNumber;
                }
                else
                {
                totalAmount=((checkOutDate.getTime()-checkInDate.getTime())*price.longValue()*roomNumber);
                }
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
    

}
