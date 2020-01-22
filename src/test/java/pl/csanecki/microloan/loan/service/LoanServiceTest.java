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
import pl.csanecki.microloan.loan.model.Loan;
import pl.csanecki.microloan.loan.model.NegativeDisposition;
import pl.csanecki.microloan.loan.model.PositiveDisposition;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static pl.csanecki.microloan.loan.service.LoanFixture.LoanBuilder;

@ExtendWith(SpringExtension.class)
class LoanServiceTest {
    private static int MAX_LOAN_VALUE = 10000;
    private static int MAX_RISK_HOUR = 6;
    private static int MIN_RISK_HOUR = 0;
    private static String CLIENT_IP = "10.0.0.90";

    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        loanService = new LoanServiceImpl(loanRepository);
        mockRequest = new MockHttpServletRequest();
        mockRequest.addHeader("X-FORWARDED-FOR", CLIENT_IP);
        ReflectionTestUtils.setField(loanService, "loanMaxAmount", BigDecimal.valueOf(MAX_LOAN_VALUE));
        ReflectionTestUtils.setField(loanService, "maxRiskHour", MAX_RISK_HOUR);
        ReflectionTestUtils.setField(loanService, "minRiskHour", MIN_RISK_HOUR);
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

    @Test
    void shouldRejectLoanQueryBecauseWasSentBetweenMidnightAndSixAmForMaxAmount() {
        //given
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE);
        LoanQuery mockLoanQuery = new LoanQuery(queryLoanAmount, 36);

        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 2, 30);
        UserRequest mockUserRequest = mock(UserRequest.class);
        when(mockUserRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof NegativeDisposition);
    }

    @Test
    void shouldSaveLoanForProperLoanQuery() {
        //given
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE);
        LoanQuery mockLoanQuery = new LoanQuery(queryLoanAmount, 36);

        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 13, 32);
        UserRequest mockUserRequest = mock(UserRequest.class);
        when(mockUserRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);

        //when
        loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        verify(loanRepository, times(1)).save(any());
    }
}