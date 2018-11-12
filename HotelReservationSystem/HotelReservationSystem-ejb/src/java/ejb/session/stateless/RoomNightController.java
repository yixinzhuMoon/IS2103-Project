/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomNight;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(RoomNightControllerLocal.class)
@Remote(RoomNightControllerRemote.class)
public class RoomNightController implements RoomNightControllerRemote, RoomNightControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomNightController() {
    }

    public void createRoomNight(){
    }
}
