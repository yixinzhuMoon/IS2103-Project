/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomTypeException;
import util.exception.GeneralException;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(RoomControllerLocal.class)
@Remote(RoomControllerRemote.class)
public class RoomController implements RoomControllerRemote, RoomControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @EJB
    private ReservationControllerLocal reservationControllerLocal;
    
    public RoomController() {
    }
    
    @Override
    public Room createRoom(Room newRoom, Long roomTypeId) throws RoomTypeNotFoundException, RoomExistException, GeneralException
    {
        try 
        {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeById(roomTypeId);
            
            if(!roomType.getStatus().equals("disabled"))
            {
                em.persist(newRoom);
                
                newRoom.setRoomType(roomType);
                roomType.getRooms().add(newRoom);
                
                em.flush();
                em.refresh(newRoom);

                return newRoom;
            }
            else
            {
                throw new RoomTypeNotFoundException("Unable to create new room as the room type record does not exist");
            }
        } 
        catch (RoomTypeNotFoundException ex) 
        {
            throw new RoomTypeNotFoundException("Unable to create new room as the room type record does not exist");
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && 
                    ex.getCause().getCause() != null &&
                    ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException"))
            {
                throw new RoomExistException("Room with same room number already exist");
            }
            else
            {
                throw new GeneralException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public List<Room> retrieveAllRooms()
    {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomStatus <> :inRoomStatus");
        query.setParameter("inRoomStatus", "disabled");
        return query.getResultList();
    }
    
    @Override
    public List<Room> retrieveAllAvailableRooms(){
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomStatus = :inRoomStatus");
        query.setParameter("inRoomStatus", "available");
        return query.getResultList();
    }
    
    
    @Override
    public Room retrieveRoomById(Integer roomNumber, Boolean fetchRoomType, Boolean fetchReservation) throws RoomNotFoundException
    {
        Room room = em.find(Room.class, roomNumber);
        
        if(room != null)
        {
            if(fetchReservation){
                room.getReservation();
            }
            if(fetchRoomType){
                room.getRoomType();
            }
            return room;
        }
        else
        {
            throw new RoomNotFoundException("Room " + roomNumber + " does not exist!");
        }
    }
    
    @Override
    public List<Room> retrieveRoomByRoomType(Long roomTypeId)
    {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType = :inRoomType");
        query.setParameter("inRoomType", em.find(RoomType.class, roomTypeId));
        
        return query.getResultList();
    }
    
    @Override
    public List<Room> retrieveAvailableRoomsByRoomType(Long roomTypeId)
    {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType = :inRoomType AND r.roomStatus = :inRoomStatus");
        query.setParameter("inRoomType", em.find(RoomType.class, roomTypeId));
        query.setParameter("inRoomStatus", "available");
        
        return query.getResultList();
    }
    @Override
    public Room updateRoom(Room room, String roomTypeName, Long reservationLineItemId) throws RoomNotFoundException, RoomTypeNotFoundException, ReservationLineItemNotFoundException
    {
        if(room.getRoomNumber()!= null)
        { 
            Room roomToUpdate = retrieveRoomById(room.getRoomNumber(), true, true);
            if(roomToUpdate.getRoomNumber().equals(room.getRoomNumber()))
            {
                roomToUpdate.setRoomStatus(room.getRoomStatus());
                if(!roomTypeName.equals("")){
                    RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
                    List<Room> oldRoomTypeRoomList = roomToUpdate.getRoomType().getRooms();
                    oldRoomTypeRoomList.remove(roomToUpdate);
                    roomToUpdate.setRoomType(roomType); 
                }
                if(reservationLineItemId != null){
                    ReservationLineItem resLineitem = reservationControllerLocal.retrieveReservationLineItemById(reservationLineItemId);
                    roomToUpdate.setReservation(resLineitem);
                }
                return roomToUpdate;
            }
            else
            {
                throw new RoomNotFoundException("Room number is invalid");
            }
        }
        else
        {
            throw new RoomNotFoundException("Room number not provided for room to be updated");
        }
    }
    
    @Override
    public void updateRoomListInRoomType(Integer roomNo) throws RoomNotFoundException
    {
        Room room = retrieveRoomById(roomNo, true, true);
        room.getRoomType().getRooms().add(room);
    }
    
    @Override
    public void deleteRoom(Integer roomNo) throws RoomNotFoundException, DeleteRoomException 
    {
        Room roomToRemove = retrieveRoomById(roomNo, false, false);
        
        if(roomToRemove.getRoomStatus().equals("available"))
        {
            List<Room> removeRoomFromRoomType = roomToRemove.getRoomType().getRooms();
            removeRoomFromRoomType.remove(roomToRemove);
            em.remove(roomToRemove); //available = not in use
        }
        else if(roomToRemove.getRoomStatus().equals("occupied"))
        {
            roomToRemove.setRoomStatus("disabled"); //occupied = in use
        }
        else
        {
            throw new DeleteRoomException("Room Number " + roomNo + " is currently in use and cannot be deleted!");
        }
    }
    
    @Override
    public List<ExceptionReport> generateRoomAllocationExceptionReport(){
        Query query = em.createQuery("SELECT e FROM ExceptionReport e");
        return query.getResultList();
    }
    
}
