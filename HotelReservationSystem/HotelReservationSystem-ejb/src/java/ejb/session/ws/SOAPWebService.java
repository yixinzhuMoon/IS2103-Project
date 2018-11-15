/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.entity.stateful.RoomReservationSessionBeanLocal;
import ejb.session.stateless.PartnerControllerLocal;
import ejb.session.stateless.ReservationControllerLocal;
import entity.OnlineReservation;
import entity.Partner;
import entity.PartnerReservation;
import entity.ReservationLineItem;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.GuestNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author haiyan
 */
@WebService(serviceName = "SOAPWebService")
@Stateless
public class SOAPWebService {

    @EJB
    private ReservationControllerLocal reservationControllerLocal;

    @EJB
    private RoomReservationSessionBeanLocal roomReservationSessionBeanLocal;

    @EJB
    private PartnerControllerLocal partnerControllerLocal;

    
    public Partner partnerLogin(String email, String password) throws InvalidLoginCredentialException
    {
        return partnerControllerLocal.partnerLogin(email, password);
    }
    
     
    public List<ReservationLineItem> searchHotelRoom(Date checkInDate,Date checkOutDate)
    {
        return roomReservationSessionBeanLocal.searchHotelRoom(checkInDate, checkOutDate);
    }
     
     
    public PartnerReservation retrievePartnerReservationById(Long reservationId)
    {
        return reservationControllerLocal.retrievePartnerReservationById(reservationId);
    }
    
    public List<PartnerReservation> retrieveAllPartnerReservations()
    {
        return reservationControllerLocal.retrieveAllPartnerReservations();
    }
    
    public PartnerReservation reservePartnerRoom(String email,ReservationLineItem reservationRoom)
    {
        return roomReservationSessionBeanLocal.reservePartnerRoom(email,reservationRoom);
    }
    
    public Long getTotalAmount(ReservationLineItem reservationLineItem)
    {
        return roomReservationSessionBeanLocal.getTotalAmount(reservationLineItem);
    }
}
