package pl.csanecki.microloan.loan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.NegativeDisposition;
import pl.csanecki.microloan.loan.model.PositiveDisposition;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LoanServiceTest {
    private static int MAX_LOAN_VALUE = 10000;

    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        loanService = new LoanServiceImpl(loanRepository);
        mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("X-FORWARDED-FOR", "10.0.0.90");
        ReflectionTestUtils.setField(loanService, "loanMaxAmount", BigDecimal.valueOf(MAX_LOAN_VALUE));
    }

    @Test
    void shouldReturnPositiveDispositionForLoanMaxValue() {
        //given
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE);
        LoanQuery mockLoanQuery = new LoanQuery(queryLoanAmount, 36);

        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 13, 30);
        UserRequest mockUserRequest = mock(UserRequest.class);
        when(mockUserRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof PositiveDisposition);
    }

    @Test
    void shouldRejectLoanQueryBecauseOfTooBigAmount() {
        //given
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE + 100);
        LoanQuery mockLoanQuery = new LoanQuery(queryLoanAmount, 36);

        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 13, 30);
        UserRequest mockUserRequest = mock(UserRequest.class);
        when(mockUserRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof NegativeDisposition);
    }
}