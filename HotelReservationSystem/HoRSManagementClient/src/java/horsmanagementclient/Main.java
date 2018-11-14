/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import javax.ejb.EJB;

/**
 *
 * @author casseylow
 */
public class Main {
    
    @EJB
    private static EmployeeControllerRemote employeeControllerRemote;
    @EJB
    private static PartnerControllerRemote partnerControllerRemote;
    @EJB
    private static RoomControllerRemote roomControllerRemote;
    @EJB
    private static RoomRateControllerRemote roomRateControllerRemote;
    @EJB
    private static RoomTypeControllerRemote roomTypeControllerRemote;

    
    public static void main(String[] args) {
        
        MainApp mainApp=new MainApp(employeeControllerRemote, partnerControllerRemote, roomControllerRemote, roomTypeControllerRemote, roomRateControllerRemote);
        mainApp.runApp();
        
    }
    
}
