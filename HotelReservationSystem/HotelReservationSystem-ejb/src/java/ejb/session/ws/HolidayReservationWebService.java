/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.entity.stateful.RoomReservationSessionBeanRemote;
import ejb.session.stateful.RoomReservationSessionBeanLocal;
import ejb.session.stateless.PartnerControllerLocal;
import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.NormalRate;
import entity.Partner;
import entity.PartnerReservation;
import entity.PeakRate;
import entity.PromotionRate;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.InvalidLoginCredentialException;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author haiyan
 */
@WebService(serviceName = "HolidayReservationWebService")
@Stateless()
public class HolidayReservationWebService {

    @EJB
    private ReservationControllerLocal reservationControllerLocal;

    @EJB
    private RoomReservationSessionBeanRemote roomReservationSessionBeanRemote;

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private RoomControllerLocal roomControllerLocal;

    @EJB
    private PartnerControllerLocal partnerControllerLocal;
    
    public Partner partnerLogin(String email, String password) throws InvalidLoginCredentialException
    {
        return partnerControllerLocal.partnerLogin(email, password);
    }
    
    public List<Room> retrieveAllAvailableRooms()
    {
        return roomControllerLocal.retrieveAllAvailableRooms();
    }
    
    public List<Room> retrieveAllRooms()
    {
        return roomControllerLocal.retrieveAllRooms();
    }
    
    public List<Room> retrieveAvailableRoomsByRoomType(Long roomTypeId)
    {
        return roomControllerLocal.retrieveAvailableRoomsByRoomType(roomTypeId);
    }
    
    public List<Room> retrieveRoomByRoomType(Long roomTypeId)
    {
        return roomControllerLocal.retrieveRoomByRoomType(roomTypeId);
    }
    
    public List<RoomType> retrieveAllRoomTypes()
    {
        return roomTypeControllerLocal.retrieveAllRoomTypes();
    }
    
    public RoomType retrieveRoomTypeById(Long roomTypeId) throws RoomTypeNotFoundException
    {
        return roomTypeControllerLocal.retrieveRoomTypeById(roomTypeId);
    }
    
    public RoomType retrieveRoomTypeByName(String name) throws RoomTypeNotFoundException
    {
        return roomTypeControllerLocal.retrieveRoomTypeByName(name);
    }
    
    public List<RoomType> retrieveAllEnabledRoomTypes()
    {
        return roomTypeControllerLocal.retrieveAllEnabledRoomTypes();
    }
    
    public List<RoomRate> retrieveAllRoomRates()
    {
        return roomRateControllerLocal.retrieveAllRoomRates();
    }
    
    public RoomRate retrieveRoomRateById(Long roomRateId, Boolean fetchRoomType) throws RoomRateNotFoundException
    {
        return roomRateControllerLocal.retrieveRoomRateById(roomRateId, fetchRoomType);
    }
    
    public List<RoomRate> retrieveRoomRateByRoomType(Long roomTypeId)
    {
        return roomRateControllerLocal.retrieveRoomRateByRoomType(roomTypeId);
    }
    
    public List<NormalRate> retrieveAllNormalRate()
    {
        return roomRateControllerLocal.retrieveAllNormalRate();
    }
    
    public List<PromotionRate> retrieveAllPromotionRate()
    {
        return roomRateControllerLocal.retrieveAllPromotionRate();
    }
    
    public List<PeakRate> retrieveAllPeakRate()
    {
        return roomRateControllerLocal.retrieveAllPeakRate();
    }
    
    public List<PeakRate> retrieveAllRoomRate()
    {
        return roomRateControllerLocal.retrieveAllRoomRate();
    }
    
    public ReservationLineItem createReservationLineItem(Date checkInDate,Date checkOutDate,String roomType)throws RoomTypeNotFoundException
    {
        return reservationControllerLocal.createReservationLineItem(checkInDate, checkOutDate, roomType);
    }
    
    public PartnerReservation retrievePartnerReservationById(Long reservationId)
    {
        return reservationControllerLocal.retrievePartnerReservationById(reservationId);
    }
    
    public List<PartnerReservation> retrieveAllPartnerReservations()
    {
        return reservationControllerLocal.retrieveAllPartnerReservations();
    }
   
    public ReservationLineItem createRoomReservationLineItem(Date checkInDate, Date checkOutDate, Long roomTypeId, Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException
    {
        return reservationControllerLocal.createRoomReservationLineItem(checkInDate, checkOutDate, roomTypeId, roomRateId);
    }
     
    public List<ReservationLineItem> retrieveAllReservationLineItems()
    {
        return reservationControllerLocal.retrieveAllReservationLineItems();
    }
    
     public String chooseRoomRate(String roomType, Date checkInDate,Date checkOutDate)
     {
         return roomReservationSessionBeanRemote.chooseRoomRate(roomType, checkInDate, checkOutDate);
     }
     
      public Long totalAmount(String roomType, Integer roomNumber,Date checkInDate,Date checkOutDate)  
      {
          return roomReservationSessionBeanRemote.totalAmount(roomType, roomNumber, checkInDate, checkOutDate);
      }
      
      public ReservationLineItem retrieveReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException
    {
        return reservationControllerLocal.retrieveReservationLineItemById(reservationLineItemId);
    }
       public PartnerReservation createPartnerReservation(Partner partner)
       {
           return reservationControllerLocal.createPartnerReservation(partner);
       }


}
