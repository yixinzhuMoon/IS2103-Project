/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceRef;
import ws.client.HolidayReservationWebService_Service;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.Partner;
import ws.client.PartnerReservation;
import ws.client.ReservationLineItem;
import ws.client.ReservationLineItemNotFoundException_Exception;
import ws.client.Room;
import ws.client.RoomType;
import ws.client.RoomTypeNotFoundException_Exception;

/**
 *
 * @author haiyan
 */
class MainApp {
    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/HolidayReservationWebService/HolidayReservationWebService.wsdl")
    private static HolidayReservationWebService_Service service;
    
    
    private Partner currentPartner;

    public MainApp() {
    }

    public void runApp() throws InvalidLoginCredentialException_Exception, ParseException, RoomTypeNotFoundException_Exception, DatatypeConfigurationException, ReservationLineItemNotFoundException_Exception 
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2: Exit\n");
            response=0;
            
            while(response<1 ||response>1)
            {
                System.out.print("> ");
                response=scanner.nextInt();
                
                if(response==1)
                {
                    doLogin();
                    menuMain();
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
    
    private void doLogin() throws InvalidLoginCredentialException_Exception
    {
        Scanner scanner=new Scanner(System.in);
        String email="";
        String password="";
        String name="";
        
        System.out.println("\n*** Holiday Reservtation System :: Partner Login ***\n");
        System.out.print("Enter name");
        name=scanner.nextLine().trim();
        System.out.print("Enter email> ");
        email=scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password=scanner.nextLine().trim();
        
        if(email.length()>0 && password.length()>0)
        {
            currentPartner=partnerLogin(email,password);
            currentPartner.setName(name);
            System.out.println("Partner Login successful !\n");
        }   
    }
    
    private void menuMain() throws ParseException, RoomTypeNotFoundException_Exception, DatatypeConfigurationException, ReservationLineItemNotFoundException_Exception
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** Holiday System ***\n");
            System.out.println("1: Partner Search Room");
            System.out.println("2: Partner Reserve Room ");
            System.out.println("3: View Partner Reservation Details");
            System.out.println("4: View All Partner Reservations");
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

    private void searchHotelRoom()
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            Date checkInDate;
            Date checkOutDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            System.out.println("\n*** Holiday System :: Partner Search Room ***\n");
            System.out.print("Enter check in date (yyyy-MM-dd)> ");
            checkInDate = sdf.parse(scanner.nextLine());
            System.out.print("Enter check out Date (yyyy-MM-dd)> ");
            checkOutDate = sdf.parse(scanner.nextLine()); 
            
            int roomLeft=0;
            
            for(RoomType roomType:retrieveAllEnabledRoomTypes())
            {
                roomLeft=roomType.getRooms().size();
                for(Room room:roomType.getRooms())
                {
                    if(!room.getRoomStatus().equalsIgnoreCase("available"))
                    {
                        roomLeft--;
                    }
            }
            
            for(ReservationLineItem reservationLineItem:roomType.getReservationLineItems())
            {
                if(!isWithinRange(reservationLineItem.getCheckInDate().toGregorianCalendar().getTime(), reservationLineItem.getCheckOutDate().toGregorianCalendar().getTime(), checkInDate, checkOutDate))
                {
                    
                    roomLeft--;
                }
            }
            
            if(roomLeft>0)
            {
                System.out.print(roomType.getName()+"has "+roomLeft+" rooms left");
            }
            else
            {
                System.out.println(roomType.getName()+"has no rooms left ");
            }   
        }
        }
        catch(ParseException ex)
        {
            System.out.println("Invalid date input!\n");
        }
    }
     
    private void reserveHotelRoom() throws ParseException, RoomTypeNotFoundException_Exception, DatatypeConfigurationException
    {
         Scanner scanner = new Scanner(System.in);
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         Date checkInDate;
         Date checkOutDate;
        
        System.out.println("\n*** Holiday Reservation System :: Reserve Hotel Room ***\n");
        System.out.print("Enter check in date (yyyy-MM-dd)> ");
        checkInDate = sdf.parse(scanner.nextLine());
        System.out.print("Enter check out Date (yyyy-MM-dd)> ");
        checkOutDate = sdf.parse(scanner.nextLine());
        System.out.print("Enter Room Type> ");
        String roomType= scanner.nextLine().trim();
        System.out.print("Enter Number of Room> ");
        Integer roomNumber=scanner.nextInt();
        
        
        Long totalAmount=totalAmount(roomType,roomNumber,toXMLGregorianCalendar(checkInDate),toXMLGregorianCalendar(checkOutDate));
        if(totalAmount.equals(new Long(0)))
        {
            System.out.println("Not enough room to reserve");
        }
        else
        {
            System.out.println("Reserve successful! totalAMount is"+totalAmount);
            PartnerReservation PartnerReservation=createPartnerReservation(currentPartner);
        
        for(int i=0;i<roomNumber;i++)
        {
          
           PartnerReservation.getReservationLineItems().add(createReservationLineItem(toXMLGregorianCalendar(checkInDate), toXMLGregorianCalendar(checkOutDate), roomType));
        }
        
        }
        
    }

    private void viewMyReservationDetails() throws ReservationLineItemNotFoundException_Exception {
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("\n *** Holiday Reservation System :: View My Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Long reservationId=scanner.nextLong();
        
        ReservationLineItem reservationLineItem=retrieveReservationLineItemById(reservationId);
        System.out.printf("%20s%20s%20s%20s%20s\n", "Reservation Id","Reservation Check In Date","Reservation Check In Date","Room Type","Room Rate");
        System.out.printf("%20s%20s%20s%20s%20s\n",reservationLineItem.getReservationLineItemId(), reservationLineItem.getCheckInDate(),reservationLineItem.getCheckOutDate(),reservationLineItem.getRoomType().getName(),reservationLineItem.getRoomRate().getName());
        
    }

    private void viewAllMyReservations() {
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("\n*** HoRS System :: View All My Reservations ***\n");
        
        System.out.printf("%20s%20s%20s%20s%20s\n", "Reservation Id","Reservation Check In Date","Reservation Check Out Date","Room Type","Room Rate");
        List<ReservationLineItem> reservationLineItems=retrieveAllReservationLineItems();
        
        for(ReservationLineItem reservationLineItem: reservationLineItems)
        {
            System.out.printf("%20s%20s%20s%20s%20s\n",reservationLineItem.getReservationLineItemId(), reservationLineItem.getCheckInDate(),reservationLineItem.getCheckOutDate(),reservationLineItem.getRoomType().getName(),reservationLineItem.getRoomRate().getName());
        } 
        
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
        
    }
    
    public boolean isWithinRange(Date startDate, Date endDate,Date checkInDate, Date checkOutDate) {
        return !(startDate.after(checkInDate) || endDate.before(checkOutDate));
    }
    
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) throws DatatypeConfigurationException{
        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(date);
        XMLGregorianCalendar xmlCalendar = null;

        xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
        return xmlCalendar;
    }


 
    private static Partner partnerLogin(java.lang.String arg0, java.lang.String arg1) throws InvalidLoginCredentialException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.partnerLogin(arg0, arg1);
    }
    private static java.util.List<ws.client.RoomType> retrieveAllEnabledRoomTypes() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.retrieveAllEnabledRoomTypes();
    }
    private static Long totalAmount(java.lang.String arg0, java.lang.Integer arg1, javax.xml.datatype.XMLGregorianCalendar arg2, javax.xml.datatype.XMLGregorianCalendar arg3) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.totalAmount(arg0, arg1, arg2, arg3);
    }
    
     private static ReservationLineItem createReservationLineItem(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1, java.lang.String arg2) throws RoomTypeNotFoundException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.createReservationLineItem(arg0, arg1, arg2);
    }
     
     private static java.util.List<ws.client.ReservationLineItem> retrieveAllReservationLineItems() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.retrieveAllReservationLineItems();
    }
     
      private static PartnerReservation createPartnerReservation(ws.client.Partner arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.createPartnerReservation(arg0);
    }
      private static ReservationLineItem retrieveReservationLineItemById(java.lang.Long arg0) throws ReservationLineItemNotFoundException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.retrieveReservationLineItemById(arg0);
    }
}
