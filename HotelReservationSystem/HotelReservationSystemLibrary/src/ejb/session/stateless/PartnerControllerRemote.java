/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

public interface PartnerControllerRemote {

    public Partner createPartner(Partner newPartner);

    public List<Partner> retrieveAllPartners();

    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerNotFoundException;

    public Partner retrievePartnerByEmail(String email) throws PartnerNotFoundException;

    public Partner partnerLogin(String email, String password) throws InvalidLoginCredentialException;
    
}
