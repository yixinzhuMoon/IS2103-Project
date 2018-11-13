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
import entity.Employee;
import entity.RoomType;
import java.util.Scanner;

/**
 *
 * @author casseylow
 */
public class HotelOperationModule {
    
    private EmployeeControllerRemote employeeControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    
    private Employee currentEmployee;
    
    public HotelOperationModule(){
        
    }
    
    public HotelOperationModule(EmployeeControllerRemote employeeControllerRemote, PartnerControllerRemote partnerControllerRemote, 
            RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, 
            RoomRateControllerRemote roomRateControllerRemote, Employee currentEmployee)
    {
        this();
        this.employeeControllerRemote = employeeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        
        this.currentEmployee = currentEmployee;
    }
    
    public void menuHotelOperation(){
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS :: Hotel Operation ***\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3: Update Room Type");
            System.out.println("4: Delete Room Type");
            System.out.println("5: View All Room Types");
            System.out.println("-----------------------");
            System.out.println("6: Create New Room");
            System.out.println("7: Update Room");
            System.out.println("8: Delete Room");
            System.out.println("9: View All Rooms");
            System.out.println("10: View Room Allocation Exception Report");
            System.out.println("-----------------------");
            System.out.println("11: Create New Room Rate");
            System.out.println("12: View Room Rate Details");
            System.out.println("13: Update Room Rate");
            System.out.println("14: Delete Room Rate");
            System.out.println("15: View All Room Rates");
            System.out.println("-----------------------");
            System.out.println("16: Allocate Room to Current Day Reservations");
            System.out.println("17: Back\n");
            response = 0;
            
            while(response < 1 || response > 17)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    createRoomType();
                }
                else if(response == 2)
                {
                    viewRoomTypeDetails();
                }
                else if(response == 3)
                {
                    updateRoomType();
                }
                else if(response == 4)
                {
                    deleteRoomType();
                }
                else if(response == 5)
                {
                    viewAllRoomTypes();
                }
                else if(response == 6)
                {
                    createRoom();
                }
                else if(response == 7)
                {
                    updateRoom();
                }
                else if(response == 8)
                {
                    deleteRoom();
                }
                else if(response == 9)
                {
                    viewAllRooms();
                }
                else if(response == 10)
                {
                    viewRoomAllocationExceptionReport();
                }
                else if(response == 11)
                {
                    createRoomRate();
                }
                else if(response == 12)
                {
                    viewRoomRateDetails();
                }
                else if(response == 13)
                {
                    updateRoomRate();
                }
                else if(response == 14)
                {
                    deleteRoomRate();
                }
                else if(response == 15)
                {
                    viewAllRoomRates();
                }
                else if(response == 16)
                {
                    allocateRoomToCurrentDayReservations();
                }
                else if (response == 17)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 17)
            {
                break;
            }
        }
    }
    
    
    public void createRoomType() {
        
        Scanner scanner = new Scanner(System.in);
        RoomType newRoomType = new RoomType();

        System.out.println("*** HoRS :: Hotel Management System :: Create New Room Type ***\n");
        System.out.print("Enter Name> ");
        newRoomType.setName(scanner.nextLine().trim());
        System.out.print("Enter Description> ");
        newRoomType.setDescription(scanner.nextLine().trim());
        System.out.print("Enter Room Size> ");
        newRoomType.setRoomSize(scanner.nextInt());
        System.out.print("Enter Bed> ");
        newRoomType.setBed(scanner.nextLine().trim());
        System.out.print("Enter Capacity> ");
        newRoomType.setCapacity(scanner.nextInt());
        System.out.print("Enter Amenities> ");
        newRoomType.setAmenities(scanner.nextLine().trim());
        newRoomType.setStatus("enabled");
        
        newRoomType = roomTypeControllerRemote.createRoomType(newRoomType);
        System.out.println("New room type created successfully!: " + newRoomType.getRoomTypeId()+ "\n");

    }

    public void viewRoomTypeDetails() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void updateRoomType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void deleteRoomType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void viewAllRoomTypes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void updateRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void deleteRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void viewAllRooms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void viewRoomAllocationExceptionReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void createRoomRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void viewRoomRateDetails() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void updateRoomRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void deleteRoomRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void viewAllRoomRates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void allocateRoomToCurrentDayReservations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
