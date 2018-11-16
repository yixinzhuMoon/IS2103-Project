/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.entity.stateful;

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
        List<NormalRate> validNormalRates = new ArrayList<>();
        List<PromotionRate> validPromotionRates = new ArrayList<>();
        List<PeakRate> validPeakRates = new ArrayList<>();
        List<Room> validRooms = new ArrayList<>();
        reservationLineItems = new ArrayList<>();
        
        String indicate="";
        
        
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-YYYY");
        Date BeginNormalRate=null;
        Date EndNormalRate=null;
        Date BeginPromotionRate=null;
        Date EndPromotionRate=null;
        Date BeginPeakRate=null;
        Date EndPeakRate=null;
        Date Begin=null;
        Date End=null;
        
        try
        {
            BeginNormalRate=df.parse(normalRate.getStartDate().toString());
            EndNormalRate=df.parse(normalRate.getEndDate().toString());
            BeginPromotionRate=df.parse(promotionRate.getStartDate().toString());
            EndPromotionRate=df.parse(promotionRate.getEndDate().toString());
            BeginPeakRate=df.parse(peakRate.getStartDate().toString());
            EndPeakRate=df.parse(peakRate.getEndDate().toString());
            Begin=df.parse(checkInDate.toString());
            End=df.parse(checkOutDate.toString());
            
        } 
        catch (ParseException ex) 
        {
            ex.printStackTrace();
        }
        
        String roomRate="";
        
        if(belongCalendar(Begin, BeginNormalRate, EndNormalRate)&&belongCalendar(End, BeginNormalRate, EndNormalRate))
        {
            roomRate="NormalRate";
        }
        else if(belongCalendar(Begin, BeginPromotionRate, EndPromotionRate)&&belongCalendar(End, BeginPromotionRate, EndPromotionRate))
        {
            roomRate="PromotionRate";
        }
        else if(belongCalendar(Begin, BeginPeakRate, EndPeakRate)&&belongCalendar(End, BeginPeakRate, EndPeakRate))
        {
            roomRate="PeakRate";
        }
        else if((belongCalendar(Begin, BeginNormalRate, EndNormalRate)&&belongCalendar(End, BeginPromotionRate, EndPromotionRate))||(belongCalendar(End, BeginNormalRate, EndNormalRate)&&belongCalendar(Begin, BeginPromotionRate, EndPromotionRate)))
        {
            roomRate="PromotionRate";
        }
        else if((belongCalendar(Begin, BeginNormalRate, EndNormalRate)&&belongCalendar(End, BeginPeakRate, EndPeakRate))||(belongCalendar(End, BeginNormalRate, EndNormalRate)&&belongCalendar(Begin, BeginPeakRate, EndPeakRate)))
        {
            roomRate="PeakRate";
        }
        else if((belongCalendar(Begin, BeginPromotionRate, EndPromotionRate)&&belongCalendar(Begin, BeginPeakRate, EndPeakRate))||(belongCalendar(End, BeginPromotionRate, EndPromotionRate)&&belongCalendar(Begin, BeginPeakRate, EndPeakRate)))
        {
            roomRate="PeakRate";
        }
        else if((belongCalendar(Begin, BeginNormalRate, EndNormalRate)&&belongCalendar(Begin, BeginPromotionRate, EndPromotionRate)&&belongCalendar(Begin, BeginPeakRate, EndPeakRate))||(belongCalendar(End, BeginNormalRate, EndNormalRate)&&belongCalendar(End, BeginPromotionRate, EndPromotionRate)&&belongCalendar(End, BeginPeakRate, EndPeakRate)))
        {
            roomRate="PeakRate";
        }

        
        //store the room types of published rates within check in and check out date
        if(roomRate.equals("NormalRate"))
        {
            for(NormalRate normalRate:allNormalRateAvailable)
            {
                String roomTypeName = normalRate.getRoomType().getName();
                if(!validRoomTypes.contains(roomTypeName))
                {
                    validRoomTypes.add(roomTypeName);
                    validNormalRates.add(normalRate);
                }
            }
        }
        else if(roomRate.equals("PromotionRate"))
        {
            for(PromotionRate promotionRate:allPromotionRateAvailable)
            {
                String roomTypeName = promotionRate.getRoomType().getName();
                if(!validRoomTypes.contains(roomTypeName))
                {
                    validRoomTypes.add(roomTypeName);
                    validPromotionRates.add(promotionRate);
                }
            }
        }
        else if(roomRate.equals("PeakRate"))
        {
            for(PeakRate peakRate:allPeakRateAvailable)
            {
                String roomTypeName = peakRate.getRoomType().getName();
                if(!validRoomTypes.contains(roomTypeName))
                {
                    validRoomTypes.add(roomTypeName);
                    validPeakRates.add(peakRate);
                }
            }
        }
        
        //create reservation line items belonging to valid room type
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
        //calculate total amount for published rate within check in date and checkout date
        return reservationLineItems;
        
       
    }
        
     private boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
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
    
    
}
