/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.entity.stateful.WalkInReservationSessionBeanRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 *
 * @author casseylow
 */
public class Main {
    
    @EJB
    private static EmployeeControllerRemote employeeControllerRemote;
    @EJB
    private static GuestControllerRemote guestControllerRemote;
    @EJB
    private static PartnerControllerRemote partnerControllerRemote;
    @EJB
    private static RoomControllerRemote roomControllerRemote;
    @EJB
    private static RoomRateControllerRemote roomRateControllerRemote;
    @EJB
    private static RoomTypeControllerRemote roomTypeControllerRemote;
    @EJB
    private static WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote;

    
    public static void main(String[] args) {
        
        MainApp mainApp=new MainApp(employeeControllerRemote, guestControllerRemote, partnerControllerRemote, roomControllerRemote, roomTypeControllerRemote, walkInReservationSessionBeanRemote, roomRateControllerRemote);
        mainApp.runApp();
        
        try {
             //allocate room to guests reservations at 2am daily
            Date currentDate = new Date();
            String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
            
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormatter.parse(modifiedDate + " 02:00:00");
            
            Timer timer = new Timer();
            
            timer.schedule(new SystemTask(), date);
        } catch (ParseException ex) {
            System.out.println("invalid date format");
        }

    }
    
}
