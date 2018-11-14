/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
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
import util.exception.DeleteRoomTypeException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(RoomTypeControllerLocal.class)
@Remote(RoomTypeControllerRemote.class)

public class RoomTypeController implements RoomTypeControllerRemote, RoomTypeControllerLocal {
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomControllerLocal roomControllerLocal;

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private ReservationControllerLocal reservationControllerLocal;

    public RoomTypeController(){
        
    }
    
    @Override
    public RoomType createRoomType(RoomType newRoomType)
    {
        em.persist(newRoomType);
        em.flush();
        
        return newRoomType;
    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypes()
    {
        Query query = em.createQuery("SELECT rt FROM RoomType rt");
        return query.getResultList();
    }
    
    @Override
    public RoomType retrieveRoomTypeById(Long roomTypeId) throws RoomTypeNotFoundException
    {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        
        if(roomType != null)
        {
            return roomType;
        }
        else
        {
            throw new RoomTypeNotFoundException("Room Type " + roomTypeId + " does not exist!");
        }
    }
    
    @Override
    public RoomType retrieveRoomTypeByName(String name) throws RoomTypeNotFoundException
    {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :inRoomTypeName");
        query.setParameter("inRoomTypeName", name);
        
        try
        {
            return (RoomType)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RoomTypeNotFoundException("Room Type " + name + " does not exist!");
        }
    }
    
    @Override
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException
    {
        if(roomType.getRoomTypeId()!= null)
        {
            RoomType roomTypeToUpdate = retrieveRoomTypeById(roomType.getRoomTypeId());
            if(roomTypeToUpdate.getName().equals(roomType.getName()))
            {
                roomTypeToUpdate.setName(roomType.getName());
                roomTypeToUpdate.setDescription(roomType.getDescription());
                roomTypeToUpdate.setRoomSize(roomType.getRoomSize());
                roomTypeToUpdate.setBed(roomType.getBed());
                roomTypeToUpdate.setCapacity(roomType.getCapacity());
                roomTypeToUpdate.setAmenities(roomType.getAmenities());
            }
            else
            {
            throw new RoomTypeNotFoundException("Room Type name is invalid");
            }
        }
        else
        {
            throw new RoomTypeNotFoundException("Room Type ID not provided for room type to be updated");
        }
    }
    
    @Override
    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException 
    {
        
        RoomType roomTypeToRemove = retrieveRoomTypeById(roomTypeId);
        
        if(roomTypeToRemove.getStatus().equals("enabled") && roomControllerLocal.retrieveRoomByRoomType(roomTypeId).isEmpty() 
                && roomRateControllerLocal.retrieveRoomRateByRoomType(roomTypeId).isEmpty() 
                && reservationControllerLocal.retrieveReservationLineItemByRoomType(roomTypeId).isEmpty())
        {
            em.remove(roomTypeToRemove); //enabled and not in use = delete
        }
        else if(roomTypeToRemove.getStatus().equals("enabled") && !roomControllerLocal.retrieveRoomByRoomType(roomTypeId).isEmpty()
                && !roomRateControllerLocal.retrieveRoomRateByRoomType(roomTypeId).isEmpty()
                && !reservationControllerLocal.retrieveReservationLineItemByRoomType(roomTypeId).isEmpty()) //enabled and in use = disabled
        {
            roomTypeToRemove.setStatus("disabled");
        }
        else
        {
            throw new DeleteRoomTypeException("Room Type ID " + roomTypeId + " is currently in use and cannot be deleted!");
        }
    }
}
