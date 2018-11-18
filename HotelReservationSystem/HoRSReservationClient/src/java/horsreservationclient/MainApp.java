/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.entity.stateful.RoomReservationSessionBeanRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Guest;
import entity.OnlineReservation;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;
import java.util.Scanner;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.helper.BigDecimalHelper;

/**
 *
 * @author Zhu Yixin
 */
class MainApp {
    private GuestControllerRemote guestControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    
    private RoomReservationSessionBeanRemote roomReservationSessionBeanRemote;
    
    private OnlineReservation onlineReservation=new OnlineReservation();
    
    private Guest currentGuest;

    public MainApp() {
    }

    public MainApp(GuestControllerRemote guestControllerRemote, RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote,RoomReservationSessionBeanRemote roomReservationSessionBeanRemote,ReservationControllerRemote reservationControllerRemote) {
        this();
        
        this.guestControllerRemote = guestControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomReservationSessionBeanRemote = roomReservationSessionBeanRemote;
        this.reservationControllerRemote=reservationControllerRemote;
    }
    
    public void runApp() throws GuestNotFoundException, ParseException, RoomTypeNotFoundException, ReservationLineItemNotFoundException
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** Welcome to HoRS Hotel Reservation System ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Exit\n");
            response=0;
            
            while(response<1 ||response>2)
            {
                System.out.print("> ");
                response=scanner.nextInt();
                
                if(response==1)
                {
                    try
                    {
                        doLogin();
                        menuMain();
                    }
                    catch(InvalidLoginCredentialException ex)
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if(response==2)
                {
                    registerAsGuest();
                    menuMain();
                }
                else if(response==3)
                {
                    break;
                }
                else
                {
                     System.out.println("Invalid option, please try again! ");
                }
            }
            
            if(response==3)
            {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException, GuestNotFoundException
    {
        Scanner scanner=new Scanner(System.in);
        String email="";
        String password="";
        
        System.out.println("\n*** HoRS System :: Guest Login ***\n");
        System.out.print("Enter email> ");
        email=scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password=scanner.nextLine().trim();
        
        if(email.length()>0 && password.length()>0)
        {
            currentGuest= guestControllerRemote.guestLogin(email,password);
            System.out.println("Gust Login successful !\n");
        }
        else 
        {
            throw new InvalidLoginCredentialException("Missing login credential !");
        }      
    }
    
    private void menuMain() throws GuestNotFoundException, ParseException, RoomTypeNotFoundException, ReservationLineItemNotFoundException
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** HoRS System ***\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: Reserve Hotel Room ");
            System.out.println("3: View My Reservation Details");
            System.out.println("4: View All My Reservations");
            System.out.println("5: Exit\n");
            response = 0;
            
            while(response<1 || response>4)
            {
                System.out.print("> ");
                response = scanner.nextInt();
                scanner.nextLine();
                
                if(response==1)
                {
                    searchHotelRoom();
                }
                else if(response==2)
                {
                    reserveHotelRoom();
                }
                else if(response==3)
                {
                    viewMyReservationDetails();
                }
                else if(response==4)
                {
                    viewAllMyReservations();
                }
                else if(response==5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again! ");
                } 
            }
            
            if(response==5)
            {
                break;
            }
        }
    }

    private void registerAsGuest() 
    {
        Scanner scanner=new Scanner(System.in);
        Guest newGuest=new Guest();
        
        System.out.println("\n*** HoRS System :: Register As Guest ***\n");
        System.out.print("ENter name >");
        newGuest.setName(scanner.nextLine().trim());
        System.out.print("Enter email > ");
        newGuest.setEmail(scanner.nextLine().trim());
        System.out.print("Enter password > ");
        newGuest.setPassword(scanner.nextLine().trim());
        
        Long guestId=guestControllerRemote.createGuest(newGuest);
        System.out.println("Visitor registered as guest "+guestId+" successfully! ");
    }

     private void searchHotelRoom() throws GuestNotFoundException
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            Date checkInDate;
            Date checkOutDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            System.out.println("\n*** HoRS System :: Search Hotel Room ***\n");
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
                System.out.print(roomType.getName()+" has "+roomLeft+" rooms left");
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
     
    private void reserveHotelRoom() throws ParseException, RoomTypeNotFoundException
    {
         Scanner scanner = new Scanner(System.in);
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         Date checkInDate;
         Date checkOutDate;
        
        System.out.println("\n*** HoRS System :: Reserve Hotel Room ***\n");
        System.out.print("Enter check in date (yyyy-MM-dd)> ");
        checkInDate = sdf.parse(scanner.nextLine());
        System.out.print("Enter check out Date (yyyy-MM-dd)> ");
        checkOutDate = sdf.parse(scanner.nextLine());
        System.out.print("Enter Room Type> ");
        String roomType= scanner.nextLine().trim();
        System.out.print("Enter Number of Room> ");
        Integer roomNumber=scanner.nextInt();
        
        
        Long totalAmount=roomReservationSessionBeanRemote.totalAmount(roomType,roomNumber,checkInDate,checkOutDate);
        if(totalAmount.equals(new Long(0)))
        {
            System.out.println("Not enough room to reserve");
        }
        else
        {
            System.out.println("Reserve successful! total amount is"+totalAmount);
        
            for(int i=0;i<roomNumber;i++)
            {
               onlineReservation.getReservationLineItems().add(reservationControllerRemote.createReservationLineItem(checkInDate, checkOutDate, roomType));
            }
        }
        
    }

    private void viewMyReservationDetails() throws ReservationLineItemNotFoundException {
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("\n *** HoRS System :: View My Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Long reservationId=scanner.nextLong();
        
        ReservationLineItem reservationLineItem=reservationControllerRemote.retrieveReservationLineItemById(reservationId);
        System.out.printf("%15s%15s%15s%15s%15s\n", "Reservation Id","Reservation Check In Date","Reservation Check In Date","Room Type","Room Rate");
        System.out.printf("%15s%15s%15s%15s%15s\n",reservationLineItem.getReservationLineItemId(), reservationLineItem.getCheckInDate(),reservationLineItem.getCheckOutDate(),reservationLineItem.getRoomType(),reservationLineItem.getRoomRate());
        
    }

    private void viewAllMyReservations() {
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("\n*** HoRS System :: View All My Reservations ***\n");
        
        List<ReservationLineItem> reservationLineItems = reservationControllerRemote.retrieveAllReservationLineItem(onlineReservation);
        System.out.printf("%15s%15s%15s%15s%15s\n", "Reservation Id","Reservation Check In Date","Reservation Check In Date","Room Type","Room Rate");
        
        for(ReservationLineItem reservationLineItem: reservationLineItems)
        {
            System.out.printf("%15s%15s%15s%15s%15s\n",reservationLineItem.getReservationLineItemId(), reservationLineItem.getCheckInDate(),reservationLineItem.getCheckOutDate(),reservationLineItem.getRoomType(),reservationLineItem.getRoomRate());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
        
    }
    
    public boolean isWithinRange(Date startDate, Date endDate,Date checkInDate, Date checkOutDate) {
        return !(startDate.after(checkInDate) || endDate.before(checkOutDate));
    }
    
}
