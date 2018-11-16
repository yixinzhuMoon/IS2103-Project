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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private RoomControllerLocal roomControllerLocal;

    @EJB
    private ReservationControllerLocal reservationControllerLocal;
    
    

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private Date checkInDate;
    private Date checkOutDate;
    
    private NormalRate normalRate;
    private PromotionRate promotionRate;
    private PeakRate peakRate;
    
    private Long totalAmount;
    
    private List<ReservationLineItem> reservationLineItems;
    
    @Remove
    @Override
    public void remove()
    {
    }
    
    @PreDestroy
    @Override
    public void preDestroy()
    {
        if(reservationLineItems != null)
        {
            reservationLineItems.clear();
            reservationLineItems = null;
        }
    }
    
    @Override
    public List<ReservationLineItem> searchHotelRoom(Date checkInDate,Date checkOutDate)
    {
        List<Room> allRoomAvailable = roomControllerLocal.retrieveAllRooms();
        List<NormalRate> allNormalRateAvailable = roomRateControllerLocal.retrieveAllNormalRate();
        List<PromotionRate> allPromotionRateAvailable= roomRateControllerLocal.retrieveAllPromotionRate();
        List<PeakRate> allPeakRateAvailable= roomRateControllerLocal.retrieveAllPeakRate();
        List<String> validRoomTypes = new ArrayList<>();
        List<String> validNormalRoomTypes = new ArrayList<>();
        List<String> validPromotionRoomTypes = new ArrayList<>();
        List<String> validPeakRoomTypes = new ArrayList<>();
        List<NormalRate> validNormalRates = new ArrayList<>();
        List<PromotionRate> validPromotionRates = new ArrayList<>();
        List<PeakRate> validPeakRates = new ArrayList<>();
        List<Room> validRooms = new ArrayList<>();
        reservationLineItems = new ArrayList<>();
        
        
        

            for(NormalRate normalRate:allNormalRateAvailable)
            {
            if(isWithinRange(normalRate.getStartDate(),normalRate.getEndDate(), checkInDate, checkOutDate))
            {
                String roomTypeName = normalRate.getRoomType().getName();
                if(!validRoomTypes.contains(roomTypeName)){
                    validNormalRoomTypes.add(roomTypeName);
                    validNormalRates.add(normalRate);
                }
            }
            }

        
        for(PromotionRate promotionRate:allPromotionRateAvailable){
            if(isWithinRange(promotionRate.getStartDate(),promotionRate.getEndDate(), checkInDate, checkOutDate)){
                String roomTypeName = promotionRate.getRoomType().getName();
                if(!validRoomTypes.contains(roomTypeName)){
                    validPromotionRoomTypes.add(roomTypeName);
                    validPromotionRates.add(promotionRate);
                }
            }
        }
        
        for(PeakRate peakRate:allPeakRateAvailable){
            if(isWithinRange(peakRate.getStartDate(),peakRate.getEndDate(), checkInDate, checkOutDate)){
                String roomTypeName = peakRate.getRoomType().getName();
                if(!validRoomTypes.contains(roomTypeName)){
                    validPeakRoomTypes.add(roomTypeName);
                    validPeakRates.add(peakRate);
                }
            }
        }
        
       
        String roomRate="NormalRate";
        
        for(int a=0;a<validNormalRates.size();a++)
        {
            for(int b=0;b<validPromotionRoomTypes.size();b++)
            {
                for(int c=0;c<validPeakRoomTypes.size();c++)
                {
                    if(validPeakRoomTypes.get(c).equals(validPromotionRoomTypes.get(b))&&validPeakRoomTypes.get(c).equals(validNormalRoomTypes.get(a)))
                    {
                        roomRate="PromotionRate";
                    }
                    else if(validPeakRoomTypes.get(c).equals(validPromotionRoomTypes.get(b)))
                    {
                        roomRate="PeakRate";
                    }
                    else if(validPeakRoomTypes.get(c).equals(validNormalRoomTypes.get(a)))
                    {
                        roomRate="PeakRate";
                    }
                           
                }
                if(validNormalRoomTypes.get(a).equals(validPromotionRoomTypes.get(b)))
                {
                    roomRate="PromotionRate";
                }
            }
        }
        
       
        Integer counter = 0;
        for(Room room:allRoomAvailable){
            if(validRoomTypes.contains(room.getRoomType().getName())){
                try {
                    counter++;
                    validRooms.add(room);
                    if(roomRate.equals("NormalRate"))
                    {
                        totalAmount = (checkOutDate.getTime()-checkInDate.getTime())*validNormalRates.get(counter).getRatePerNight().longValue();
                        reservationLineItems.add(reservationControllerLocal.createRoomReservationLineItem(checkInDate, checkOutDate,
                            room.getRoomType().getRoomTypeId(), validNormalRates.get(counter).getRoomRateId()));
                    }
                    else if(roomRate.equals("PromotionRate"))
                    {
                        totalAmount = (checkOutDate.getTime()-checkInDate.getTime())*validPromotionRates.get(counter).getRatePerNight().longValue();
                        reservationLineItems.add(reservationControllerLocal.createRoomReservationLineItem(checkInDate, checkOutDate,
                            room.getRoomType().getRoomTypeId(), validPromotionRates.get(counter).getRoomRateId()));
                    }
                    else if(roomRate.equals("PeakRate"))
                    {
                        totalAmount = (checkOutDate.getTime()-checkInDate.getTime())*validPeakRates.get(counter).getRatePerNight().longValue();
                        reservationLineItems.add(reservationControllerLocal.createRoomReservationLineItem(checkInDate, checkOutDate,
                            room.getRoomType().getRoomTypeId(), validPeakRates.get(counter).getRoomRateId()));
                    }
                    
                } catch (RoomTypeNotFoundException | RoomRateNotFoundException ex) {
                    System.out.println("An error has occured while creating reservation line item");
                }
            }
        }
        
        return reservationLineItems;             
    }
        
     
    @Override
    public Long getTotalAmount(ReservationLineItem reservationLineItem) 
    {
        return totalAmount;
    }
    
    
    @Override
    public OnlineReservation reserveRoom(String email,ReservationLineItem reservationRoom) throws GuestNotFoundException
    {
        if(email!=null)
        {
            Query query=em.createQuery("SELECT g FROM Guest g WHERE g.email=:inEMail");
            query.setParameter("inEmail", email);
            Guest guest=(Guest) query.getSingleResult();
            
            OnlineReservation onlineReservation=new OnlineReservation();
            onlineReservation.getReservationLineItems().add(reservationRoom);
            guest.getOnlineReservations().add(onlineReservation);
            
            return onlineReservation;
        }
        else
        {
            throw new GuestNotFoundException("Missing customer data or no holiday to checkout");
        }
        
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
