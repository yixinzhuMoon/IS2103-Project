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
    
    public List<ReservationLineItem> retrieveReservationLineItemByCheckOutDate(Date checkOutDate)
    {
        Query query = em.createQuery("SELECT rli FROM ReservationLineItem rli WHERE rli.checkOutDate = :inCheckOutDate");
        query.setParameter("inCheckOutDate", checkOutDate); //reservationlineitem is due for check out today
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
    public List<Room> allocateRoomToCurrentDayReservations(){
        Date todayDate = new Date();
        List<ReservationLineItem> reservationLineItemsCheckInToday = retrieveReservationLineItemByCheckInDate(todayDate);
        List<ReservationLineItem> reservationLineItemsCheckOutToday = retrieveReservationLineItemByCheckOutDate(todayDate);
        ExceptionReport exReport = new ExceptionReport();
        List<Room> roomsReserved = new ArrayList<>();
        List<Room> roomsAvailableForCheckIn = new ArrayList<>();
        
        //get reservation list for check in today
        for(ReservationLineItem reservationLineItem:reservationLineItemsCheckInToday)
        {
            //get rooms available
            List<Room> roomsFiltered = roomControllerLocal.retrieveAvailableRoomsByRoomType(reservationLineItem.getRoomType().getRoomTypeId());
            for(Room room: roomsFiltered){
                roomsAvailableForCheckIn.add(room);
            }
            //get rooms that are due for check out today
            if(!reservationLineItemsCheckOutToday.isEmpty()){
                
            }
            for(ReservationLineItem reservations:reservationLineItemsCheckOutToday){
                for(Room room:reservations.getRoomList()){
                    if(room.getRoomStatus().equals("occupied") && reservations.getRoomType() == reservationLineItem.getRoomType()){
                        roomsAvailableForCheckIn.add(room);
                    }
                }
            }
            List<Room> otherRooms = roomControllerLocal.retrieveAllAvailableRooms();
            
            //room available for reserved room type
            if(!roomsAvailableForCheckIn.isEmpty()){
                for(Room room:roomsAvailableForCheckIn){
                    if(room.getRoomStatus().equals("occupied")){
                        room.setRoomStatus("occupied reserved");
                    }else if(room.getRoomStatus().equals("available")){
                        room.setRoomStatus("reserved");
                    }
                    reservationLineItem.getRoomList().add(room);
                    roomsReserved.add(room);
                }
            }
            else if(otherRooms != null)
            {
                //no room of room type, upgrade available
                for(Room room:otherRooms){
                    if(room.getRoomStatus().equals("available") 
                            && room.getRoomType().getRoomRank() < reservationLineItem.getRoomType().getRoomRank())
                    {
                        room.setRoomStatus("reserved");
                        reservationLineItem.getRoomList().add(room);
                        roomsReserved.add(room);
                        //raise exception report
                        exReport.setDescription("No available room for reserved room type, upgrade to next higher room type available. Room " + room.getRoomId() + " allocated.");
                        createExceptionReport(exReport);
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
            else 
            {
                //no room no upgrade
                if(reservationLineItem.getRoomList() == null){
                    exReport.setDescription("No available room for reserved room type, no upgrade to next higher room type available. No room allocated.");
                    createExceptionReport(exReport);
                }
            }
        }
        return roomsReserved;
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
            RoomRate roomRate = roomRateControllerLocal.retrieveRoomRateById(roomRateId, false);

            em.persist(reservationLineItem);
            
            reservationLineItem.setRoomRate(roomRate);
            reservationLineItem.setRoomType(roomType);
            roomType.getReservationLineItems().add(reservationLineItem);
            
            em.flush();
            em.refresh(reservationLineItem);

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
