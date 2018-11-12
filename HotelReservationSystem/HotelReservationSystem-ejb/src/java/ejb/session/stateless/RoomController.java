/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.Room;
import entity.RoomType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomTypeException;
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

    public RoomController() {
    }
    
    @Override
    public Room createRoom(Room newRoom)
    {
        em.persist(newRoom);
        em.flush();
        
        return newRoom;
    }
    
    @Override
    public List<Room> retrieveAllRooms()
    {
        Query query = em.createQuery("SELECT r FROM Room r");
        return query.getResultList();
    }
    
    @Override
    public Room retrieveRoomById(Integer roomId) throws RoomNotFoundException
    {
        Room room = em.find(Room.class, roomId);
        
        if(room != null)
        {
            return room;
        }
        else
        {
            throw new RoomNotFoundException("Room " + roomId + " does not exist!");
        }
    }
    
    @Override
    public void updateRoom(Room room, String roomTypeName) throws RoomNotFoundException, RoomTypeNotFoundException
    {
        if(room.getRoomNumber()!= null)
        {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
            Room roomToUpdate = retrieveRoomById(room.getRoomNumber());
            
            if(roomToUpdate.getRoomNumber().equals(room.getRoomNumber()))
            {
                roomToUpdate.setRoomStatus(room.getRoomStatus());
                roomToUpdate.setRoomType(roomType); 
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
    public void deleteRoom(Integer roomId) throws RoomNotFoundException, DeleteRoomException 
    {
        
        Room roomToRemove = retrieveRoomById(roomId);
        
         //available and not in use = delete
         //available and in use = change status to disabled
         //otherwise unable to delete from because it is already disabled
        if(roomToRemove.getRoomStatus().equals("available"))
        {
            em.remove(roomToRemove);
        }
        else
        {
            throw new DeleteRoomException("Room Number " + roomId + " is currently in use and cannot be deleted!");
        }
    }
    
    
    public List<ExceptionReport> generateRoomAllocationExceptionReport(){
        return new ArrayList<>();
    }
    
}
