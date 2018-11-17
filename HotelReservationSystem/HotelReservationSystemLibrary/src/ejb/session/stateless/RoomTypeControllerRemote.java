/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import util.exception.DeleteRoomTypeException;
import util.exception.RoomTypeNotFoundException;

public interface RoomTypeControllerRemote {

    public RoomType createRoomType(RoomType newRoomType);

    public List<RoomType> retrieveAllRoomTypes();

    public RoomType retrieveRoomTypeById(Long roomTypeId) throws RoomTypeNotFoundException;

    public RoomType retrieveRoomTypeByName(String name) throws RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException;

    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException;
    
    public List<RoomType> retrieveAllEnabledRoomTypes();
}
