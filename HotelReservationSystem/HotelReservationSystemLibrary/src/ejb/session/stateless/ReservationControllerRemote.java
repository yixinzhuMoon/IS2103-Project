/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OnlineReservation;
import java.util.List;

public interface ReservationControllerRemote {

    public OnlineReservation retrieveOnlineReservationById(Long reservationId);

    public List<OnlineReservation> retrieveAllOnlineReservations();

    
    
}
