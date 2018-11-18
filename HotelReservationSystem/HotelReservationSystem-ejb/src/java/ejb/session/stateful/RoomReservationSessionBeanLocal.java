/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import entity.OnlineReservation;
import entity.PartnerReservation;
import entity.ReservationLineItem;
import java.util.Date;
import java.util.List;
import util.exception.GuestNotFoundException;

public interface RoomReservationSessionBeanLocal {
    
    public void searchHotelRoom(Date checkInDate, Date checkOutDate);

    public Long totalAmount(String roomType, Integer roomNumber,Date checkInDate,Date checkOutDate);

    public PartnerReservation reservePartnerRoom(String email, ReservationLineItem reservationRoom);

}
