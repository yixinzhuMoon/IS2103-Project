/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.OnlineReservation;
import entity.Room;
import java.util.List;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.TimeException;

public interface GuestControllerRemote {

    public Guest guestLogin(String email, String password) throws InvalidLoginCredentialException, GuestNotFoundException;

    public Guest retrieveGuestByEmail(String email) throws GuestNotFoundException;

    public Long createGuest(Guest newGuest);

    public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException;

    public List<OnlineReservation> retrieveOnlineReservationListByGuest(Long guestId) throws GuestNotFoundException;

    public void checkOutGuest(Long guestId) throws GuestNotFoundException;

    public List<Room> checkInGuest(Long guestId) throws GuestNotFoundException, TimeException;
    
}
