/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.OnlineReservation;
import entity.PartnerReservation;
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
    
    public ReservationLineItem createReservationLineItem(Date checkInDate,Date checkOutDate,String roomType)throws RoomTypeNotFoundException;
    
    public ReservationLineItem createWalkInReservationLineItem(Date checkInDate, Date checkOutDate, Long roomTypeId, Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException;

    public List<Room> allocateRoomToCurrentDayReservations();

    public PartnerReservation retrievePartnerReservationById(Long reservationId);

    public List<PartnerReservation> retrieveAllPartnerReservations();

    public ExceptionReport createExceptionReport(ExceptionReport exceptionReport);

    public ReservationLineItem createRoomReservationLineItem(Date checkInDate, Date checkOutDate, Long roomTypeId, Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException;

}
