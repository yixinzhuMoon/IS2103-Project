/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

/**
 *
 * @author haiyan
 */
public class NonUniqueResultException extends Exception{

    public NonUniqueResultException() {
    }

    public NonUniqueResultException(String message) {
        super(message);
    }
    
}
