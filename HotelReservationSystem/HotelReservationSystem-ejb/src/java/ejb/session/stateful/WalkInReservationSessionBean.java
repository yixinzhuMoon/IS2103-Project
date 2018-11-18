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
    
    private List<Long> ratePrices;

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
        if(ratePrices != null)
        {
            ratePrices.clear();
            ratePrices = null;
        }
    }
    
    @Override
    public List<Room> walkInSearchHotelRoom(Date checkInDate, Date checkOutDate)
    {
        List<RoomType> allRoomTypeAvailable= roomTypeControllerLocal.retrieveAllEnabledRoomTypes();
        List<PublishedRate> publishedRateAvailable = new ArrayList<>();
        List<PublishedRate> publishedRateWithinRange = new ArrayList<>();
        List<RoomType> validRoomTypes = new ArrayList<>();
        
        //get all room types enabled
        for(RoomType roomType: allRoomTypeAvailable)
        {
            for(RoomRate roomRate:roomType.getRoomRates())
            {
                if(roomRate instanceof PublishedRate){
                    publishedRateAvailable.add((PublishedRate) roomRate);
                }
            }
        }
        //get published rate within check in and check out date
        for(PublishedRate publishedRate:publishedRateAvailable){
            if(isWithinRange(publishedRate.getValidity(), checkInDate, checkOutDate))
            {
                if(!validRoomTypes.contains(publishedRate.getRoomType())){
                    publishedRateWithinRange.add(publishedRate);
                    validRoomTypes.add(publishedRate.getRoomType());
                }
            }
        }
        //get all available rooms for reservation
        List<Room> roomsSearchResult = new ArrayList<>();
        for(PublishedRate pubRate:publishedRateWithinRange){
            Long totalAmount = checkOutDate.getTime()-checkInDate.getTime()*pubRate.getRatePerNight().longValue();
            ratePrices.add(totalAmount);
        }
        for(RoomType roomType: validRoomTypes){
            for(Room room: roomType.getRooms()){
                roomsSearchResult.add(room);
            }
        }
        return roomsSearchResult;
    }
    
    @Override
    public List<Long> totalAmount()  
    {
        return this.ratePrices;
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
    
    public boolean isWithinRange(Date testDate, Date checkInDate, Date checkOutDate) {
        return !(testDate.after(checkInDate) || testDate.before(checkOutDate));
    }

}
