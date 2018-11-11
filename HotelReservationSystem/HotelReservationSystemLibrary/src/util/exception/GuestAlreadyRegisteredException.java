/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author Zhu Yixin
 */
public class GuestAlreadyRegisteredException extends Exception{

    public GuestAlreadyRegisteredException() {
    }

    public GuestAlreadyRegisteredException(String message) {
        super(message);
    }
    
}
