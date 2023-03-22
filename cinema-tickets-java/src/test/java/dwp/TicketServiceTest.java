package dwp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
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
		TicketTypeRequest[] requests = new TicketTypeRequest[25];
		for (int i = 0; i < 25; i++) {
			requests[i] = new TicketTypeRequest(Type.ADULT, 1);
		}
		assertThrows(InvalidPurchaseException.class, () -> {
			ticketService.purchaseTickets(accountId, requests);
		});
	}

}
