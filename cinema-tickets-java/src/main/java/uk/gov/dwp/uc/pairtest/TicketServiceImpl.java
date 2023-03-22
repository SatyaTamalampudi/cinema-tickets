package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;

import thirdparty.paymentgateway.TicketPaymentService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
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
		int totalTickets = Arrays.stream(ticketTypeRequests).mapToInt(TicketTypeRequest::getNoOfTickets).sum();
		if (totalTickets > 20) {
			throw new InvalidPurchaseException("Maximum of 20 tickets can be purchased at a time.");
		}
		ticketPaymentService.makePayment(accountId, 20);
	}

}
