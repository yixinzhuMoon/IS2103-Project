/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationLineItem;
import java.util.List;
import util.exception.ReservationLineItemNotFoundException;

public interface ReservationControllerLocal {

    public List<ReservationLineItem> retrieveReservationLineItemByRoomType(Long roomTypeId);

    public ReservationLineItem retrieveReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException;
}
