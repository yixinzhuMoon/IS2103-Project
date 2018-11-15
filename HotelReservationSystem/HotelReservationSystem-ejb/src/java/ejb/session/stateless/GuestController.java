/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import com.sun.xml.wss.util.DateUtils;
import entity.Guest;
import entity.OnlineReservation;
import entity.ReservationLineItem;
import entity.Room;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.TimeException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(GuestControllerLocal.class)
@Remote(GuestControllerRemote.class)
public class GuestController implements GuestControllerRemote, GuestControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public GuestController(){
        
    }
    
    @Override
    public Guest guestLogin(String email,String password) throws InvalidLoginCredentialException,GuestNotFoundException
    {
       try
       {
           Guest newGuest=retrieveGuestByEmail(email);
           if(newGuest.getPassword().equals(password))
           {
              newGuest.getOnlineReservations().size();
              return newGuest;
           }
           else 
           {
               throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
           }
       }
       catch(GuestNotFoundException ex)
       {
            throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
       }
        
    }
    
    @Override
     public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException
    {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.guestId = :inGuestId");
        query.setParameter("inGuestId", guestId);
        
        try
        {
            return (Guest)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new GuestNotFoundException("Guest guest id " + guestId + " does not exist!");
        }
    }

    
    @Override
     public Guest retrieveGuestByEmail(String email) throws GuestNotFoundException
    {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email = :inEmail");
        query.setParameter("inUsername", email);
        
        try
        {
            return (Guest)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new GuestNotFoundException("Guest email " + email + " does not exist!");
        }
    }

    @Override
    public Long createGuest(Guest newGuest)
    {
        em.persist(newGuest);
        em.flush();
        
        return newGuest.getGuestId();
    }
    
    @Override
    public List<OnlineReservation> retrieveOnlineReservationListByGuest(Long guestId) throws GuestNotFoundException
    {
        Query query = em.createQuery("SELECT o FROM OnlineReservation o WHERE o.guest = :inGuest");
        query.setParameter("inGuest", retrieveGuestById(guestId));
        return query.getResultList();
    }
    
    @Override
    public void checkOutGuest(Long guestId) throws GuestNotFoundException
    {
        Date todayDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
        List<OnlineReservation> guestList = retrieveOnlineReservationListByGuest(guestId);
        for(OnlineReservation guestReservation:guestList){
            for(ReservationLineItem reservationLineItem:guestReservation.getReservationLineItems())
            {
                try {
                    //today's date is reservation line item check out date && time is 12pm
                    if(sdf.format(todayDate).equals(sdf.format(reservationLineItem.getCheckOutDate())) &&
                            dateFormat.parse(dateFormat.format(todayDate)).equals(dateFormat.parse("12:00"))){
                        for(Room roomToCheckOut:reservationLineItem.getRoomList()){
                            roomToCheckOut.setRoomStatus("available");
                            roomToCheckOut.setReservation(null);
                        }
                    }else{
                        System.out.println("Check out of guest only happens at 12pm on the day of departure.");
                    }
                } catch (ParseException ex) {
                    System.out.println("Invalid Date Format entered!" + "\n");
                }
            }
        }
    }
    
    @Override
    public void checkInGuest(Long guestId)
    {
        try {
            Date todayDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
            List<OnlineReservation> guestList = retrieveOnlineReservationListByGuest(guestId);
            for(OnlineReservation guestReservation:guestList){
                for(ReservationLineItem reservationLineItem:guestReservation.getReservationLineItems())
                {
                    //today's date is reservation line item check out date && time is 12pm
                    if(sdf.format(todayDate).equals(sdf.format(reservationLineItem.getCheckInDate())) &&
                            dateFormat.parse(dateFormat.format(todayDate)).equals(dateFormat.parse("14:00"))){
                        for(Room roomToCheckIn:reservationLineItem.getRoomList()){
                            roomToCheckIn.setRoomStatus("occupied");
                            roomToCheckIn.setReservation(reservationLineItem);
                            em.merge(reservationLineItem);
                        }
                    }
                    else if(!dateFormat.parse(dateFormat.format(todayDate)).equals(dateFormat.parse("14:00")))
                    {
                        //current time before 12pm and room is available
                        for(Room roomToCheckIn:reservationLineItem.getRoomList()){
                            if(roomToCheckIn.getRoomStatus().equals("available")){
                                roomToCheckIn.setRoomStatus("occupied");
                                roomToCheckIn.setReservation(reservationLineItem);
                                em.merge(reservationLineItem);
                            }
                        }
                    }
                    else
                    {
                        throw new TimeException("Check in of guest only happens at 2pm on the day of arrival unless room is available before then.");
                    }
                }
            }
            System.out.println("");
        } 
        catch (GuestNotFoundException ex) 
        {
            System.out.println("Guest " + guestId + " does not exist: " + ex.getMessage() + "\n");
        }
        catch (ParseException ex)
        {
            System.out.println("Invalid Date Format entered!" + "\n");
        } 
        catch (TimeException ex) 
        {
            System.out.println("Check in of guest only happens at 2pm on the day of arrival unless room is available before then.\n");
        }
    }
}
