/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.InvalidLoginCredentialException_Exception;
import ws.Partner;
import ws.PartnerReservation;
import ws.ReservationLineItem;

/**
 *
 * @author haiyan
 */
class MainApp {
    
    private List<Long> totalAmount;
    
    private Partner currentPartner;
    List<ReservationLineItem> reservationLineItems;

    public MainApp() {
        totalAmount=new ArrayList<>();
        reservationLineItems=new ArrayList<>();
    }
    
    public void runApp() throws InvalidLoginCredentialException_Exception, DatatypeConfigurationException
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2: Exit\n");
            response=0;
            
            while(response<1 ||response>2)
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
        
        System.out.println("\n*** Holiday System :: Partner Login ***\n");
        System.out.print("Enter email> ");
        email=scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password=scanner.nextLine().trim();
        
        if(email.length()>0 && password.length()>0)
        {
            currentPartner= partnerLogin(email,password);
            System.out.println("Gust Login successful !\n");
        }    
    }
    
    private void menuMain() throws DatatypeConfigurationException 
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** HoRS System ***\n");
            System.out.println("1: Partner Search Hotel Room");
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
            System.out.println("4: Exit\n");
            
            while(response<1 || response>5)
            {
                System.out.print("> ");
                response=scanner.nextInt();
                
                if(response==1)
                {
                    partnerSearchHotelRoom();
                }
                else if(response==2)
                {
                    viewPartnerReservationDetails();
                }
                else if(response==3)
                {
                    viewAllPartnerReservations();
                }
                else if(response==4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again! ");
                } 
            }
            
            if(response==4)
            {
                break;
            }
        }
    }

     private void partnerSearchHotelRoom() throws DatatypeConfigurationException 
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
            
            GregorianCalendar gcal = null;
            XMLGregorianCalendar xgcal1;
            XMLGregorianCalendar xgcal2;

            System.out.println("\n*** HoRS System :: Search Hotel Room ***\n");
            System.out.print("Enter check in date (dd/mm/yyyy)> ");
            checkInDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter check out Date (dd/mm/yyyy)> ");
            checkOutDate = inputDateFormat.parse(scanner.nextLine().trim());

            gcal.setTime(checkInDate);
            xgcal1= DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            
            gcal.setTime(checkOutDate);
            xgcal2= DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            
            reservationLineItems=searchHotelRoom(xgcal1,xgcal2);
            
             System.out.printf("%11s%20s%15%15s%14s%\n", "Room Number","Room Type", "Check in Date", "Check out Date", "Total amount");

            Integer number=0;
            for(ReservationLineItem reservationLineItem:reservationLineItems)
            {
                number++;
                totalAmount.add(getTotalAmount(reservationLineItem));
                System.out.printf("%11s%20s%15%15s%14s%%s\n",number, reservationLineItem.getRoomType(), outputDateFormat.format(checkInDate), outputDateFormat.format(checkOutDate),totalAmount);
            }
            
            System.out.println("------------------------");
            System.out.println("1: Reserve Hotel Room");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                if(currentPartner != null)
                {
                    while(true)
                    {
                    
                        System.out.print("Select room number(press 0 to exit)> ");
                        Integer roomNumber=scanner.nextInt();
                        
                        if(roomNumber>=1&&roomNumber<=number)
                        {
                            System.out.println("\nTotal Amount is " + totalAmount.get(roomNumber));
                            PartnerReservation partnerReservation = reservePartnerRoom(currentPartner.getEmail(),reservationLineItems.get(roomNumber));
                            System.out.println("Reservation of room completed successfully!: " + partnerReservation.getReservationId() + "\n");
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
     



    private void viewPartnerReservationDetails() {
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("\n *** HoRS System :: View My Reservation Details ***\n");
        System.out.print("Enter reservation id> ");
        Long reservationId=scanner.nextLong();
        
        PartnerReservation onlineReservation=retrievePartnerReservationById(reservationId);
        System.out.printf("%8s%22s%\n", "Reservation Id","Reservation Date");
        System.out.printf("%8s%22s%\n", onlineReservation.getReservationId(),onlineReservation.getReservationDate());
        
    }

    private void viewAllPartnerReservations() {
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("\n*** HoRS System :: View All My Reservations ***\n");
        
        List<PartnerReservation> partnerReservations =retrieveAllPartnerReservations();
        System.out.printf("%8s%20s%\n", "Reservation Id", "Reservation Date");

        for(PartnerReservation partnerReservation: partnerReservations)
        {
            System.out.printf("%8s%20s%\n",partnerReservation.getReservationId(),partnerReservation.getReservationDate());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    

    
    
    
    
    
    
 private static Long getTotalAmount(ws.ReservationLineItem arg0) {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.getTotalAmount(arg0);
    }

    private static ws.Partner partnerLogin(java.lang.String arg0, java.lang.String arg1) throws ws.InvalidLoginCredentialException_Exception {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.partnerLogin(arg0, arg1);
    }

    private static ws.PartnerReservation reservePartnerRoom(java.lang.String arg0, ws.ReservationLineItem arg1) {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.reservePartnerRoom(arg0, arg1);
    }

    private static java.util.List<ws.PartnerReservation> retrieveAllPartnerReservations() {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.retrieveAllPartnerReservations();
    }

    private static ws.PartnerReservation retrievePartnerReservationById(java.lang.Long arg0) {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.retrievePartnerReservationById(arg0);
    }

    private static java.util.List<ws.ReservationLineItem> searchHotelRoom(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.searchHotelRoom(arg0, arg1);
    }
    
    
    
}
