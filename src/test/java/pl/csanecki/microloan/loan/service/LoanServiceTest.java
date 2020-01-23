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
import static pl.csanecki.microloan.loan.service.LoanFixture.grantedLoan;

@ExtendWith(SpringExtension.class)
class LoanServiceTest {
    private static int MAX_LOAN_VALUE = 10000;
    private static int PERIOD_IN_MONTHS = 36;
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
        setUpDefaultValuesForLoanService();
    }

    private void setUpDefaultValuesForLoanService() {
        ReflectionTestUtils.setField(loanService, "loanMaxAmount", BigDecimal.valueOf(MAX_LOAN_VALUE));
        ReflectionTestUtils.setField(loanService, "maxRiskHour", MAX_RISK_HOUR);
        ReflectionTestUtils.setField(loanService, "minRiskHour", MIN_RISK_HOUR);
    }

    @Test
    void shouldReturnPositiveDispositionForLoanMaxValue() {
        //given
        LoanQuery mockLoanQuery = loanQueryForMaxValue();
        UserRequest mockUserRequest = commonUserRequest();
        Loan grantedLoan = grantedLoan();

        when(loanRepository.save(any())).thenReturn(grantedLoan);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof PositiveDisposition);
        assertEquals(LoanStatus.GRANTED, disposition.getLoanStatus());
        assertEquals("Pożczyka została pomyślnie wydana", disposition.getMessage());

        PositiveDisposition positiveDisposition = (PositiveDisposition) disposition;
        assertEquals(grantedLoan.getId(), positiveDisposition.getLoanId());
    }

    @Test
    void shouldRejectLoanQueryBecauseOfTooBigAmount() {
        //given
        LoanQuery mockLoanQuery = loanQueryForExceedsValue();
        UserRequest mockUserRequest = commonUserRequest();

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
        LoanQuery mockLoanQuery = loanQueryForMaxValue();
        UserRequest mockUserRequest = userRequestAtRiskHours();

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
        LoanQuery mockLoanQuery = loanQueryForMaxValue();
        UserRequest mockUserRequest = commonUserRequest();
        Loan grantedLoan = grantedLoan();

        when(loanRepository.save(any())).thenReturn(grantedLoan);

        //when
        loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        verify(loanRepository, times(1)).save(any());
    }

    @Test
    void shouldRejectLoanQueryWhenThisIsThirdQueryFromTheSameIp() {
        //given
        LoanQuery mockLoanQuery = loanQueryForMaxValue();
        UserRequest mockUserRequest = commonUserRequest();
        Loan grantedLoan = grantedLoan();

        when(loanRepository.save(any())).thenReturn(grantedLoan);
        when(loanRepository.countLoansByClientIpAndStatus(CLIENT_IP, LoanStatus.GRANTED)).thenReturn(2);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof NegativeDisposition);
        assertEquals(LoanStatus.REJECTED, disposition.getLoanStatus());
        assertEquals("Nie można wydać trzeciej pożyczki", disposition.getMessage());
    }

    private LoanQuery loanQueryForMaxValue() {
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE);
        return new LoanQuery(queryLoanAmount, PERIOD_IN_MONTHS);
    }

    private LoanQuery loanQueryForExceedsValue() {
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE + 100);
        return new LoanQuery(queryLoanAmount, PERIOD_IN_MONTHS);
    }

    private UserRequest commonUserRequest() {
        UserRequest userRequest = mock(UserRequest.class);
        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 13, 30);

        when(userRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);
        when(userRequest.getIp()).thenReturn(CLIENT_IP);

        return userRequest;
    }

    private UserRequest userRequestAtRiskHours() {
        UserRequest userRequest = mock(UserRequest.class);
        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, MIN_RISK_HOUR + 1, 32);

        when(userRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);

        return userRequest;
    }
}