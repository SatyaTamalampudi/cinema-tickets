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
		
		isValidAccountId(accountId);
		boolean adultTicketFound = false;
		int totalTickets = Arrays.stream(ticketTypeRequests).mapToInt(TicketTypeRequest::getNoOfTickets).sum();
		int totalAmount = Arrays.stream(ticketTypeRequests).mapToInt(TicketTypeRequest::getTotalPrice).sum();
		int totalSeats = Arrays.stream(ticketTypeRequests).mapToInt(TicketTypeRequest::getTotalSeats).sum();

		for (TicketTypeRequest request : ticketTypeRequests) {
			if (request.getTicketType() == Type.ADULT) {
				adultTicketFound = true;
			}
		}

		if (!adultTicketFound) {
			throw new InvalidPurchaseException(
					"At least one Adult ticket is required to purchase Child or Infant tickets.");
		}
		if (totalTickets > 20) {
			throw new InvalidPurchaseException("Maximum of 20 tickets can be purchased at a time.");
		}
		if (totalSeats > 20) {
			throw new InvalidPurchaseException("Maximum of 20 seats can be reserved at a time.");
		}

		ticketPaymentService.makePayment(accountId, totalAmount);
		seatReservationService.reserveSeat(accountId, totalSeats);
	}

	private void isValidAccountId(Long accountId) {
		if (accountId <= 0) {
			throw new InvalidPurchaseException("Invalid account number");
		}
	}

}
