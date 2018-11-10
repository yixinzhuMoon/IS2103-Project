/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Guest;
import exception.GuestNotFoundException;
import exception.InvalidLoginCredentialException;
import java.util.Scanner;

/**
 *
 * @author Zhu Yixin
 */
class MainApp {
    private GuestControllerRemote guestControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    
    private Guest currentGuest;

    public MainApp() {
    }

    public MainApp(GuestControllerRemote guestControllerRemote, RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote) {
        this.guestControllerRemote = guestControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
    }
    
    public void runApp() throws GuestNotFoundException
    {
        Scanner scanner=new Scanner(System.in);
        Integer response=0;
        
        while(true)
        {
            System.out.println("\n*** Welcome to HoRS Reservatoion System ***\n");
            System.out.println("1.Gust Login");
            System.out.println("2.Exit\n");
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
                        System.out.println("Gust Login successful !\n");
                        menuMain();
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
        
        System.out.println("\n*** HoRS System :: Gust Login ***\n");
        System.out.print("Enter email> ");
        email=scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password=scanner.nextLine().trim();
        
        if(email.length()>0 && password.length()>0)
        {
            currentGuest= guestControllerRemote.guestLogin(email,password);
        }
        else 
        {
            throw new InvalidLoginCredentialException("Missing login credential !");
        }
            
    }
    
}
