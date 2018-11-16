/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.OnlineReservation;
import entity.PartnerReservation;
import entity.PublishedRate;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(ReservationControllerLocal.class)
@Remote(ReservationControllerRemote.class)
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @EJB
    private RoomControllerLocal roomControllerLocal;
    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;
    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    
    public ReservationController()
    {
        
    }
    
    public List<ReservationLineItem> retrieveReservationLineItemByCheckInDate(Date checkInDate)
    {
        Query query = em.createQuery("SELECT rli FROM ReservationLineItem rli WHERE rli.checkInDate = :inCheckInDate");
        query.setParameter("inCheckInDate", checkInDate);
        return query.getResultList();
    }
    
    @Override
    public List<ReservationLineItem> retrieveReservationLineItemByRoomType(Long roomTypeId)
    {
        Query query = em.createQuery("SELECT rli FROM ReservationLineItem rli WHERE rli.roomType = :inRoomType");
        query.setParameter("inRoomType", em.find(RoomType.class, roomTypeId));
        
        return query.getResultList();
    }
    
    @Override
    public ReservationLineItem retrieveReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException
    {
        ReservationLineItem resLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        
        if(resLineItem != null)
        {
            return resLineItem;
        }
        else
        {
            throw new ReservationLineItemNotFoundException("Reservation Line Item " + resLineItem + " does not exist!");
        }
    }
    @Override
    public ReservationLineItem createReservationLineItem(Date checkInDate,Date checkOutDate,int roomType)
    {
        ReservationLineItem reservationLineItem=new ReservationLineItem();
        reservationLineItem.setCheckInDate(checkInDate);
        reservationLineItem.setCheckOutDate(checkOutDate);
        
        RoomType roomTypeItem=new RoomType();
        if(roomType==1)
        {
            roomTypeItem.setName("Deluxe Room");
            reservationLineItem.setRoomType(roomTypeItem);
        }
        else if(roomType==2)
        {
            roomTypeItem.setName("Permier Room");
            reservationLineItem.setRoomType(roomTypeItem);
        }
        else if(roomType==3)
        {
            roomTypeItem.setName("Family Room");
            reservationLineItem.setRoomType(roomTypeItem);
        }
        else if(roomType==4)
        {
            roomTypeItem.setName("Junior Suite");
            reservationLineItem.setRoomType(roomTypeItem);
        }
        else if(roomType==5)
        {
            roomTypeItem.setName("Grand Suite");
            reservationLineItem.setRoomType(roomTypeItem);
        }
                
        em.persist(reservationLineItem);
        em.flush();
        
        return reservationLineItem;
    }
    
    @Override
    public OnlineReservation retrieveOnlineReservationById(Long reservationId)
    {
        Query query=em.createQuery("SELECT o FROM OnlineReservation o WHERE o.reservationId=:inReservationId");
        query.setParameter("inReservationId", reservationId);
        
        return (OnlineReservation) query.getSingleResult();
    }
    
    @Override
    public List<OnlineReservation> retrieveAllOnlineReservations()
    {
        Query query=em.createNamedQuery("SELECT o FROM OnlineReservation o");
        
        return query.getResultList();
    }
    
    @Override
    public PartnerReservation retrievePartnerReservationById(Long reservationId)
    {
        Query query=em.createQuery("SELECT p FROM PartnerReservationp WHERE p.reservationId=:inReservationId");
        query.setParameter("inReservationId", reservationId);
        
        return (PartnerReservation) query.getSingleResult();
    }
    
    @Override
    public List<PartnerReservation> retrieveAllPartnerReservations()
    {
        Query query=em.createNamedQuery("SELECT p FROM PartnerReservation o");
        
        return query.getResultList();
    }
    
    @Override
    public ExceptionReport createExceptionReport(ExceptionReport exceptionReport){
        em.persist(exceptionReport);
        em.flush();
        return exceptionReport;
    }
    
    @Override
    public void allocateRoomToCurrentDayReservations(){
        Date todayDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        List<ReservationLineItem> reservationLineItems = retrieveReservationLineItemByCheckInDate(todayDate);
        List<Room> rooms = new ArrayList<>();
        ExceptionReport exReport = new ExceptionReport();
        
        for(ReservationLineItem reservationLineItem:reservationLineItems)
        {
            //allocate an available room for reserved room type
            List<Room> roomsFiltered = roomControllerLocal.retrieveAvailableRoomsByRoomType(reservationLineItem.getRoomType().getRoomTypeId());
            List<Room> otherRooms = roomControllerLocal.retrieveAllAvailableRooms();
            
            if(roomsFiltered!=null){
                for(Room room:roomsFiltered){
                    room.setRoomStatus("occupied");
                    room.setReservation(reservationLineItem);
                    reservationLineItem.getRoomList().add(room);
                }
            }
            else if(otherRooms != null)
            {
                //no room of room type, upgrade available
                for(Room room:roomsFiltered){
                    if(room.getRoomStatus().equals("available"))
                    {
                        room.setRoomStatus("occupied");
                        room.setReservation(reservationLineItem);
                        reservationLineItem.getRoomList().add(room);
                        if(reservationLineItem.getRoomList() == null){
                            exReport.setDescription("No available room for reserved room type, upgrade to next higher room type available. Room " + room.getRoomId() + " allocated.");
                            createExceptionReport(exReport);
                        }
                    }
                }
            }
            else
            {
                //no room no upgrade
                if(reservationLineItem.getRoomList() == null){
                    exReport.setDescription("No available room for reserved room type, no upgrade to next higher room type available. No room allocated.");
                    createExceptionReport(exReport);
                }
            }
        }
    }
    
    @Override
    public ReservationLineItem createWalkInReservationLineItem(Date checkInDate, Date checkOutDate, Long roomTypeId, Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException
    {
        try 
        {
            ReservationLineItem reservationLineItem=new ReservationLineItem();
            reservationLineItem.setCheckInDate(checkInDate);
            reservationLineItem.setCheckOutDate(checkOutDate);
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeById(roomTypeId);
            reservationLineItem.setRoomType(roomType);
            RoomRate roomRate = roomRateControllerLocal.retrieveRoomRateById(roomRateId, false);
            reservationLineItem.setRoomRate(roomRate);

            em.persist(reservationLineItem);
            em.flush();

            return reservationLineItem;
        }
        catch(RoomTypeNotFoundException ex)
        {
            throw new RoomTypeNotFoundException("Unable to create new reservation line item as the room type record does not exist");
        } 
        catch (RoomRateNotFoundException ex) 
        {
            throw new RoomRateNotFoundException("Unable to create new reservation line item as the room rate record does not exist");
        }
    }
    
    @Override
    public ReservationLineItem createRoomReservationLineItem(Date checkInDate, Date checkOutDate, Long roomTypeId, Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException
    {
        try 
        {
            ReservationLineItem reservationLineItem=new ReservationLineItem();
            reservationLineItem.setCheckInDate(checkInDate);
            reservationLineItem.setCheckOutDate(checkOutDate);
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeById(roomTypeId);
            reservationLineItem.setRoomType(roomType);
            RoomRate roomRate = roomRateControllerLocal.retrieveRoomRateById(roomRateId, false);
            reservationLineItem.setRoomRate(roomRate);

            em.persist(reservationLineItem);
            em.flush();

            return reservationLineItem;
        }
        catch(RoomTypeNotFoundException ex)
        {
            throw new RoomTypeNotFoundException("Unable to create new reservation line item as the room type record does not exist");
        } 
        catch (RoomRateNotFoundException ex) 
        {
            throw new RoomRateNotFoundException("Unable to create new reservation line item as the room rate record does not exist");
        }
    }
    
    
}
