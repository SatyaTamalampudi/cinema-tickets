package uk.gov.dwp.uc.pairtest.domain;

import java.util.HashMap;
import java.util.List;

/**
 * Immutable Object
 */

public class TicketTypeRequest {

	private final int noOfTickets;
	private final Type type;

	private final int ADULT_TICKET_PRICE = 20;
	private final int CHILD_TICKET_PRICE = 10;
	private final int INFANT_TICKET_PRICE = 0;

	private final boolean ADULT_SEAT_RESERVATION = true;
	private final boolean CHILD_SEAT_RESERVATION = true;
	private final boolean INFANT_SEAT_RESERVATION = false;

	private final HashMap<Type, Integer> ticketPriceMap = new HashMap<>();
	private final HashMap<Type, Boolean> seatReservationMap = new HashMap<>();

	public TicketTypeRequest(Type type, int noOfTickets) {
		this.type = type;
		this.noOfTickets = noOfTickets;

		ticketPriceMap.put(Type.ADULT, ADULT_TICKET_PRICE);
		ticketPriceMap.put(Type.CHILD, CHILD_TICKET_PRICE);
		ticketPriceMap.put(Type.INFANT, INFANT_TICKET_PRICE);

		seatReservationMap.put(Type.ADULT, ADULT_SEAT_RESERVATION);
		seatReservationMap.put(Type.CHILD, CHILD_SEAT_RESERVATION);
		seatReservationMap.put(Type.INFANT, INFANT_SEAT_RESERVATION);
	}

	public int getNoOfTickets() {
		return noOfTickets;
	}

	public Type getTicketType() {
		return type;
	}

	public enum Type {
		ADULT, CHILD, INFANT
	}

	public int getTotalPrice() {
		return noOfTickets * ticketPriceMap.get(type);
	}

	public int getTotalSeats() {
		return seatReservationMap.get(type) ? noOfTickets : 0;
	}

}
