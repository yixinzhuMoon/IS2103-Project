/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PublishedRate;
import entity.Room;
import entity.RoomRate;
import java.util.List;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomRateException;
import util.exception.GeneralException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

public interface RoomRateControllerRemote {

    public List<RoomRate> retrieveRoomRateByRoomType(Long roomTypeId);

    public RoomRate createRoomRate(RoomRate newRoomRate, Long roomTypeId) throws RoomTypeNotFoundException, RoomExistException, GeneralException;

    public RoomRate retrieveRoomRateById(Long roomRateId, Boolean fetchRoomType) throws RoomRateNotFoundException;

    public List<RoomRate> retrieveAllRoomRates();

    public void updateRoomRate(RoomRate roomRate, String roomTypeName) throws RoomTypeNotFoundException, RoomNotFoundException, RoomRateNotFoundException;

    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException;

    public List<PublishedRate> retrieveAllPublishedRate();
    
}
