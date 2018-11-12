/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(EmployeeControllerLocal.class)
@Remote(EmployeeControllerRemote.class)
public class EmployeeController implements EmployeeControllerRemote, EmployeeControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public EmployeeController() {
    }
    
    @Override
    public Long createEmployee(Employee newEmployee){
        em.persist(newEmployee);
        em.flush();
        
        return newEmployee.getEmployeeId();
    }
    
    public Employee employeeLogin(String email, String password) throws InvalidLoginCredentialException{
        
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.email = :inEmail AND e.password = :inPassword");
        query.setParameter("inEmail", email);
        query.setParameter("inPassword", password);
        
        try{
            
            Employee employee = (Employee)query.getSingleResult();
            return employee;
        
        }catch(Exception ex){
            
            throw new InvalidLoginCredentialException("Username does not exist or invalid password");
        }
    }
}
