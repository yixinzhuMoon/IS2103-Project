/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystemclient;

import java.text.ParseException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceRef;
import ws.client.HolidayReservationWebService_Service;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.Partner;
import ws.client.PartnerReservation;
import ws.client.ReservationLineItem;
import ws.client.ReservationLineItemNotFoundException_Exception;
import ws.client.RoomTypeNotFoundException_Exception;

/**
 *
 * @author haiyan
 */
public class Main {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8080/HolidayReservationWebService/HolidayReservationWebService.wsdl")
    private static HolidayReservationWebService_Service service;


    public static void main(String[] args) throws InvalidLoginCredentialException_Exception, ParseException, RoomTypeNotFoundException_Exception, DatatypeConfigurationException, ReservationLineItemNotFoundException_Exception  {
        MainApp mainApp=new MainApp();
        mainApp.runApp();
    }

    private static Partner partnerLogin(java.lang.String arg0, java.lang.String arg1) throws InvalidLoginCredentialException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.partnerLogin(arg0, arg1);
    }

    private static java.util.List<ws.client.RoomType> retrieveAllEnabledRoomTypes() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.retrieveAllEnabledRoomTypes();
    }

    private static Long totalAmount(java.lang.String arg0, java.lang.Integer arg1, javax.xml.datatype.XMLGregorianCalendar arg2, javax.xml.datatype.XMLGregorianCalendar arg3) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.totalAmount(arg0, arg1, arg2, arg3);
    }

    private static ReservationLineItem createReservationLineItem(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1, java.lang.String arg2) throws RoomTypeNotFoundException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.createReservationLineItem(arg0, arg1, arg2);
    }

    private static PartnerReservation retrievePartnerReservationById(java.lang.Long arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.retrievePartnerReservationById(arg0);
    }

    private static java.util.List<ws.client.ReservationLineItem> retrieveAllReservationLineItems() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.retrieveAllReservationLineItems();
    }

    private static PartnerReservation createPartnerReservation(ws.client.Partner arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.createPartnerReservation(arg0);
    }

    private static ReservationLineItem retrieveReservationLineItemById(java.lang.Long arg0) throws ReservationLineItemNotFoundException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.client.HolidayReservationWebService port = service.getHolidayReservationWebServicePort();
        return port.retrieveReservationLineItemById(arg0);
    }

    
    
}
