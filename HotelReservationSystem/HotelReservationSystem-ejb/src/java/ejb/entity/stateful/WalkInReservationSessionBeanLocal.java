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
import javax.ejb.Local;
import util.exception.EmployeeNotFoundException;

public interface WalkInReservationSessionBeanLocal {

    public List<ReservationLineItem> walkInSearchHotelRoom(Date checkInDate, Date checkOutDate);

    public void preDestroy();

    public void remove();

    public Long getTotalAmount();
    
    public WalkInReservation reserveRoom(String email, ReservationLineItem reservationRoom) throws EmployeeNotFoundException;
}
