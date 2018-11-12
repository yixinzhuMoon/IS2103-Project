/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
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
    
    public static void main(String[] args) {
        
        MainApp mainApp=new MainApp(employeeControllerRemote, partnerControllerRemote);
        mainApp.runApp();
        
    }
    
}
