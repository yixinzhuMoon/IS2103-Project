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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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

    @EJB
    private RoomControllerLocal roomControllerLocal;
    
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
        query.setParameter("inEmail", email);
        
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
    public List<Room> checkOutGuest(Long guestId) throws GuestNotFoundException, TimeException
    {
        Date todayDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
        Guest guest = retrieveGuestById(guestId);
        if(guest!=null)
        {
            List<OnlineReservation> guestList = retrieveOnlineReservationListByGuest(guestId);
            List<Room> roomsCheckedOut = new ArrayList<>();
            for(OnlineReservation guestReservation:guestList){
                for(ReservationLineItem reservationLineItem:guestReservation.getReservationLineItems())
                {
                    try {
                        //today's date is reservation line item check out date && time is 12pm
                        if(sdf.format(todayDate).equals(sdf.format(reservationLineItem.getCheckOutDate())) &&
                                dateFormat.parse(dateFormat.format(todayDate)).equals(dateFormat.parse("12:00"))){
                            for(Room roomToCheckOut:reservationLineItem.getRoomList()){
                                if(roomToCheckOut.getRoomStatus().equals("occupied")){
                                    roomToCheckOut.setRoomStatus("available"); 
                                }
                                else if(roomToCheckOut.getRoomStatus().equals("occupied reserved")){
                                    roomToCheckOut.setRoomStatus("reserved");
                                }
                                roomToCheckOut.setReservation(null);
                                reservationLineItem.getRoomList().remove(roomToCheckOut);
                                roomsCheckedOut.add(roomToCheckOut);
                            }
                        }
                        else if(!dateFormat.parse(dateFormat.format(todayDate)).equals(dateFormat.parse("12:00")))
                        {
                            Iterator<Room> roomListIterator = reservationLineItem.getRoomList().iterator();
                            //if guest checks out late and room is not reserved by another incoming guest
                            while(roomListIterator.hasNext()){
                                Room room = roomListIterator.next();
                                if(room.getRoomStatus().equals("occupied")){
                                    room.setRoomStatus("available"); 
                                    room.setReservation(null);
                                    roomListIterator.remove(); //reservationLineItem.getRoomList().remove(room)
                                    roomsCheckedOut.add(room);
                                }
                            }
                        }
                        else{
                            throw new TimeException("Check out of guest only happens at 12pm on the day of departure.");
                        }
                    } catch (ParseException ex) {
                        System.out.println("Invalid Date Format entered!" + "\n");
                    }
                }
            } 
            return roomsCheckedOut;
        }
        else
        {
            throw new GuestNotFoundException("Guest " + guestId + " does not exist! \n");
        }
    }
    
    @Override
    public List<Room> checkInGuest(Long guestId) throws GuestNotFoundException, TimeException
    {
        Date todayDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
        Guest guest = retrieveGuestById(guestId);
        if(guest!=null){
            List<Room> roomsCheckedIn = new ArrayList<>();
            List<OnlineReservation> guestList = retrieveOnlineReservationListByGuest(guestId);
            for(OnlineReservation guestReservation:guestList){
                for(ReservationLineItem reservationLineItem:guestReservation.getReservationLineItems())
                {
                    try{
                        //today's date is reservation line item check in date && time is 2pm
                        if(sdf.format(todayDate).equals(sdf.format(reservationLineItem.getCheckInDate())) &&
                                dateFormat.parse(dateFormat.format(todayDate)).equals(dateFormat.parse("14:00")))
                        {
                            for(Room roomToCheckIn:reservationLineItem.getRoomList()) //list of reserved rooms in reservation line item
                            {
                                roomToCheckIn.setRoomStatus("occupied");
                                roomToCheckIn.setReservation(reservationLineItem);
                                roomsCheckedIn.add(roomToCheckIn);
                            }
                        }
                        else if(!dateFormat.parse(dateFormat.format(todayDate)).equals(dateFormat.parse("14:00")))
                        {
                            //current time before 2pm and room is available
                            for(Room roomToCheckIn:reservationLineItem.getRoomList())
                            {
                                if(roomToCheckIn.getRoomStatus().equals("reserved")){
                                    roomToCheckIn.setRoomStatus("occupied");
                                    roomToCheckIn.setReservation(reservationLineItem);
                                    roomsCheckedIn.add(roomToCheckIn);
                                }
                            }
                        }
                        else
                        {
                            throw new TimeException("Check in of guest only happens at 2pm on the day of arrival unless room is available before then.");
                        }
                    }
                    catch(ParseException ex)
                    {
                        System.out.println("Invalid Date Format entered!" + "\n");
                    }
                }
            }
            System.out.println("");
            return roomsCheckedIn;
        }
        else
        {
            throw new GuestNotFoundException("Guest " + guestId + " does not exist! \n");
        }
    }
}
