package pl.csanecki.microloan.loan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.*;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.csanecki.microloan.loan.service.LoanFixture.*;

@ExtendWith(SpringExtension.class)
class LoanServiceTest {
    private static final int POSTPONE_DAYS = 14;
    private static int ALLOWED_NUMBER_OF_LOANS = 2;
    private static int MAX_LOAN_VALUE = 10000;
    private static int PERIOD_IN_MONTHS = 36;
    private static int MAX_RISK_HOUR = 6;
    private static int MIN_RISK_HOUR = 0;
    private static String CLIENT_IP = "10.0.0.90";
    private static String DIFFERENT_CLIENT_IP = "10.0.0.91";

    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    @BeforeEach
    void setUp() {
        loanService = new LoanServiceImpl(loanRepository);
        setUpDefaultValuesForLoanService();
    }

    private void setUpDefaultValuesForLoanService() {
        ReflectionTestUtils.setField(loanService, "allowedNumberOfLoans", ALLOWED_NUMBER_OF_LOANS);
        ReflectionTestUtils.setField(loanService, "loanMaxAmount", BigDecimal.valueOf(MAX_LOAN_VALUE));
        ReflectionTestUtils.setField(loanService, "maxRiskHour", MAX_RISK_HOUR);
        ReflectionTestUtils.setField(loanService, "minRiskHour", MIN_RISK_HOUR);
        ReflectionTestUtils.setField(loanService, "postponeDays", POSTPONE_DAYS);
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
        assertEquals("Pożyczka została pomyślnie wydana", disposition.getMessage());

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
    void shouldRejectLoanQueryBecauseOfNegativeAmount() {
        //given
        LoanQuery mockLoanQuery = loanQueryForNegativeValue();
        UserRequest mockUserRequest = commonUserRequest();

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof NegativeDisposition);
        assertEquals(LoanStatus.REJECTED, disposition.getLoanStatus());
        assertEquals("Nie spełniono kryteriów do wydania pożyczki", disposition.getMessage());
    }

    @Test
    void shouldRejectLoanQueryBecauseOfNegativePeriodsInMonths() {
        //given
        LoanQuery mockLoanQuery = loanQueryForNegativePeriodsInMonths();
        UserRequest mockUserRequest = commonUserRequest();

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof NegativeDisposition);
        assertEquals(LoanStatus.REJECTED, disposition.getLoanStatus());
        assertEquals("Nie spełniono kryteriów do wydania pożyczki", disposition.getMessage());
    }

    @ParameterizedTest
    @MethodSource("dateTimestampParams")
    void shouldRejectLoanQueryBecauseWasSentBetweenMidnightAndSixAmForMaxAmount(int hour, int minutes) {
        //given
        LoanQuery mockLoanQuery = loanQueryForMaxValue();
        UserRequest mockUserRequest = userRequestDuringRiskHoursAt(hour, minutes);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof NegativeDisposition);
        assertEquals(LoanStatus.REJECTED, disposition.getLoanStatus());
        assertEquals("Nie spełniono kryteriów do wydania pożyczki", disposition.getMessage());
    }

    static Stream<Arguments> dateTimestampParams() {
        Random random = new Random();

        return Stream.of(
                arguments(MIN_RISK_HOUR, 0),
                arguments(MAX_RISK_HOUR - 1, 59),
                arguments(randomizeHour(random), random.nextInt(60)),
                arguments(randomizeHour(random), random.nextInt(60)),
                arguments(randomizeHour(random), random.nextInt(60)),
                arguments(randomizeHour(random), random.nextInt(60)),
                arguments(randomizeHour(random), random.nextInt(60)),
                arguments(randomizeHour(random), random.nextInt(60)),
                arguments(randomizeHour(random), random.nextInt(60)),
                arguments(randomizeHour(random), random.nextInt(60))
        );
    }

    static int randomizeHour(Random random) {
        return MIN_RISK_HOUR + random.nextInt(MAX_RISK_HOUR - MIN_RISK_HOUR);
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
    void shouldRejectLoanQueryWhenThisIsThirdQueryFromTheSameIpAndLoansAreGrantedOrPostponed() {
        //given
        LoanQuery mockLoanQuery = loanQueryForMaxValue();
        UserRequest mockUserRequest = commonUserRequest();

        List<LoanStatus> countedStatues = Arrays.asList(LoanStatus.GRANTED, LoanStatus.POSTPONED);
        when(loanRepository.countLoansByClientIpAndStatusIsIn(CLIENT_IP, countedStatues)).thenReturn(MAX_LOAN_VALUE);

        //when
        Disposition disposition = loanService.considerLoanRequest(mockUserRequest, mockLoanQuery);

        //then
        assertTrue(disposition instanceof NegativeDisposition);
        assertEquals(LoanStatus.REJECTED, disposition.getLoanStatus());
        assertEquals("Nie można wydać trzeciej pożyczki", disposition.getMessage());
    }

    @Test
    void shouldPostponeEndingDateOfLoanByTwoWeeks() {
        //given
        LocalDate date = LocalDate.of(2020, 3, 20);
        Loan grantedLoan = grantedLoanWithEndingDateForClientIp(date, CLIENT_IP);
        UserRequest mockUserRequest = commonUserRequest();

        when(loanRepository.findById(grantedLoan.getId())).thenReturn(Optional.of(grantedLoan));

        //when
        PostponementDecision postponementDecision = loanService.postponeLoan(mockUserRequest, grantedLoan.getId());

        //then
        assertTrue(postponementDecision instanceof PositivePostponement);
        assertEquals(LoanStatus.POSTPONED, ((PositivePostponement) postponementDecision).getLoanStatus());
        assertEquals("Pożyczka została przesunięta o " + POSTPONE_DAYS + " dni", postponementDecision.getMessage());
        assertEquals(date.plusDays(POSTPONE_DAYS), ((PositivePostponement) postponementDecision).getEndingDate());
    }

    @Test
    void shouldNotPostponeLoanWhichNotExists() {
        //given
        LocalDate date = LocalDate.of(2020, 3, 20);
        Loan grantedLoan = grantedLoanWithEndingDateForClientIp(date, CLIENT_IP);
        UserRequest mockUserRequest = commonUserRequest();

        when(loanRepository.findById(grantedLoan.getId())).thenReturn(Optional.empty());

        //when
        PostponementDecision postponementDecision = loanService.postponeLoan(mockUserRequest, grantedLoan.getId());

        //then
        assertTrue(postponementDecision instanceof NegativePostponement);
        assertEquals("Nie można odroczyć pożyczki o id " + grantedLoan.getId(), postponementDecision.getMessage());
    }

    @Test
    void shouldNotPostponeLoanWhichOnceHasBeenPostponed() {
        //given
        Loan postponedLoan = postponedLoanForClientIp(CLIENT_IP);
        UserRequest mockUserRequest = commonUserRequest();

        when(loanRepository.findById(postponedLoan.getId())).thenReturn(Optional.of(postponedLoan));

        //when
        PostponementDecision postponementDecision = loanService.postponeLoan(mockUserRequest, postponedLoan.getId());

        //then
        assertTrue(postponementDecision instanceof NegativePostponement);
        assertEquals("Nie można odroczyć już odroczonej pożyczki", postponementDecision.getMessage());
    }

    @Test
    void shouldNotPostponeLoanWhenPostponementIsNotMadeByOwner() {
        //given
        Loan grantedLoan = grantedLoanForClientIp(CLIENT_IP);
        UserRequest mockUserRequest = commonUserRequestWithDifferentIp();

        when(loanRepository.findById(grantedLoan.getId())).thenReturn(Optional.of(grantedLoan));

        //when
        PostponementDecision postponementDecision = loanService.postponeLoan(mockUserRequest, grantedLoan.getId());

        //then
        assertTrue(postponementDecision instanceof NegativePostponement);
        assertEquals("Nie można odroczyć pożyczki o id " + grantedLoan.getId(), postponementDecision.getMessage());
    }

    private LoanQuery loanQueryForMaxValue() {
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE);
        return new LoanQuery(queryLoanAmount, PERIOD_IN_MONTHS);
    }

    private LoanQuery loanQueryForExceedsValue() {
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE + 100);
        return new LoanQuery(queryLoanAmount, PERIOD_IN_MONTHS);
    }

    private LoanQuery loanQueryForNegativeValue() {
        BigDecimal queryLoanAmount = BigDecimal.valueOf(-100);
        return new LoanQuery(queryLoanAmount, PERIOD_IN_MONTHS);
    }

    private LoanQuery loanQueryForNegativePeriodsInMonths() {
        BigDecimal queryLoanAmount = BigDecimal.valueOf(MAX_LOAN_VALUE);
        return new LoanQuery(queryLoanAmount, -12);
    }

    private UserRequest commonUserRequest() {
        UserRequest userRequest = mock(UserRequest.class);
        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 13, 30);

        when(userRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);
        when(userRequest.getIp()).thenReturn(CLIENT_IP);

        return userRequest;
    }

    private UserRequest commonUserRequestWithDifferentIp() {
        UserRequest userRequest = mock(UserRequest.class);
        LocalDateTime expectedTimestamp = LocalDateTime.of(2020, 1, 20, 13, 30);

        when(userRequest.getRequestTimestamp()).thenReturn(expectedTimestamp);
        when(userRequest.getIp()).thenReturn(DIFFERENT_CLIENT_IP);

        return userRequest;
    }

    private UserRequest userRequestDuringRiskHoursAt(int hour, int minutes) {
        UserRequest userRequest = mock(UserRequest.class);
        LocalDateTime timestamp = LocalDateTime.of(2020, 1, 20, hour, minutes);

        when(userRequest.getRequestTimestamp()).thenReturn(timestamp);

        return userRequest;
    }
}