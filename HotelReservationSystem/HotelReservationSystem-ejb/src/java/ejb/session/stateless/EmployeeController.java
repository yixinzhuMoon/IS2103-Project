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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;
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
    public Employee createEmployee(Employee newEmployee)
    {
        em.persist(newEmployee);
        em.flush();
        
        return newEmployee;
    }
    
    @Override
    public List<Employee> retrieveAllEmployees()
    {
        Query query = em.createQuery("SELECT e FROM Employee e");
        return query.getResultList();
    }
    
    @Override
    public Employee retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException
    {
        Employee employee = em.find(Employee.class, employeeId);
        
        if(employee != null)
        {
            return employee;
        }
        else
        {
            throw new EmployeeNotFoundException("Employee ID " + employeeId + " does not exist!");
        }
    }
    
    
    
    @Override
    public Employee retrieveEmployeeByEmail(String email) throws EmployeeNotFoundException
    {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try
        {
            return (Employee)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new EmployeeNotFoundException("Employee Email " + email + " does not exist!");
        }
    }
    
    @Override
    public Employee employeeLogin(String email, String password) throws InvalidLoginCredentialException
    {
        try
        {
            Employee employee = retrieveEmployeeByEmail(email);
            
            if(employee.getPassword().equals(password))
            {
                employee.getWalkInReservations().size();                
                return employee;
            }
            else
            {
                throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
            }
        }
        catch(EmployeeNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
        }
    }
}

