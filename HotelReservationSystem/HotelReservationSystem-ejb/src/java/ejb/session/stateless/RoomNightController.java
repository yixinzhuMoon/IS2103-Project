/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomNight;
import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.RoomRateNotFoundException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(RoomNightControllerLocal.class)
@Remote(RoomNightControllerRemote.class)
public class RoomNightController implements RoomNightControllerRemote, RoomNightControllerLocal {

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomNightController() {
    }
    
    public RoomNight createRoomNight(RoomNight newRoomNight, Long roomRateId) throws RoomRateNotFoundException
    {
        try 
        {
            RoomRate roomRate = roomRateControllerLocal.retrieveRoomRateById(roomRateId, true);
            
            if(!roomRate.getRoomRateStatus().equals("disabled"))
            {
                em.persist(newRoomNight);
                
                newRoomNight.setRoomRate(roomRate);
                
                em.flush();
                em.refresh(newRoomNight);

                return newRoomNight;
            }
            else
            {
                throw new RoomRateNotFoundException("Unable to create new room as the room rate record does not exist");
            }
        } 
        catch (RoomRateNotFoundException ex) 
        {
            throw new RoomRateNotFoundException("Unable to create new room as the room rate record does not exist");
        }
    }
    
    @Override
    public List<RoomNight> retrieveRoomNightByRoomRate(Long roomRateId)
    {
        Query query = em.createQuery("SELECT r FROM RoomNight r WHERE r.roomRate = :inRoomRate");
        query.setParameter("inRoomRate", em.find(RoomRate.class, roomRateId));
        
        return query.getResultList();
    }
    
}
