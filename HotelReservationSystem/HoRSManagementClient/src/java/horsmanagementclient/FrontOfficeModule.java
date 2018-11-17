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
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.ReservationLineItem;
import entity.Room;
import entity.WalkInReservation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.EmployeeNotFoundException;
import util.exception.GuestNotFoundException;
import util.exception.TimeException;

/**
 *
 * @author casseylow
 */
public class FrontOfficeModule {
    
    private EmployeeControllerRemote employeeControllerRemote;
    private GuestControllerRemote guestControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    
    private WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote;
    
    private Employee currentEmployee;
    private List<Long> totalAmount;
    private List<ReservationLineItem> searchResults;
    
    public FrontOfficeModule(){
        
    }
    
    public FrontOfficeModule(EmployeeControllerRemote employeeControllerRemote, GuestControllerRemote guestControllerRemote, PartnerControllerRemote partnerControllerRemote, 
            RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, 
            RoomRateControllerRemote roomRateControllerRemote, WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote,
            Employee currentEmployee)
    {
        this();
        this.employeeControllerRemote = employeeControllerRemote;
        this.guestControllerRemote = guestControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.walkInReservationSessionBeanRemote = walkInReservationSessionBeanRemote;
        
        this.currentEmployee = currentEmployee;
    }
    
    public void menuFrontOffice(){
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS :: Front Office ***\n");
            System.out.println("1: Walk-in Search Room");
            System.out.println("2: Walk-in Reserve Room");
            System.out.println("-----------------------");
            System.out.println("3: Check-in Guest");
            System.out.println("4: Check-out Guest");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    walkInSearchRoom();
                }
                else if(response == 2)
                {
//                    reserveRoom();
                }
                else if(response == 3)
                {
                    checkInGuest();
                }
                else if(response == 4)
                {
                    checkOutGuest();
                }
                else if(response == 5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 5)
            {
                break;
            }
        }
    }
    
    public void walkInSearchRoom() 
    {
        totalAmount=new ArrayList<>();
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date checkInDate;
            Date checkOutDate;
            String roomType;
            
            System.out.println("*** HoRS :: Hotel Management System :: Walk-in Search Room ***\n");
            System.out.print("Enter check in date (dd/mm/yyyy)> ");
            checkInDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter check out Date (dd/mm/yyyy)> ");  
            checkOutDate = inputDateFormat.parse(scanner.nextLine().trim());
            
            searchResults = walkInReservationSessionBeanRemote.walkInSearchHotelRoom(checkInDate, checkOutDate);    
            System.out.println("Showing rooms available for Walk-in Reservation");
            System.out.printf("%11s%20s%15%15s%14s%\n", "Room Number","Room Type", "Check in Date", "Check out Date", "Total amount");

            Integer number=0;
            for(ReservationLineItem reservationLineItem: searchResults){
                number++;
                totalAmount.add(walkInReservationSessionBeanRemote.getTotalAmount());
                System.out.printf("%11s%20s%15%15s%14s%\n", number, reservationLineItem.getRoomType().getName(), outputDateFormat.format(checkInDate), 
                        outputDateFormat.format(checkOutDate), walkInReservationSessionBeanRemote.getTotalAmount());
            }
            
            System.out.println("------------------------");
            System.out.println("1: Reserve Hotel Room");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                reserveRoom(number, searchResults);
            }
        } catch (ParseException ex) {
            System.out.println("Invalid Date Format entered!" + "\n");
        }
    }

    public void reserveRoom(Integer numberOfRoomsInSearchResult, List<ReservationLineItem> searchResults)
    {
        this.searchResults = searchResults;
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS :: Hotel Management System :: Walk-in Reserve Room ***\n");
        if(!searchResults.isEmpty()){
            try{
                while(true)
                {
                    System.out.print("Select room number(press 0 to exit)> ");
                    Integer roomNumber=scanner.nextInt();
                    scanner.nextLine();
                    if(roomNumber>=1&&roomNumber<=numberOfRoomsInSearchResult)
                    {
                        System.out.println("\nTotal Amount is " + totalAmount.get(roomNumber));
                        WalkInReservation walkInReservation = walkInReservationSessionBeanRemote.reserveRoom(currentEmployee.getEmail(), searchResults.get(roomNumber));
                        System.out.println("Reservation of room completed successfully!: " + walkInReservation.getReservationId() + "\n");

                    }
                    else if(roomNumber==0)
                    {
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid option, please try again!");
                    }

                    System.out.print("Would you like to reserve another room? (Enter 'Y' to continue)> ");
                    String choice = scanner.nextLine().trim();
                    if(!choice.equals("Y")){
                        break;
                    }
                }                       
            } 
            catch (EmployeeNotFoundException ex) 
            {
                System.out.println("Please login first before making a reservation!\n");
            }
        }
        else
        {
            System.out.println("No Rooms available for reservation.");
        }
    }
    
    public void checkInGuest() 
    {
        Long guestId = null;
        try 
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** HoRS :: Hotel Management System :: Check-in Guest ***\n");
            System.out.print("Enter Guest id>");
            guestId = scanner.nextLong();
            List<Room> roomsCheckedIn = guestControllerRemote.checkInGuest(guestId);
            System.out.println("Guest " + guestId.toString() + " checked in successfully to the following rooms: ");
            for(Room room:roomsCheckedIn)
            {
                System.out.println("Room Number: " + room.getRoomNumber());
            }
            scanner.nextLine();
        }   
        catch (GuestNotFoundException | TimeException ex) 
        {
            System.out.println("An error has occurred while checking in guest: " + guestId.toString() + ex.getMessage() + "\n");
        }
    }

    public void checkOutGuest() 
    {
        try 
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** HoRS :: Hotel Management System :: Check-out Guest ***\n");
            System.out.print("Enter Guest id>");
            Long guestId = scanner.nextLong();
            List<Room> roomsCheckedOut = guestControllerRemote.checkOutGuest(guestId);
            System.out.println("Guest " + guestId.toString() + " checked out successfully from the following rooms: ");
            for(Room room:roomsCheckedOut)
            {
                System.out.println("Room Number: " + room.getRoomNumber());
            }
            scanner.nextLine();
        } 
        catch (GuestNotFoundException | TimeException ex) 
        {
            System.out.println("An error has occurred while checking out guest: " + ex.getMessage() + "\n");
        }
    }
}
 