/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.entity.stateful;

import ejb.session.stateless.ReservationController;
import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import entity.Employee;
import entity.PublishedRate;
import entity.ReservationLineItem;
import entity.Room;
import entity.WalkInReservation;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

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
    
    private Long totalAmount;
    
    private List<Room> validRooms;

    @Remove
    @Override
    public void remove()
    {
        // Do nothing
    }
    
    @PreDestroy
    @Override
    public void preDestroy()
    {
        if(validRooms != null)
        {
            validRooms.clear();
            validRooms = null;
        }
    }
    
    @Override
    public List<ReservationLineItem> walkInSearchHotelRoom(Date checkInDate, Date checkOutDate){
        List<Room> allRoomAvailable = roomControllerLocal.retrieveAllRooms();
        List<PublishedRate> allPublishedRateAvailable = roomRateControllerLocal.retrieveAllPublishedRate();
        List<String> validRoomTypes = new ArrayList<>();
        List<PublishedRate> validPublishedRates = new ArrayList<>();
        List<ReservationLineItem> reservationLineItems = new ArrayList<>();
        validRooms = new ArrayList<>();
        
        //store the room types of published rates within check in and check out date
        for(PublishedRate pubRate:allPublishedRateAvailable){
            if(isWithinRange(pubRate.getValidity(), checkInDate, checkOutDate)){
                String roomTypeName = pubRate.getRoomType().getName();
                if(!validRoomTypes.contains(roomTypeName)){
                    validRoomTypes.add(roomTypeName);
                    validPublishedRates.add(pubRate);
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
                    totalAmount = (checkOutDate.getTime()-checkInDate.getTime())*validPublishedRates.get(counter).getRatePerNight().longValue();
                    reservationLineItems.add(reservationControllerLocal.createWalkInReservationLineItem(checkInDate, checkOutDate,
                            room.getRoomType().getRoomTypeId(), validPublishedRates.get(counter).getRoomRateId()));
                } catch (RoomTypeNotFoundException | RoomRateNotFoundException ex) {
                    System.out.println("An error has occured while creating reservation line item");
                }
            }
        }
        //calculate total amount for published rate within check in date and checkout date
        return reservationLineItems;
    }
    
    @Override
    public WalkInReservation reserveRoom(String email, ReservationLineItem reservationRoom) throws EmployeeNotFoundException
    {
        if(email!=null)
        {
            Query query=em.createQuery("SELECT e FROM Employee e WHERE e.email=:inEmail");
            query.setParameter("inEmail", email);
            Employee employee=(Employee) query.getSingleResult();
            
            WalkInReservation walkInReservation = new WalkInReservation();
            walkInReservation.getReservationLineItems().add(reservationRoom);
            employee.getWalkInReservations().add(walkInReservation);
            
            return walkInReservation;
        }
        else
        {
            throw new EmployeeNotFoundException("Employee data not found!");
        }
        
    }
    
    
     
    @Override
    public Long getTotalAmount() 
    {
        return totalAmount;
    }
    
    public boolean isWithinRange(Date testDate, Date checkInDate, Date checkOutDate) {
        return !(testDate.before(checkInDate) || testDate.after(checkOutDate));
    }
}
