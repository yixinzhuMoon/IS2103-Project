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
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import javax.management.Query;
import util.enumeration.EmployeeAccessRight;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author casseylow
 */
class MainApp {
    
    private EmployeeControllerRemote employeeControllerRemote;
    private GuestControllerRemote guestControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote;
    private ReservationControllerRemote reservationControllerRemote;
    
    private SystemAdministrationModule systemAdminModule;
    private HotelOperationModule hotelOpModule;
    private FrontOfficeModule frontOfficeModule;
    
    private Employee currentEmployee;
    
    public MainApp(EmployeeControllerRemote employeeControllerRemote, GuestControllerRemote guestControllerRemote, PartnerControllerRemote partnerControllerRemote, 
            RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote,
            RoomRateControllerRemote roomRateControllerRemote, ReservationControllerRemote reservationControllerRemote)
    {
        this.employeeControllerRemote = employeeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.guestControllerRemote = guestControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.walkInReservationSessionBeanRemote = walkInReservationSessionBeanRemote;
    }
    
    public void runApp()
    {
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true){
            System.out.println("*** Welcome to HoRS :: Hotel Management System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        systemAdminModule = new SystemAdministrationModule(employeeControllerRemote, partnerControllerRemote, currentEmployee);
                        hotelOpModule = new HotelOperationModule(employeeControllerRemote, partnerControllerRemote, roomControllerRemote, roomTypeControllerRemote, roomRateControllerRemote, reservationControllerRemote, currentEmployee);
                        frontOfficeModule = new FrontOfficeModule(employeeControllerRemote, guestControllerRemote, partnerControllerRemote, roomControllerRemote, roomTypeControllerRemote, roomRateControllerRemote, walkInReservationSessionBeanRemote, reservationControllerRemote, currentEmployee);
                        
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** HoRS System :: Employee Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(email.length() > 0 && password.length() > 0)
        {
            currentEmployee = employeeControllerRemote.employeeLogin(email, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void menuMain() 
    {
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Hotel Management (HoRS) System ***\n");
            System.out.println("You are logged in as " + currentEmployee.getName() + " with " + currentEmployee.getAccessRight().toString() + " rights\n");
            System.out.println("1: System Administration");
            System.out.println("2: Hotel Operation");
            System.out.println("3: Front Office");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    systemAdminModule.menuSystemAdministration();
                }
                else if(response == 2)
                {
                    hotelOpModule.menuHotelOperation();
                }
                else if(response == 3)
                {
                    frontOfficeModule.menuFrontOffice();
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 4)
            {
                break;
            }
        }
    }
    
}
   
