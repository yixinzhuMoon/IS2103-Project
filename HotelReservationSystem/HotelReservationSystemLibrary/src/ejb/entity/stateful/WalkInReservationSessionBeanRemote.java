/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.entity.stateful;

import entity.ReservationLineItem;
import entity.Room;
import entity.WalkInReservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeNotFoundException;

public interface WalkInReservationSessionBeanRemote {

    public Long totalAmount(String roomType, Integer numberOfRooms, Date checkInDate, Date checkOutDate);

    public String chooseRoomRate(String roomType, Date checkInDate, Date checkOutDate);
    
}
