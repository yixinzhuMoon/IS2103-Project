/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import exception.GuestNotFoundException;
import exception.InvalidLoginCredentialException;

public interface GuestControllerRemote {

    public Guest guestLogin(String email, String password) throws InvalidLoginCredentialException, GuestNotFoundException;

    public Guest retrieveGuestByEmail(String email) throws GuestNotFoundException;

    public Long createGuest(Guest newGuest);
    
}
