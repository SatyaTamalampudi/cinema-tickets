package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;

import thirdparty.paymentgateway.TicketPaymentService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
	/**
	 * Should only have private methods other than the one below.
	 */

	private TicketPaymentService ticketPaymentService;

	public TicketServiceImpl(TicketPaymentService ticketPaymentService) {
		this.ticketPaymentService = ticketPaymentService;
	}

	@Override
	public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
			throws InvalidPurchaseException {
		boolean adultTicketFound = false;
		int totalTickets = 0;
		int totalAmount = 0;
		for (TicketTypeRequest request : ticketTypeRequests) {
			if (request.getTicketType() == Type.ADULT) {
				adultTicketFound = true;
			}
			totalTickets += request.getNoOfTickets();
			totalAmount += request.getNoOfTickets() * getPrice(request.getTicketType());
		}
		if (!adultTicketFound) {
			throw new InvalidPurchaseException(
					"At least one Adult ticket is required to purchase Child or Infant tickets.");
		}
		if (totalTickets > 20) {
			throw new InvalidPurchaseException("Maximum of 20 tickets can be purchased at a time.");
		}
		ticketPaymentService.makePayment(accountId, totalAmount);
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
