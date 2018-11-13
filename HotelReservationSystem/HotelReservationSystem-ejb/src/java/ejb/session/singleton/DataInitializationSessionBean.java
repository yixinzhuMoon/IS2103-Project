/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeControllerLocal;
import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeAccessRight;

/**
 *
 * @author casseylow
 */
@Singleton
@LocalBean
@Startup
public class DataInitializationSessionBean {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private EmployeeControllerLocal employeeController;

    @PostConstruct
    public void postConstruct()
    {
        Employee sysAdmin = em.find(Employee.class,  1l);
        
        if(sysAdmin == null)
        {
            initializeData();
        }
    }
    
    public void initializeData(){
        try{
            
            employeeController.createEmployee(new Employee("sys_admin@gmail.com", "Default System Administrator", "password", EmployeeAccessRight.SYSTEM_ADMIN));
        
        }catch(Exception ex){
            
            System.err.println("********** DataInitializationSessionBean.initializeData(): An error has occurred while loading initial test data: " + ex.getMessage());
  
        }
    }

    public void persist(Object object) {
        em.persist(object);
    }
    
}
