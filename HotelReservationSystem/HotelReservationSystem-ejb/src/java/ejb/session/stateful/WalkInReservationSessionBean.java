/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.entity.stateful.WalkInReservationSessionBeanRemote;
import ejb.session.stateless.ReservationController;
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
import entity.RoomType;
import entity.WalkInReservation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    
    @EJB 
    private RoomTypeControllerLocal roomTypeControllerLocal;
    
    private PublishedRate publishedRate;
    private Date checkInDate;
    private Date checkOutDate;
    private Long totalAmount;
    
    private List<ReservationLineItem> reservationLineItems;

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
        if(reservationLineItems != null)
        {
            reservationLineItems.clear();
            reservationLineItems = null;
        }
    }
    
    @Override
    public List<ReservationLineItem> walkInSearchHotelRoom(Date checkInDate, Date checkOutDate){
        reservationLineItems = new ArrayList<>();
        List<RoomType> validRoomTypes = roomTypeControllerLocal.retrieveAllRoomTypes();
        
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
    
    public boolean belongCalendar(Date date, Date startDate, Date endDate) 
    {
        return !(date.before(startDate) || date.after(endDate));
    }
}
