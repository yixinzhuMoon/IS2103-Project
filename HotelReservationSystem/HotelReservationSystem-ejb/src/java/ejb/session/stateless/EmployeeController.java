/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
}
