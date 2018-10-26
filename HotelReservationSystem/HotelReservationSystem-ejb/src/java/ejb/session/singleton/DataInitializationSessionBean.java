/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author casseylow
 */
@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    public DataInitializationSessionBean() {
    }

}
