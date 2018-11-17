/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.ExceptionReport;
import entity.NormalRate;
import entity.Partner;
import entity.PeakRate;
import entity.PromotionRate;
import entity.PublishedRate;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomRateException;
import util.exception.DeleteRoomTypeException;
import util.exception.GeneralException;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

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
    private static ReservationControllerRemote reservationControllerRemote;
    private Employee currentEmployee;
    
    public HotelOperationModule(){
        
    }
    
    public HotelOperationModule(EmployeeControllerRemote employeeControllerRemote, PartnerControllerRemote partnerControllerRemote, 
            RoomControllerRemote roomControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, 
            RoomRateControllerRemote roomRateControllerRemote, ReservationControllerRemote reservationControllerRemote, Employee currentEmployee)
    {
        this();
        this.employeeControllerRemote = employeeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        
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
            System.out.println("16: Allocate room");
            System.out.println("-----------------------");
            System.out.println("17: Back\n");
            response = 0;
            
            while(response < 1 || response > 16)
            {
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();

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
                    System.out.print("Enter Room Type Name> ");
                    String name = scanner.nextLine().trim();
                    try {
                        updateRoomType(roomTypeControllerRemote.retrieveRoomTypeByName(name));
                    } catch (RoomTypeNotFoundException ex) {
                        System.out.println("An error has occurred while retrieving Room Type details: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 4)
                {
                    System.out.print("Enter Room Type Name> ");
                    String name = scanner.nextLine().trim();
                    try {
                        deleteRoomType(roomTypeControllerRemote.retrieveRoomTypeByName(name));
                    } catch (RoomTypeNotFoundException ex) {
                        System.out.println("An error has occurred while retrieving Room Type details: " + ex.getMessage() + "\n");
                    }
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
                    System.out.print("Enter Room Rate Id> ");
                    Long rateId = scanner.nextLong();
                    scanner.nextLine();
                    try {
                        updateRoomRate(roomRateControllerRemote.retrieveRoomRateById(rateId, true));
                    } catch (RoomRateNotFoundException ex) {
                        System.out.println("An error has occurred while retrieving Room Rate details: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 14)
                {
                    System.out.print("Enter Room Rate Id> ");
                    Long rateId = scanner.nextLong();
                    scanner.nextLine();
                    try {
                        deleteRoomRate(roomRateControllerRemote.retrieveRoomRateById(rateId, true));
                    } catch (RoomRateNotFoundException ex) {
                        System.out.println("An error has occurred while retrieving Room Rate details: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 15)
                {
                    viewAllRoomRates();
                }
                else if (response == 16)
                {
                    allocateRoom();
                }
                else if (response == 17){
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 16)
            {
                break;
            }
        }
    }
    
    public void allocateRoom(){
        reservationControllerRemote.allocateRoomToCurrentDayReservations();
    }
    
    public void createRoomType() 
    {
        
        Scanner scanner = new Scanner(System.in);
        RoomType newRoomType = new RoomType();

        System.out.println("*** HoRS :: Hotel Management System :: Create New Room Type ***\n");
        System.out.print("Enter Name> ");
        newRoomType.setName(scanner.nextLine().trim());
        System.out.print("Enter Description> ");
        newRoomType.setDescription(scanner.nextLine().trim());
        System.out.print("Enter Room Size (square meters)> ");
        newRoomType.setRoomSize(scanner.nextInt());
        scanner.nextLine();
        System.out.print("Enter Bed> ");
        newRoomType.setBed(scanner.nextLine().trim());
        System.out.print("Enter Capacity> ");
        newRoomType.setCapacity(scanner.nextInt());
        scanner.nextLine();
        System.out.print("Enter Amenities> ");
        newRoomType.setAmenities(scanner.nextLine().trim());
        System.out.print("Enter Room Rank (1 is the highest)> ");
        newRoomType.setRoomRank(scanner.nextInt());
        scanner.nextLine();
        newRoomType.setStatus("enabled");
        
        newRoomType = roomTypeControllerRemote.createRoomType(newRoomType);
        System.out.println("New room type created successfully!: " + newRoomType.getRoomTypeId()+ "\n");
        System.out.print("Press any key to continue...> ");

    }

    public void viewRoomTypeDetails() 
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** HoRS :: Hotel Management System :: View Room Type Details ***\n");
        
        System.out.print("Enter Room Type Name> ");
        String name = scanner.nextLine().trim();
        
        try 
        {
            RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByName(name); 
            
            System.out.println("\n" + "Room Type name: " + roomType.getName());
            System.out.println("Room Type description: " + roomType.getDescription());
            System.out.println("Room Type size: " + roomType.getRoomSize());
            System.out.println("Room Type bed: " + roomType.getBed());
            System.out.println("Room Type capacity: " + roomType.getCapacity());
            System.out.println("Room Type amenities: " + roomType.getAmenities());
            System.out.println("Room Type status: " + roomType.getStatus());
            System.out.println("------------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                updateRoomType(roomType);
            }
            else if(response == 2)
            {
                deleteRoomType(roomType);
            }
        } 
        catch (RoomTypeNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving Room Type details: " + ex.getMessage() + "\n");
            System.out.print("Press any key to continue...> ");
        }
    }
    
    public void updateRoomType(RoomType roomType) 
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** HoRS :: Hotel Management System :: Update Room Type ***\n");
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setName(input);
        }
        System.out.print("Enter Description (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setDescription(input);
        }
        System.out.print("Enter Size - in square meters (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            while(true){
                try
                {
                    roomType.setRoomSize(Integer.parseInt(input));
                    break;
                } 
                catch (NumberFormatException e) 
                {
                    System.out.println("Please enter a number");
                }
            }
        }
        System.out.print("Enter Bed (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setBed(input);
        }
        System.out.print("Enter Capacity (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            while(true){
                try
                {
                    roomType.setCapacity(Integer.parseInt(input));
                    break;
                } 
                catch (NumberFormatException e) 
                {
                    System.out.println("Please enter a number");
                }
            }
        }
        System.out.print("Enter Amenities (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0)
        {
            roomType.setAmenities(input);
        }
        System.out.print("Enter Room Rank (blank if no change)> ");
        Integer roomRank = scanner.nextInt();
        if(roomRank != null)
        {
            roomType.setRoomRank(roomRank);
        }
        
        try 
        {
            roomTypeControllerRemote.updateRoomType(roomType);
            System.out.println("Room Type updated successfully!\n");
        } 
        catch (RoomTypeNotFoundException ex) 
        {
            System.out.println("An error has occurred while updating room type: " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoomType(RoomType roomType) 
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** HoRS :: Hotel Management System :: Delete Room Type ***\n");
        System.out.printf("Confirm Delete Room Type %s (Room Type ID: %d) (Enter 'Y' to Delete)> ", roomType.getName(), roomType.getRoomTypeId());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try 
            {
                roomTypeControllerRemote.deleteRoomType(roomType.getRoomTypeId());
                System.out.println("Room Type deleted successfully!\n");
            }
            catch (RoomTypeNotFoundException | DeleteRoomTypeException ex) 
            {
                System.out.println("An error has occurred while deleting Room Type: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            System.out.println("Room Type NOT deleted!\n");
        }
    }

    public void viewAllRoomTypes()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS :: Hotel Management System :: View All Room Type ***\n");
        
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();
        System.out.printf("%12s%20s%70s%6s%50s%10s%60s%10s\n", "Room Type Id", "Name", "Description", "Size", "Bed", "Capacity", "Amenities", "Status");

        for(RoomType roomType:roomTypes)
        {
            System.out.printf("%12s%20s%70s%6s%50s%10s%60s%10s\n", roomType.getRoomTypeId().toString(), roomType.getName(), roomType.getDescription(), roomType.getRoomSize(), roomType.getBed(), roomType.getCapacity(), roomType.getAmenities(), roomType.getStatus());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void createRoom()  //solve error with Room number primary key
    {
        try 
        {
            Scanner scanner = new Scanner(System.in);
            Room newRoom = new Room();
            
            System.out.println("*** HoRS :: Hotel Management System :: Create New Room ***\n");
            System.out.print("Enter Room Number (room floor + room number)> ");
            newRoom.setRoomId(scanner.nextLong());
            scanner.nextLine();
            System.out.print("Enter Room Type Id> ");
            Long roomTypeId = scanner.nextLong();
            RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeById(roomTypeId);
            System.out.println("Open room for room type: " + roomType.getName()+ "\n");
            newRoom.setRoomStatus("available");
            newRoom = roomControllerRemote.createRoom(newRoom, roomTypeId);
            System.out.println("New room created successfully!: " + newRoom.getRoomId()+ "\n");
        } 
        catch (RoomTypeNotFoundException ex) 
        {
            System.out.println(ex.getMessage() + "!\n");
        } 
        catch (RoomExistException | GeneralException ex) 
        {
            System.out.println("An error has occurred while creating the new room: " + ex.getMessage() + "!\n");
        }
    }

    public void updateRoom() 
    {
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            
            System.out.println("*** HoRS :: Hotel Management System :: Update Room ***\n");
            System.out.print("Enter Room Id> ");
            Long roomId = scanner.nextLong();
            scanner.nextLine();
            Room room = roomControllerRemote.retrieveRoomById(roomId, true, true);
            System.out.print("Enter Room Number (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                room.setRoomNumber(Integer.parseInt(input));
            }
            System.out.print("Enter Room Type name (blank if no change)> ");
            input = scanner.nextLine();
            String roomTypeName = "";
            if(input.length() > 0)
            {
                roomTypeName = input;
            }
            while(true)
            {
                System.out.print("Choose Room Status (1. available, 2.occupied)> ");
                Integer roomStatusInt = scanner.nextInt();
                
                if(roomStatusInt >= 1 && roomStatusInt <= 2)
                {
                    if(roomStatusInt == 1)
                    {
                        room.setRoomStatus("available");
                        Long reslineItemId = null;
                        roomControllerRemote.updateRoom(room, roomTypeName, reslineItemId);
                        
                        System.out.println("Room Type updated successfully!\n");
                        break;
                    }
                    else
                    { 
                        scanner.nextLine();
                        System.out.print("Enter Reservation Line Item Id> ");
                        Long resLineItemId = scanner.nextLong();
                        if(resLineItemId != null)
                        {
                            room.setRoomStatus("occupied");
                            roomControllerRemote.updateRoom(room, roomTypeName, resLineItemId);
                            System.out.println("Room Type updated successfully!\n");
                        }
                        else
                        {
                            System.out.println("Please enter a valid reservation line item Id.");
                        }
                        break;
                    }
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
        } catch (RoomNotFoundException | RoomTypeNotFoundException | ReservationLineItemNotFoundException ex) {
                System.out.println("An error has occurred while updating room : " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoom() 
    {
        try 
        {
            Scanner scanner = new Scanner(System.in);
            String input;
            
            System.out.println("*** HoRS :: Hotel Management System :: Delete Room ***\n");
            System.out.print("Enter Room Id> ");
            Long roomId = scanner.nextLong();
            scanner.nextLine();
            Room room = roomControllerRemote.retrieveRoomById(roomId, false, false);
            System.out.printf("Confirm Delete Room Number %d (Enter 'Y' to Delete)> ", room.getRoomId());
            input = scanner.nextLine().trim();
            
            if(input.equals("Y")) 
            {
                roomControllerRemote.deleteRoom(room.getRoomId());
                System.out.println("Room deleted successfully!\n");
            }
            else 
            {
                System.out.println("Room NOT deleted!\n");
            }
        } catch (RoomNotFoundException | DeleteRoomException ex) {
            System.out.println("An error has occurred while deleting Room : " + ex.getMessage() + "\n");
        }
    }

    public void viewAllRooms() 
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS :: Hotel Management System :: View All Rooms ***\n");
        
        List<Room> rooms = roomControllerRemote.retrieveAllRooms();
        System.out.printf("%12s%12s%20s%20s\n", "Room Number", "Room Status", "Room Type", "Room Reservation Id");
        try{
            for(Room room:rooms)
            {
                if(room.getReservation() != null){
                    System.out.printf("%12s%12s%20s%20s\n", room.getRoomId().toString(), room.getRoomStatus(),
                            room.getRoomType().getName(), room.getReservation().getReservationLineItemId().toString());
                }
                else
                {
                    System.out.printf("%12s%12s%20s%20s\n", room.getRoomId().toString(), room.getRoomStatus(),
                            room.getRoomType().getName(), "none");
                }
            }
        }catch(NullPointerException e) 
        { 
            System.out.print("NullPointerException Caught"); 
        } 
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void viewRoomAllocationExceptionReport() //incomplete
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS :: Hotel Management System :: View Room Allocation Exception Report ***\n");
        
        List<ExceptionReport> exReports = roomControllerRemote.generateRoomAllocationExceptionReport();
        System.out.printf("%20s%100s\n", "Exception Report Id", "Description");

        for(ExceptionReport exReport:exReports)
        {
            System.out.printf("%20s%100s\n", exReport.getExceptionReportId().toString(), exReport.getDescription());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

    public void createRoomRate() 
    {
        try 
        {
            Scanner scanner = new Scanner(System.in);
            RoomRate newRoomRate;
            
            System.out.println("*** HoRS :: Hotel Management System :: Create New Room Rate ***\n");
            while(true)
            {
                System.out.print("Select Room Rate Type (1. Normal Rate, 2. Published Rate, 3. Promotion Rate, 4. Peak Rate)> ");
                Integer roomRateTypeInt = scanner.nextInt();
                if(roomRateTypeInt >= 1 && roomRateTypeInt <= 4){
                    if(roomRateTypeInt == 1)
                    {
                        newRoomRate = new NormalRate();
                        break;
                    }else if(roomRateTypeInt == 2)
                    {
                        newRoomRate = new PublishedRate();
                        break;
                    }else if(roomRateTypeInt == 3)
                    {
                        newRoomRate = new PromotionRate();
                        break;
                    }else if(roomRateTypeInt == 4)
                    {
                        newRoomRate = new PeakRate();
                        break;
                    }
                    else
                    {
                        System.out.println("Sorry, this room rate type is currently not available. Please try again!\n");
                    }
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            scanner.nextLine();
            
            System.out.print("Enter Room Rate name> ");
            newRoomRate.setName(scanner.nextLine());
            
            System.out.print("Enter Room Rate per night> ");
            newRoomRate.setRatePerNight(scanner.nextBigDecimal());
            
            System.out.print("Enter Room Type Id> ");
            Long roomTypeId = scanner.nextLong();
            RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeById(roomTypeId);
            scanner.nextLine();
            
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(newRoomRate instanceof PromotionRate){
                System.out.print("Enter Promotion Rate start date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                ((PromotionRate) newRoomRate).setStartDate(date);
                System.out.print("Enter Promotion Rate end date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                ((PromotionRate) newRoomRate).setEndDate(date);
            }
            else if(newRoomRate instanceof PeakRate){
                System.out.print("Enter Peak Rate start date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                ((PeakRate) newRoomRate).setStartDate(date);
                System.out.print("Enter Peak Rate end date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                ((PeakRate) newRoomRate).setEndDate(date);
            }
            else if(newRoomRate instanceof NormalRate){
                System.out.print("Enter Normal Rate start date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                ((NormalRate) newRoomRate).setStartDate(date);
                System.out.print("Enter Normal Rate end date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                ((NormalRate) newRoomRate).setEndDate(date);
            }
            else if(newRoomRate instanceof PublishedRate){
                System.out.print("Enter Published Rate validity date (yyyy-MM-dd)> ");
                date = sdf.parse(scanner.nextLine());
                ((PublishedRate) newRoomRate).setValidity(date);
            }
            System.out.println("Open room rate for room type: " + roomType.getName()+ "\n");
            newRoomRate.setRoomRateStatus("enabled");
            newRoomRate = roomRateControllerRemote.createRoomRate(newRoomRate, roomTypeId);
            System.out.println("New room rate created successfully!: " + newRoomRate.getRoomRateId()+ "\n");
        } 
        catch (RoomTypeNotFoundException ex) 
        {
            System.out.println(ex.getMessage() + "!\n");
        } 
        catch (RoomExistException | GeneralException ex) 
        {
            System.out.println("An error has occurred while creating the new room rate: " + ex.getMessage() + "!\n");
        } catch (ParseException ex) {
            System.out.println("Invalid Date Format entered!" + "\n");
        }
    }

    public void viewRoomRateDetails() 
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("*** HoRS :: Hotel Management System :: View Room Rate Details ***\n");
        
        System.out.print("Enter Room Rate Id> ");
        Long roomRateId = scanner.nextLong();
        scanner.nextLine();
        
        try 
        {
            RoomRate roomRate = roomRateControllerRemote.retrieveRoomRateById(roomRateId, true); 
            
            System.out.println("\n" + "Room rate name: " + roomRate.getName());
            System.out.println("Room rate per night: " + roomRate.getRatePerNight().toString());
            System.out.println("Room rate type : " + roomRate.getRoomType().getName());
            System.out.println("Room rate status: " + roomRate.getRoomRateStatus());
            if(roomRate instanceof NormalRate){
                System.out.println("Room rate start date: " + ((NormalRate) roomRate).getStartDate().toString());
                System.out.println("Room rate end date: " + ((NormalRate) roomRate).getEndDate().toString());
            }
            else if(roomRate instanceof PublishedRate){
                System.out.println("Room rate validity date: " + ((PublishedRate) roomRate).getValidity().toString());
            }
            else if(roomRate instanceof PromotionRate)
            {
                System.out.println("Room rate start date: " + ((PromotionRate) roomRate).getStartDate().toString());
                System.out.println("Room rate end date: " + ((PromotionRate) roomRate).getEndDate().toString());
            }
            else if(roomRate instanceof PeakRate)
            {
                System.out.println("Room rate start date: " + ((PeakRate) roomRate).getStartDate().toString());
                System.out.println("Room rate end date: " + ((PeakRate) roomRate).getEndDate().toString());
            }
            System.out.println("------------------------");
            System.out.println("1: Update Room Rate");
            System.out.println("2: Delete Room Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                updateRoomRate(roomRate);
            }
            else if(response == 2)
            {
                deleteRoomRate(roomRate);
            }
        } 
        catch (RoomRateNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving Room Rate details: " + ex.getMessage() + "\n");
            System.out.print("Press any key to continue...> ");
        }
    }

    public void updateRoomRate(RoomRate roomRate) 
    {
        try {
            Scanner scanner = new Scanner(System.in);        
            String input;

            System.out.println("*** HoRS :: Hotel Management System :: Update Room Rate ***\n");
            System.out.print("Enter Name (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                roomRate.setName(input);
                }
            System.out.print("Enter Rate per night (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                roomRate.setRatePerNight(new BigDecimal(input));
            }
            System.out.print("Enter Rate's Room type name (blank if no change)> ");
            input = scanner.nextLine();
            String roomTypeName = "";
            if(input.length() > 0)
            {
                roomTypeName = input;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(roomRate instanceof NormalRate){
                System.out.print("Enter Rate Start date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    ((NormalRate) roomRate).setStartDate(sdf.parse(input));
                }
                System.out.print("Enter Rate End date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    ((NormalRate) roomRate).setEndDate(sdf.parse(input));
                }
            }
            else if(roomRate instanceof PublishedRate)
            {
                System.out.print("Enter Rate Validity date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    ((PublishedRate) roomRate).setValidity(sdf.parse(input));
                }
            }
            else if(roomRate instanceof PromotionRate)
            {
                System.out.print("Enter Rate Start date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    ((PromotionRate) roomRate).setStartDate(sdf.parse(input));
                }
                System.out.print("Enter Rate End date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    ((PromotionRate) roomRate).setEndDate(sdf.parse(input));
                }
            }
            else if(roomRate instanceof PeakRate)
            {
                System.out.print("Enter Rate Start date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    ((PeakRate) roomRate).setStartDate(sdf.parse(input));
                }
                System.out.print("Enter Rate End date (blank if no change)> ");
                input = scanner.nextLine().trim();
                if(input.length() > 0)
                {
                    ((PeakRate) roomRate).setEndDate(sdf.parse(input));
                }
            }
            if(roomRate.getRoomRateStatus().equals("disabled")){
                System.out.print("Room rate is currently disabled, enable it? (Enter 'Y' to Enable)> ");
                input = scanner.nextLine().trim();
                if(input.equals("Y")){
                    roomRate.setRoomRateStatus("enabled");
                }
            }
            roomRateControllerRemote.updateRoomRate(roomRate, roomTypeName);
            System.out.println("Room Rate updated successfully!\n");
            
        } catch (ParseException ex) {
            System.out.println("Invalid Date Format entered!" + "\n");
        } catch (RoomTypeNotFoundException | RoomNotFoundException | RoomRateNotFoundException ex) {
                System.out.println("An error has occurred while updating room rate: " + ex.getMessage() + "\n");
        }
    }

    public void deleteRoomRate(RoomRate roomRate) 
    {
        Scanner scanner = new Scanner(System.in);        
        String input;
        
        System.out.println("*** HoRS :: Hotel Management System :: Delete Room Rate ***\n");
        System.out.printf("Confirm Delete Room Rate %s (Room Rate ID: %d) (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try 
            {
                roomRateControllerRemote.deleteRoomRate(roomRate.getRoomRateId());
                System.out.println("Room Rate deleted successfully!\n");
            }
            catch (RoomRateNotFoundException | DeleteRoomRateException ex) 
            {
                System.out.println("An error has occurred while deleting Room Rate: " + ex.getMessage() + "\n");
            } 
        }
        else
        {
            System.out.println("Room Rate NOT deleted!\n");
        }
    }

    public void viewAllRoomRates() 
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS :: Hotel Management System :: View All Room Rates ***\n");
        
        List<RoomRate> roomRates = roomRateControllerRemote.retrieveAllRoomRates();
        
        for(RoomRate roomRate:roomRates)
        {
            System.out.printf("%12s%40s%16s%10s%15s%12s%12s%30s\n", "Room Rate Id", "Name", "Rate Per Night", "Status", "Room Type", "Start Date", "End Date", "Validity");
            if(roomRate instanceof PromotionRate)
            {
                System.out.printf("%12s%40s%16s%10s%15s%12s%12s%30s\n", roomRate.getRoomRateId().toString(), roomRate.getName(),
                        roomRate.getRatePerNight().toString(), roomRate.getRoomRateStatus(), roomRate.getRoomType().getName(),
                        ((PromotionRate) roomRate).getStartDate().toString(), ((PromotionRate) roomRate).getEndDate().toString(), "NA");
            }
            else if(roomRate instanceof PeakRate)
            {
                System.out.printf("%12s%40s%16s%10s%15s%12s%12s%30s\n", roomRate.getRoomRateId().toString(), roomRate.getName(),
                        roomRate.getRatePerNight().toString(), roomRate.getRoomRateStatus(), roomRate.getRoomType().getName(),
                        ((PeakRate) roomRate).getStartDate().toString(), ((PeakRate) roomRate).getEndDate().toString(), "NA");
            }
            else if(roomRate instanceof NormalRate)
            {
                System.out.printf("%12s%40s%16s%10s%15s%12s%12s%30s\n", roomRate.getRoomRateId().toString(), roomRate.getName(),
                        roomRate.getRatePerNight().toString(), roomRate.getRoomRateStatus(), roomRate.getRoomType().getName(),
                        ((NormalRate) roomRate).getStartDate().toString(), ((NormalRate) roomRate).getEndDate().toString(), "NA");
            }
            else if(roomRate instanceof PublishedRate)
            {
                System.out.printf("%12s%40s%16s%10s%15s%12s%12s%30s\n", roomRate.getRoomRateId().toString(), roomRate.getName(),
                        roomRate.getRatePerNight().toString(), roomRate.getRoomRateStatus(), roomRate.getRoomType().getName(),
                        "NA", "NA", ((PublishedRate) roomRate).getValidity().toString());
            }
        }
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
}
