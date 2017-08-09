package com.rossweinstein.landon.business.service;

import com.rossweinstein.landon.business.domain.RoomReservation;
import com.rossweinstein.landon.data.entity.Guest;
import com.rossweinstein.landon.data.entity.Reservation;
import com.rossweinstein.landon.data.entity.Room;
import com.rossweinstein.landon.data.repository.GuestRepository;
import com.rossweinstein.landon.data.repository.ReservationRepository;
import com.rossweinstein.landon.data.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {

    private RoomRepository roomRepository;
    private GuestRepository guestRepository;
    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomReservation> getRoomReservationsForDate(Date date) {
        Map<Long, RoomReservation> roomReservationMap = this.createRoomReservationMap(date);
        return this.getAllRoomReservations(roomReservationMap);
    }

    private Map<Long, RoomReservation> createRoomReservationMap(Date date) {
        Map<Long, RoomReservation> roomInformationMap = this.getAllRoomInformation();
        return this.addGuestInformationToMap(date, roomInformationMap);
    }

    private Map<Long, RoomReservation> getAllRoomInformation() {

        Iterable<Room> rooms = this.roomRepository.findAll();

        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();

        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomID(room.getId());
            roomReservation.setRoomName(room.getName());
            roomReservation.setRoomNumber(room.getNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });

        return roomReservationMap;
    }

    private Map<Long, RoomReservation> addGuestInformationToMap(Date date, Map<Long, RoomReservation> roomInformationMap) {

        Iterable<Reservation> reservations = this.reservationRepository.findByDate(new java.sql.Date(date.getTime()));

        if (reservations != null) {
            reservations.forEach(reservation -> {
                this.populateRoomWithGuestInfo(date, roomInformationMap, reservation);
            });
        }
        return roomInformationMap;
    }

    private void populateRoomWithGuestInfo(Date date, Map<Long, RoomReservation> roomInformationMap, Reservation reservation) {
        Guest guest = this.guestRepository.findOne(reservation.getGuestID());

        if (guest != null) {
            RoomReservation roomReservation = roomInformationMap.get(reservation.getId());
            roomReservation.setDate(date);
            roomReservation.setFirstName(guest.getFirstName());
            roomReservation.setLastName(guest.getLastName());
            roomReservation.setGuestID(guest.getId());
        }
    }

    private List<RoomReservation> getAllRoomReservations(Map<Long, RoomReservation> roomReservationMap) {

        List<RoomReservation> roomReservations = new ArrayList<>();

        for (Long roomID: roomReservationMap.keySet()) {
            roomReservations.add(roomReservationMap.get(roomID));
        }
        return roomReservations;
    }
}
