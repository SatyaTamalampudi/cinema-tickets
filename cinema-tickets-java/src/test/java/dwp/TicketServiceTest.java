package dwp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

	@Mock
	private TicketPaymentService ticketPaymentService;

	@Mock
	private SeatReservationService seatReservationService;

	@InjectMocks
	private TicketServiceImpl ticketService;

	@Test
	public void testAdultRequestPayment() {
		Long accountId = 123L;
		TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 1);
		ticketService.purchaseTickets(accountId, request);
		verify(ticketPaymentService).makePayment(accountId, 20);
	}

	@Test
	public void testMaxTicketLimit() {
		Long accountId = 123L;
		TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 25);
		assertThrows(InvalidPurchaseException.class, () -> {
			ticketService.purchaseTickets(accountId, request);
		});
	}

	@Test
	public void testMultipleRequestPayment() {
		Long accountId = 123L;
		TicketTypeRequest adultRequest = new TicketTypeRequest(Type.ADULT, 10);
		TicketTypeRequest childRequest = new TicketTypeRequest(Type.CHILD, 5);
		TicketTypeRequest infantRequest = new TicketTypeRequest(Type.INFANT, 8);
		assertThrows(InvalidPurchaseException.class, () -> {
			ticketService.purchaseTickets(accountId, adultRequest, childRequest, infantRequest);
		});
	}

	@Test
	public void testChildAndInfantWithoutAdult() {
		Long accountId = 123L;
		TicketTypeRequest childRequest = new TicketTypeRequest(Type.CHILD, 5);
		TicketTypeRequest infantRequest = new TicketTypeRequest(Type.INFANT, 8);
		assertThrows(InvalidPurchaseException.class, () -> {
			ticketService.purchaseTickets(accountId, childRequest, infantRequest);
		});
	}

	@Test
	public void testCalculateAmountAndMakePayment() {
		Long accountId = 123L;
		TicketTypeRequest adultRequest = new TicketTypeRequest(Type.ADULT, 2);
		TicketTypeRequest childRequest = new TicketTypeRequest(Type.CHILD, 1);
		TicketTypeRequest infantRequest = new TicketTypeRequest(Type.INFANT, 1);
		int totalAmount = 2 * 20 + 1 * 10 + 1 * 0;
		ticketService.purchaseTickets(accountId, adultRequest, childRequest, infantRequest);
		verify(ticketPaymentService).makePayment(accountId, totalAmount);
	}

	@Test
	public void testAdultRequestPaymentAndReservation() {
		Long accountId = 123L;
		TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 2);
		ticketService.purchaseTickets(accountId, request);
		verify(ticketPaymentService).makePayment(accountId, 40);
		verify(seatReservationService).reserveSeat(accountId, 2);
	}

	@Test
	public void testMixedRequestPaymentAndReservation() {
		Long accountId = 123L;
		TicketTypeRequest adultRequest = new TicketTypeRequest(Type.ADULT, 2);
		TicketTypeRequest childRequest = new TicketTypeRequest(Type.CHILD, 1);
		TicketTypeRequest infantRequest = new TicketTypeRequest(Type.INFANT, 1);
		ticketService.purchaseTickets(accountId, adultRequest, childRequest, infantRequest);
		verify(ticketPaymentService).makePayment(accountId, 50);
		verify(seatReservationService).reserveSeat(accountId, 3);
	}

	@Test
	public void testMaxSeatLimit() {
		Long accountId = 123L;
		TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 21);
		assertThrows(InvalidPurchaseException.class, () -> {
			ticketService.purchaseTickets(accountId, request);
		});
	}
	
	@Test
	public void testAccountIdValidation() {
		Long accountId = 0L;
		TicketTypeRequest request = new TicketTypeRequest(Type.ADULT, 20);
		assertThrows(InvalidPurchaseException.class, () -> {
			ticketService.purchaseTickets(accountId, request);
		});
	}

}
