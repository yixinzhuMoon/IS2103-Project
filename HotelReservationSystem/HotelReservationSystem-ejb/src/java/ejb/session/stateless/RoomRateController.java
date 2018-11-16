/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.NormalRate;
import entity.PeakRate;
import entity.PromotionRate;
import entity.PublishedRate;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import entity.RoomRate;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomRateException;
import util.exception.GeneralException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(RoomRateControllerLocal.class)
@Remote(RoomRateControllerRemote.class)
public class RoomRateController implements RoomRateControllerRemote, RoomRateControllerLocal {
    

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    
    
    public RoomRateController(){
        
    }
    
    @Override
    public RoomRate createRoomRate(RoomRate newRoomRate, Long roomTypeId) throws RoomTypeNotFoundException, RoomExistException, GeneralException
    {
        try 
        {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeById(roomTypeId);
            
            if(!roomType.getStatus().equals("disabled"))
            {
                em.persist(newRoomRate);
                
                newRoomRate.setRoomType(roomType);
                
                em.flush();
                em.refresh(newRoomRate);

                return newRoomRate;
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
    public List<RoomRate> retrieveAllRoomRates()
    {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr");
        return query.getResultList();
    }
    
    @Override
    public RoomRate retrieveRoomRateById(Long roomRateId, Boolean fetchRoomType) throws RoomRateNotFoundException{
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        
        if(roomRate != null)
        {
            if(fetchRoomType){
                roomRate.getRoomType();
            }
            return roomRate;
        }
        else
        {
            throw new RoomRateNotFoundException("Room rate" + roomRateId + " does not exist!");
        }
    }
    
    @Override
    public List<RoomRate> retrieveRoomRateByRoomType(Long roomTypeId)
    {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType = :inRoomType");
        query.setParameter("inRoomType", em.find(RoomType.class, roomTypeId));
        
        return query.getResultList();
    }
    
    @Override
    public void updateRoomRate(RoomRate roomRate, String roomTypeName) throws RoomTypeNotFoundException, RoomNotFoundException, RoomRateNotFoundException
    {
        if(roomRate.getRoomRateId()!= null)
        {
            if(roomRate instanceof PromotionRate){
                PromotionRate roomRateToUpdate = (PromotionRate) retrieveRoomRateById(roomRate.getRoomRateId(), true);
                if(roomRateToUpdate.getRoomRateId().equals(roomRate.getRoomRateId()))
                {
                    roomRateToUpdate.setName(roomRate.getName());
                    if(!roomTypeName.equals("")){
                        RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
                        roomRateToUpdate.setRoomType(roomType); 
                    }
                    roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                    roomRateToUpdate.setStartDate(((PromotionRate) roomRate).getStartDate());
                    roomRateToUpdate.setEndDate(((PromotionRate) roomRate).getEndDate());
                }
            }
            else if(roomRate instanceof NormalRate)
            {
                NormalRate roomRateToUpdate = (NormalRate) retrieveRoomRateById(roomRate.getRoomRateId(), true);
                if(roomRateToUpdate.getRoomRateId().equals(roomRate.getRoomRateId()))
                {
                    roomRateToUpdate.setName(roomRate.getName());
                    if(!roomTypeName.equals("")){
                        RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
                        roomRateToUpdate.setRoomType(roomType); 
                    }
                    roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                    roomRateToUpdate.setStartDate(((NormalRate) roomRate).getStartDate());
                    roomRateToUpdate.setEndDate(((NormalRate) roomRate).getEndDate());
                }
            }
            else if(roomRate instanceof PeakRate)
            {
                PeakRate roomRateToUpdate = (PeakRate) retrieveRoomRateById(roomRate.getRoomRateId(), true);
                if(roomRateToUpdate.getRoomRateId().equals(roomRate.getRoomRateId()))
                {
                    roomRateToUpdate.setName(roomRate.getName());
                    if(!roomTypeName.equals("")){
                        RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
                        roomRateToUpdate.setRoomType(roomType); 
                    }
                    roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                    roomRateToUpdate.setStartDate(((PeakRate) roomRate).getStartDate());
                    roomRateToUpdate.setEndDate(((PeakRate) roomRate).getEndDate());
                }
            }
            else if(roomRate instanceof PublishedRate)
            {
                PublishedRate roomRateToUpdate = (PublishedRate) retrieveRoomRateById(roomRate.getRoomRateId(), true);
                if(roomRateToUpdate.getRoomRateId().equals(roomRate.getRoomRateId()))
                {
                    roomRateToUpdate.setName(roomRate.getName());
                    if(!roomTypeName.equals("")){
                        RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
                        roomRateToUpdate.setRoomType(roomType); 
                    }
                    roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                    roomRateToUpdate.setValidity(((PublishedRate) roomRate).getValidity());
                }
            }
            else
            {
                throw new RoomRateNotFoundException("Room Rate Id is invalid");
            }
        }
        else
        {
            throw new RoomRateNotFoundException("Room Rate Id not provided for room rate to be updated");
        }
    }
    
    @Override
    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException
    {
        RoomRate roomRateToRemove = retrieveRoomRateById(roomRateId, false);
        
        if(roomRateToRemove.getRoomRateStatus().equals("enabled")) 
        {
            em.remove(roomRateToRemove); //enabled and not in use = delete
        }
        else if(roomRateToRemove.getRoomRateStatus().equals("enabled"))
        {
            roomRateToRemove.setRoomRateStatus("disabled"); //enabled and in use = disable
        }
        else
        {
            throw new DeleteRoomRateException("Room Rate " + roomRateId + " is currently in use and cannot be deleted!");
        }
    }
    
    @Override
    public List<PublishedRate> retrieveAllPublishedRate(){
        Query query = em.createQuery("SELECT p FROM PublishedRate p WHERE p.roomRateStatus <> :inRoomRateStatus");
        query.setParameter("inRoomRateStatus", "disabled");
        return query.getResultList();
    }
    
    @Override
    public List<NormalRate> retrieveAllNormalRate(){
        Query query = em.createQuery("SELECT n FROM NormalRate n WHERE n.roomRateStatus <> :inRoomRateStatus");
        query.setParameter("inRoomRateStatus", "disabled");
        return query.getResultList();
    }
    
    @Override
    public List<PromotionRate> retrieveAllPromotionRate(){
        Query query = em.createQuery("SELECT p FROM PromotionRate p WHERE p.roomRateStatus <> :inRoomRateStatus");
        query.setParameter("inRoomRateStatus", "disabled");
        return query.getResultList();
    }
    
    @Override
    public List<PeakRate> retrieveAllPeakRate(){
        Query query = em.createQuery("SELECT p FROM PeakRate p WHERE p.roomRateStatus <> :inRoomRateStatus");
        query.setParameter("inRoomRateStatus", "disabled");
        return query.getResultList();
    }
    
    @Override
    public List<PeakRate> retrieveAllRoomRate(){
        Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomRateStatus <> :inRoomRateStatus");
        query.setParameter("inRoomRateStatus", "disabled");
        return query.getResultList();
    }
}
