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
import entity.OnlineReservation;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomType;
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
import util.exception.RoomTypeNotFoundException;
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
    private ReservationControllerRemote reservationControllerRemote;
    
    private WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote;
    
    private Employee currentEmployee;
    
    public FrontOfficeModule(){
        
    }
    
    public FrontOfficeModule(EmployeeControllerRemote employeeControllerRemote, GuestControllerRemote guestControllerRemote, PartnerControllerRemote partnerControllerRemote, 
            RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, 
            RoomRateControllerRemote roomRateControllerRemote, WalkInReservationSessionBeanRemote walkInReservationSessionBeanRemote,
            ReservationControllerRemote reservationControllerRemote,
            Employee currentEmployee)
    {
        this();
        this.reservationControllerRemote = reservationControllerRemote;
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
                    reserveRoom();
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
        try
        {
            Scanner scanner = new Scanner(System.in);
            Date checkInDate;
            Date checkOutDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            System.out.println("\n*** HoRS System :: Walk-in Search Room ***\n");
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkInDate = sdf.parse(scanner.nextLine());
            System.out.print("Enter check out Date (yyyy-MM-dd)> ");
            checkOutDate = sdf.parse(scanner.nextLine()); 
            
            int roomLeft=0;
            
            for(RoomType roomType:roomTypeControllerRemote.retrieveAllEnabledRoomTypes())
            {
                if(!roomType.getRooms().isEmpty())
                {
                    roomLeft=roomType.getRooms().size();
                }
                for(Room room:roomType.getRooms())
                {
                    if(!room.getRoomStatus().equals("available"))
                    {
                        roomLeft--;
                    }
            }
            
            for(ReservationLineItem reservationLineItem:roomType.getReservationLineItems())
            {
                if(!isWithinRange(reservationLineItem.getCheckInDate(), reservationLineItem.getCheckOutDate(), checkInDate, checkOutDate))
                {
                    roomLeft--;
                }
            }
            
            if(roomLeft>0)
            {
                System.out.println(roomType.getName()+" has "+roomLeft+" rooms left");
            }
            else
            {
                System.out.println(roomType.getName()+" has no rooms left ");
            }   
        }
        }
        catch(ParseException ex)
        {
            System.out.println("Invalid date input!\n");
        }
    }

    public void reserveRoom()
    {
    
        WalkInReservation walkInReservation = new WalkInReservation();
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date checkInDate;
        Date checkOutDate;
        
        System.out.println("*** HoRS :: Hotel Management System :: Walk-in Reserve Room ***\n");
        try{
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkInDate = sdf.parse(scanner.nextLine()); 
            System.out.print("Enter check out date (yyyy-MM-dd)> ");
            checkOutDate = sdf.parse(scanner.nextLine());
            System.out.print("Enter Room Type> ");
            String roomType= scanner.nextLine().trim();
            System.out.print("Enter Number of Room> ");
            Integer numberOfRooms=scanner.nextInt();


            Long totalAmount=walkInReservationSessionBeanRemote.totalAmount(roomType,numberOfRooms,checkInDate,checkOutDate);
            if(totalAmount.equals(new Long(0)))
            {
                System.out.println("Not enough room to reserve");
            }
            else
            {
                System.out.println("Reserve successful! total amount is "+totalAmount);

            for(int i=0;i<numberOfRooms;i++)
            {
                walkInReservation = reservationControllerRemote.createWalkInReservation(walkInReservation, currentEmployee.getEmployeeId());
                walkInReservation.getReservationLineItems().add(reservationControllerRemote.createWalkInReservationLineItem(checkInDate, checkOutDate, roomType));
            }
            }
        }  catch (ParseException ex) {
            System.out.println("Invalid Date Format entered!" + "\n");
        } catch (RoomTypeNotFoundException | EmployeeNotFoundException ex) {
            System.out.println("An error ocurred when reserving room: " + ex.getMessage() + "\n");
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
    
    public boolean isWithinRange(Date startDate, Date endDate,Date checkInDate, Date checkOutDate) {
        return !(startDate.after(checkInDate) || endDate.before(checkOutDate));
    }
    
}
 