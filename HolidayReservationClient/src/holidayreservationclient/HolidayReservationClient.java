/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclient;

import javax.xml.datatype.DatatypeConfigurationException;
import ws.InvalidLoginCredentialException_Exception;
import ws.Partner;
import ws.PartnerReservation;

/**
 *
 * @author haiyan
 */
public class HolidayReservationClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InvalidLoginCredentialException_Exception, DatatypeConfigurationException {
        MainApp mainApp=new MainApp();
        mainApp.runApp();
    }

    private static Long getTotalAmount(ws.ReservationLineItem arg0) {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.getTotalAmount(arg0);
    }

    private static Partner partnerLogin(java.lang.String arg0, java.lang.String arg1) throws InvalidLoginCredentialException_Exception {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.partnerLogin(arg0, arg1);
    }

    private static PartnerReservation reservePartnerRoom(java.lang.String arg0, ws.ReservationLineItem arg1) {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.reservePartnerRoom(arg0, arg1);
    }

    private static java.util.List<ws.PartnerReservation> retrieveAllPartnerReservations() {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.retrieveAllPartnerReservations();
    }

    private static PartnerReservation retrievePartnerReservationById(java.lang.Long arg0) {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.retrievePartnerReservationById(arg0);
    }

    private static java.util.List<ws.ReservationLineItem> searchHotelRoom(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        ws.SOAPWebService_Service service = new ws.SOAPWebService_Service();
        ws.SOAPWebService port = service.getSOAPWebServicePort();
        return port.searchHotelRoom(arg0, arg1);
    }
    
    
    
}
