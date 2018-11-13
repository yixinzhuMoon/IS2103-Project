/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import util.exception.DeleteRoomException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;

public interface RoomControllerLocal {

    public Room createRoom(Room newRoom);

    public List<Room> retrieveAllRooms();

    public Room retrieveRoomById(Integer roomId) throws RoomNotFoundException;

    public void updateRoom(Room room, String roomTypeName) throws RoomNotFoundException, RoomTypeNotFoundException;

    public void deleteRoom(Integer roomId) throws RoomNotFoundException, DeleteRoomException;
}
