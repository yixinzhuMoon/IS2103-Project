/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomNight;
import java.util.List;
import util.exception.RoomRateNotFoundException;

public interface RoomNightControllerRemote {

    public List<RoomNight> retrieveRoomNightByRoomRate(Long roomRateId);
    
    public RoomNight createRoomNight(RoomNight newRoomNight, Long roomRateId) throws RoomRateNotFoundException;
}
