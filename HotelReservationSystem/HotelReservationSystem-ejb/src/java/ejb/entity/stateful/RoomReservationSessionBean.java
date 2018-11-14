/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.entity.stateful;

import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.Guest;
import entity.NormalRate;
import entity.OnlineReservation;
import entity.PeakRate;
import entity.PromotionRate;
import entity.ReservationLineItem;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/**
 *
 * @author zhu yixin
 */
@Stateful
@Remote(RoomReservationSessionBeanRemote.class)
@Local(RoomReservationSessionBeanLocal.class)
public class RoomReservationSessionBean implements RoomReservationSessionBeanRemote, RoomReservationSessionBeanLocal {

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
        this.checkInDate=checkInDate;
        this.checkOutDate=checkOutDate;
        
        SimpleDateFormat df=new SimpleDateFormat("MM-dd");
        
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
        
        BigDecimal pricePerNight=BigDecimal.ZERO;
        String roomRate="";
        
        if(belongCalendar(Begin, BeginNormalRate, EndNormalRate)&&belongCalendar(End, BeginNormalRate, EndNormalRate))
        {
            pricePerNight=normalRate.getRatePerNight();
            roomRate="NormalRate";
        }
        else if(belongCalendar(Begin, BeginPromotionRate, EndPromotionRate)&&belongCalendar(End, BeginPromotionRate, EndPromotionRate))
        {
            pricePerNight=promotionRate.getRatePerNight();
            roomRate="PromotionRate";
        }
        else if(belongCalendar(Begin, BeginPeakRate, EndPeakRate)&&belongCalendar(End, BeginPeakRate, EndPeakRate))
        {
            pricePerNight=peakRate.getRatePerNight();
            roomRate="PeakRate";
        }
        else if((belongCalendar(Begin, BeginNormalRate, EndNormalRate)&&belongCalendar(End, BeginPromotionRate, EndPromotionRate))||(belongCalendar(End, BeginNormalRate, EndNormalRate)&&belongCalendar(Begin, BeginPromotionRate, EndPromotionRate)))
        {
            pricePerNight=promotionRate.getRatePerNight();
            roomRate="PromotionRate";
        }
        else if((belongCalendar(Begin, BeginNormalRate, EndNormalRate)&&belongCalendar(End, BeginPeakRate, EndPeakRate))||(belongCalendar(End, BeginNormalRate, EndNormalRate)&&belongCalendar(Begin, BeginPeakRate, EndPeakRate)))
        {
            pricePerNight=peakRate.getRatePerNight();
            roomRate="PeakRate";
        }
        else if((belongCalendar(Begin, BeginPromotionRate, EndPromotionRate)&&belongCalendar(Begin, BeginPeakRate, EndPeakRate))||(belongCalendar(End, BeginPromotionRate, EndPromotionRate)&&belongCalendar(Begin, BeginPeakRate, EndPeakRate)))
        {
            pricePerNight=peakRate.getRatePerNight();
            roomRate="PeakRate";
        }
        else if((belongCalendar(Begin, BeginNormalRate, EndNormalRate)&&belongCalendar(Begin, BeginPromotionRate, EndPromotionRate)&&belongCalendar(Begin, BeginPeakRate, EndPeakRate))||(belongCalendar(End, BeginNormalRate, EndNormalRate)&&belongCalendar(End, BeginPromotionRate, EndPromotionRate)&&belongCalendar(End, BeginPeakRate, EndPeakRate)))
        {
            pricePerNight=peakRate.getRatePerNight();
            roomRate="PeakRate";
        }
        
        totalAmount=(checkOutDate.getTime()-checkInDate.getTime())*pricePerNight.longValue();
        
        reservationLineItems.add(reservationControllerLocal.createReservationLineItem(checkInDate,checkInDate,1));
        reservationLineItems.add(reservationControllerLocal.createReservationLineItem(checkInDate,checkInDate,2));
        reservationLineItems.add(reservationControllerLocal.createReservationLineItem(checkInDate,checkInDate,3));
        reservationLineItems.add(reservationControllerLocal.createReservationLineItem(checkInDate,checkInDate,4));
        reservationLineItems.add(reservationControllerLocal.createReservationLineItem(checkInDate,checkInDate,5));
        
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
    
    
}
