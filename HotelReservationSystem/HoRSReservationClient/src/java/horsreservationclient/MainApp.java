/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.entity.stateful.RoomReservationSessionBeanRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Guest;
import entity.OnlineReservation;
import entity.ReservationLineItem;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;
import java.util.Scanner;
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
    
    private RoomReservationSessionBeanRemote roomReservationSessionBeanRemote;
    
    private List<Long> totalAmount;
    
    private Guest currentGuest;

    public MainApp() {
        totalAmount=new ArrayList<>();
    }

    public MainApp(GuestControllerRemote guestControllerRemote, RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote,RoomReservationSessionBeanRemote roomReservationSessionBeanRemote) {
        this();
        
        this.guestControllerRemote = guestControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomReservationSessionBeanRemote = roomReservationSessionBeanRemote;
    }
    
    public void runApp() throws GuestNotFoundException
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** Welcome to HoRS Hotel Reservation System ***\n");
            System.out.println("1: Gust Login");
            System.out.println("2: Exit\n");
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
                    }
                    catch(InvalidLoginCredentialException ex)
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if(response==2)
                {
                    break;
                }
                else
                {
                     System.out.println("Invalid option, please try again! ");
                }
            }
            
            if(response==2)
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
            menuMain();
        }
        else 
        {
            throw new InvalidLoginCredentialException("Missing login credential !");
        }      
    }
    
    private void menuMain() throws GuestNotFoundException
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** HoRS System ***\n");
            System.out.println("1: Register As Guest");
            System.out.println("2: Search Hotel Room");
            System.out.println("3: Reserve Hotel Room");
            System.out.println("4: View My Reservation Details");
            System.out.println("5: View All My Reservations");
            System.out.println("6: Exit\n");
            
            while(response<1 || response>6)
            {
                System.out.print("> ");
                response=scanner.nextInt();
                
                if(response==1)
                {
                    registerAsGuest();
                }
                else if(response==2)
                {
                    searchHotelRoom();
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
        System.out.print("Enter name > ");
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
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date checkInDate;
            Date checkOutDate;
            String roomType;

            System.out.println("\n*** HoRS System :: Search Hotel Room ***\n");
            System.out.print("Enter check in date (dd/mm/yyyy)> ");
            checkInDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter check out Date (dd/mm/yyyy)> ");
            checkOutDate = inputDateFormat.parse(scanner.nextLine().trim());            
            
            List<ReservationLineItem> reservationLineItems=roomReservationSessionBeanRemote.searchHotelRoom(checkInDate,checkOutDate);
            
            System.out.printf("%8s%22s   %s\n", "No.","Room Type", "Check in Date", "Check out Date", "Total amount");
            
            Integer number=0;
            for(ReservationLineItem reservationLineItem:reservationLineItems)
            {
                number++;
                totalAmount.add(roomReservationSessionBeanRemote.getTotalAmount(reservationLineItem));
                System.out.printf("%8s%22s   %s\n",number, reservationLineItem.getRoomType(), outputDateFormat.format(checkInDate), outputDateFormat.format(checkOutDate),totalAmount);
            }
            
            System.out.println("------------------------");
            System.out.println("1: Reserve Hotel Room");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                if(currentGuest != null)
                {
                    while(true)
                    {
                    
                        System.out.print("Select room number(press 0 to exit)> ");
                        Integer roomNumber=scanner.nextInt();
                        
                        if(roomNumber>=1&&roomNumber<=number)
                        {
                            System.out.println("\nTotal Amount is " + totalAmount.get(roomNumber));
                            OnlineReservation onlineReservation = roomReservationSessionBeanRemote.reserveRoom(currentGuest.getEmail(),reservationLineItems.get(roomNumber));
                            System.out.println("Reservation of room completed successfully!: " + onlineReservation.getReservationId() + "\n");
                        }
                        else if(roomNumber==0)
                        {
                            break;
                        }
                        else
                        {
                            System.out.println("Invalid option, please try again!");
                        }
                    }                       
                }
                else
                {
                    System.out.println("Please login first before making a reservation!\n");
                }
            }
        }
        catch(ParseException ex)
        {
            System.out.println("Invalid date input!\n");
        }
    }

    private void viewMyReservationDetails() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void viewAllMyReservations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
