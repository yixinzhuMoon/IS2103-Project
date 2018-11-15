/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.ReservationControllerRemote;
import java.util.TimerTask;
import javax.ejb.EJB;

/**
 *
 * @author casseylow
 */
public class SystemTask extends TimerTask{
    
    @EJB 
    private static ReservationControllerRemote reservationControllerRemote;
    
    public void run()
    {
        reservationControllerRemote.allocateRoomToCurrentDayReservations();
    }
}
