/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Partner;
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
import util.exception.PartnerNotFoundException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(PartnerControllerLocal.class)
@Remote(PartnerControllerRemote.class)
public class PartnerController implements PartnerControllerRemote, PartnerControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Partner createPartner(Partner newPartner)
    {
        em.persist(newPartner);
        em.flush();
        
        return newPartner;
    }
    
    @Override
    public List<Partner> retrieveAllPartners()
    {
        Query query = em.createQuery("SELECT p FROM Partner p");
        return query.getResultList();
    }
    
    @Override
    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerNotFoundException
    {
        Partner partner = em.find(Partner.class, partnerId);
        
        if(partner != null)
        {
            return partner;
        }
        else
        {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist!");
        }
    }
    
    
    
    @Override
    public Partner retrievePartnerByEmail(String email) throws PartnerNotFoundException
    {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try
        {
            return (Partner)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new PartnerNotFoundException("Partner Email " + email + " does not exist!");
        }
    }
    
    @Override
    public Partner partnerLogin(String email, String password) throws InvalidLoginCredentialException
    {
        try
        {
            Partner partner = retrievePartnerByEmail(email);
            
            if(partner.getPassword().equals(password))
            {
                partner.getPartnerReservations().size();                
                return partner;
            }
            else
            {
                throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
            }
        }
        catch(PartnerNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
        }
    }
}
