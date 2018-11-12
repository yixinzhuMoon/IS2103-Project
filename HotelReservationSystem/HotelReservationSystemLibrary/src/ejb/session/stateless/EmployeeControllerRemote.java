/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidLoginCredentialException;

public interface EmployeeControllerRemote {

    public Employee createEmployee(Employee newEmployee);

    public Employee employeeLogin(String email, String password) throws InvalidLoginCredentialException;

    public Employee retrieveEmployeeByEmail(String email) throws EmployeeNotFoundException;

    public Employee retrieveEmployeeByEmployeeId(Long employeeId) throws EmployeeNotFoundException;

    public List<Employee> retrieveAllEmployees();
    
}
