/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OnlineReservation;
import entity.ReservationLineItem;
import entity.Room;
import java.util.Date;
import java.util.List;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

public interface ReservationControllerLocal {

    public List<ReservationLineItem> retrieveReservationLineItemByRoomType(Long roomTypeId);

    public ReservationLineItem retrieveReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException;
    
    public OnlineReservation retrieveOnlineReservationById(Long reservationId);

    public List<OnlineReservation> retrieveAllOnlineReservations();
    
    public ReservationLineItem createReservationLineItem(Date checkInDate, Date checkOutDate, int roomType);
    
    public ReservationLineItem createWalkInReservationLineItem(Date checkInDate, Date checkOutDate, Long roomTypeId, Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException;

    
}
