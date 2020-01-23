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
import pl.csanecki.microloan.loan.model.*;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private static long LOAN_ID = 34L;

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

        Loan grantedLoan = LoanBuilder
                .newLoan()
                .withId(LOAN_ID)
                .build();
        when(loanRepository.save(any())).thenReturn(grantedLoan);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof PositiveDisposition);
        assertEquals(LoanStatus.GRANTED, disposition.getLoanStatus());
        assertEquals("Pożczyka została pomyślnie wydana", disposition.getMessage());

        PositiveDisposition positiveDisposition = (PositiveDisposition) disposition;
        assertEquals(LOAN_ID, positiveDisposition.getLoanId());
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
        assertEquals(LoanStatus.REJECTED, disposition.getLoanStatus());
        assertEquals("Nie spełniono kryteriów do wydania pożyczki", disposition.getMessage());
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
        assertEquals(LoanStatus.REJECTED, disposition.getLoanStatus());
        assertEquals("Nie spełniono kryteriów do wydania pożyczki", disposition.getMessage());
    }

    @Test
    void shouldSaveLoanForProperLoanQuery() {
        //given
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE);
        LoanQuery mockLoanQuery = new LoanQuery(queryLoanAmount, 36);

        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 13, 32);
        UserRequest mockUserRequest = mock(UserRequest.class);
        when(mockUserRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);

        Loan grantedLoan = LoanBuilder
                .newLoan()
                .withId(LOAN_ID)
                .build();
        when(loanRepository.save(any())).thenReturn(grantedLoan);

        //when
        loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void shouldRejectLoanQueryWhenThisIsThirdQueryFromTheSameIp() {
        //given
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE);
        LoanQuery mockLoanQuery = new LoanQuery(queryLoanAmount, 36);

        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 13, 32);
        UserRequest mockUserRequest = mock(UserRequest.class);
        when(mockUserRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);
        when(mockUserRequest.getIp()).thenReturn(CLIENT_IP);

        Loan grantedLoan = LoanBuilder
                .newLoan()
                .withId(LOAN_ID)
                .build();
        when(loanRepository.save(any())).thenReturn(grantedLoan);

        when(loanRepository.countLoansByClientIpAndStatus(CLIENT_IP, LoanStatus.GRANTED)).thenReturn(2);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof NegativeDisposition);
        assertEquals(LoanStatus.REJECTED, disposition.getLoanStatus());
        assertEquals("Nie można wydać trzeciej pożyczki", disposition.getMessage());
    }
}