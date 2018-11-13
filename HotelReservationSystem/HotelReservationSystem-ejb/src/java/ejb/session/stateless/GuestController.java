/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(GuestControllerLocal.class)
@Remote(GuestControllerRemote.class)
public class GuestController implements GuestControllerRemote, GuestControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Guest guestLogin(String email,String password) throws InvalidLoginCredentialException,GuestNotFoundException
    {
       try
       {
           Guest newGuest=retrieveGuestByEmail(email);
           if(newGuest.getPassword().equals(password))
           {
              newGuest.getOnlineReservations().size();
              return newGuest;
           }
           else 
           {
               throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
           }
       }
       catch(GuestNotFoundException ex)
       {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
       }
        
    }
    
    @Override
     public Guest retrieveGuestByEmail(String email) throws GuestNotFoundException
    {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email = :inEmail");
        query.setParameter("inUsername", email);
        
        try
        {
            return (Guest)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new GuestNotFoundException("Guest email " + email + " does not exist!");
        }
    }

    @Override
    public Long createGuest(Guest newGuest)
    {
        em.persist(newGuest);
        em.flush();
        
        return newGuest.getGuestId();
    }
    
}
