package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
	/**
	 * Should only have private methods other than the one below.
	 */

	private final TicketPaymentService ticketPaymentService;
	private final SeatReservationService seatReservationService;

	public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
		this.ticketPaymentService = ticketPaymentService;
		this.seatReservationService = seatReservationService;
	}

	@Override
	public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
			throws InvalidPurchaseException {
		boolean adultTicketFound = false;
		int totalTickets = Arrays.stream(ticketTypeRequests).mapToInt(TicketTypeRequest::getNoOfTickets).sum();
		int numInfants = 0;
		int totalAmount = 0;

		for (TicketTypeRequest request : ticketTypeRequests) {
			if (request.getTicketType() == Type.ADULT) {
				adultTicketFound = true;
			}
			if (request.getTicketType() == Type.INFANT) {
				numInfants += 1;
			}

			totalAmount += request.getNoOfTickets() * getPrice(request.getTicketType());
		}
		int numSeats = totalTickets - numInfants;
		if (!adultTicketFound) {
			throw new InvalidPurchaseException(
					"At least one Adult ticket is required to purchase Child or Infant tickets.");
		}
		if (totalTickets > 20) {
			throw new InvalidPurchaseException("Maximum of 20 tickets can be purchased at a time.");
		}
		if (numSeats > 20) {
			throw new InvalidPurchaseException("Maximum of 20 seats can be reserved at a time.");
		}
		ticketPaymentService.makePayment(accountId, totalAmount);
		seatReservationService.reserveSeat(accountId, numSeats);
	}

	private int getPrice(Type type) {
		switch (type) {
		case INFANT:
			return 0;
		case CHILD:
			return 10;
		case ADULT:
			return 20;
		default:
			return 0;
		}
	}

}
