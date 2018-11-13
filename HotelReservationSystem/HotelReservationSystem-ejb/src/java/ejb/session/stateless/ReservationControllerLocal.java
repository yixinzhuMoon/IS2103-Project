/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationLineItem;
import java.util.Date;

public interface ReservationControllerLocal {

    public ReservationLineItem createReservationLineItem(Date checkInDate, Date checkOutDate,int roomType);
    
}
