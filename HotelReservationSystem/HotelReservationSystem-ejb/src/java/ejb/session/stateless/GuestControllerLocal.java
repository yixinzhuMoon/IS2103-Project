/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.OnlineReservation;
import java.util.List;
import util.exception.GuestNotFoundException;

public interface GuestControllerLocal {
    
    public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException;

    public List<OnlineReservation> retrieveOnlineReservationListByGuest(Long guestId) throws GuestNotFoundException;

    public void checkOutGuest(Long guestId) throws GuestNotFoundException;
    
    public void checkInGuest(Long guestId) throws GuestNotFoundException;
}
