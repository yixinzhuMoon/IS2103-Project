/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRight;

/**
 *
 * @author casseylow
 */
public class SystemAdministrationModule {
    
    private EmployeeControllerRemote employeeControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    
    private Employee currentEmployee;
    
    public SystemAdministrationModule(){
        
    }
    
    public SystemAdministrationModule(EmployeeControllerRemote employeeControllerRemote, PartnerControllerRemote partnerControllerRemote, Employee currentEmployee)
    {
        this();
        this.employeeControllerRemote = employeeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuSystemAdministration(){
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("-----------------------");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partner");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    createEmployee();
                }
                else if(response == 2)
                {
                    viewAllEmployees();
                }
                else if(response == 3)
                {
                    createPartner();
                }
                else if(response == 4)
                {
                    viewAllPartners();
                }
                else if (response == 5)
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
    
    public void createEmployee()
    {
        Scanner scanner = new Scanner(System.in);
        Employee newEmployee = new Employee();

        System.out.println("*** HoRS :: Hotel Management System :: Create New Employee ***\n");
        System.out.print("Enter Name> ");
        newEmployee.setName(scanner.nextLine().trim());
        System.out.print("Enter Email> ");
        newEmployee.setEmail(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newEmployee.setPassword(scanner.nextLine().trim());
        System.out.print("Select Employee Access Right (1: System Admin, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
        Integer employeeRoleInt = scanner.nextInt();

        OUTER:
        while (true) {
            if (employeeRoleInt >= 1 && employeeRoleInt <= 4) {
                switch (employeeRoleInt) {
                    case 1:
                        newEmployee.setAccessRight(EmployeeAccessRight.SYSTEM_ADMIN);
                        break OUTER;
                    case 2:
                        newEmployee.setAccessRight(EmployeeAccessRight.OPERATION_MANAGER);
                        break OUTER;
                    case 3:
                        newEmployee.setAccessRight(EmployeeAccessRight.SALES_MANAGER);
                        break OUTER;
                    case 4:
                        newEmployee.setAccessRight(EmployeeAccessRight.GUEST_RELATION_OFFICER);
                        break OUTER;
                    default:
                        System.out.println("Sorry, this employee access right is currently not available. Please try again!\n");
                        break;
                }
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        newEmployee = employeeControllerRemote.createEmployee(newEmployee);
        System.out.println("New employee created successfully!: " + newEmployee.getEmployeeId()+ "\n");

    }
    
    public void viewAllEmployees()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS :: Hotel Management System :: View All Employees ***\n");
        
        List<Employee> employees = employeeControllerRemote.retrieveAllEmployees();
        System.out.printf("%8s%20s%20s%15s%20s\n", "Employee Id", "Name", "Email", "Access Right", "Password");

        for(Employee employee:employees)
        {
            System.out.printf("%8s%20s%20s%15s%20s\n", employee.getEmployeeId().toString(), employee.getName(), employee.getEmail(), employee.getAccessRight().toString(), employee.getPassword());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    public void createPartner()
    {
        Scanner scanner = new Scanner(System.in);
        Partner newPartner = new Partner();

        System.out.println("*** HoRS :: Hotel Management System :: Create New Partner ***\n");
        System.out.print("Enter Name> ");
        newPartner.setName(scanner.nextLine().trim());
        System.out.print("Enter Email> ");
        newPartner.setEmail(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newPartner.setPassword(scanner.nextLine().trim());
        newPartner = partnerControllerRemote.createPartner(newPartner);
        System.out.println("New partner created successfully!: " + newPartner.getPartnerId()+ "\n");

    }
    
    public void viewAllPartners()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS :: Hotel Management System :: View All Partners ***\n");
        
        List<Partner> partners = partnerControllerRemote.retrieveAllPartners();
        System.out.printf("%8s%20s%20s%15s\n", "Partner Id", "Name", "Email", "Password");

        for(Partner partner:partners)
        {
            System.out.printf("%8s%20s%20s%15s\n", partner.getPartnerId().toString(), partner.getName(), partner.getEmail(), partner.getPassword());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }

}
