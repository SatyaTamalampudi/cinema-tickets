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
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

	@Mock
	private TicketPaymentService ticketPaymentService;

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

}
