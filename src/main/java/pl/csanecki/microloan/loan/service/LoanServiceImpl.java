package pl.csanecki.microloan.loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.csanecki.microloan.loan.LoanService;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.db.Loan;
import pl.csanecki.microloan.loan.model.db.LoanBuilder;
import pl.csanecki.microloan.loan.model.db.LoanStatus;
import pl.csanecki.microloan.loan.model.disposition.Disposition;
import pl.csanecki.microloan.loan.model.disposition.NegativeDisposition;
import pl.csanecki.microloan.loan.model.disposition.PositiveDisposition;
import pl.csanecki.microloan.loan.model.postponement.NegativePostponement;
import pl.csanecki.microloan.loan.model.postponement.PositivePostponement;
import pl.csanecki.microloan.loan.model.postponement.PostponementDecision;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    @Value("${loan.allowed-amount}")
    public int allowedNumberOfLoans;

    @Value("${loan.max-amount}")
    private BigDecimal loanMaxAmount;

    @Value("${loan.risk.max-hour}")
    private int maxRiskHour;

    @Value("${loan.risk.min-hour}")
    private int minRiskHour;

    @Value("${loan.postpone-days}")
    private int postponeDays;

    private LoanRepository loanRepository;

    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public PostponementDecision postponeLoan(UserRequest userRequest, Long loanId) {
        Optional<Loan> loanEntity = loanRepository.findById(loanId);

        return loanEntity
                .map(l -> handlePostponeRequest(userRequest, loanEntity.get()))
                .orElse(new NegativePostponement("Nie można odroczyć pożyczki o id " + loanId));
    }

    private PostponementDecision handlePostponeRequest(UserRequest userRequest, Loan loan) {
        if(clientIsNotLoanOwner(userRequest, loan)) {
            return new NegativePostponement("Nie można odroczyć pożyczki o id " + loan.getId());
        }

        if(loanHasBeenPostponed(loan)) {
            return new NegativePostponement("Nie można odroczyć już odroczonej pożyczki");
        }

        return executePostponement(loan);
    }

    private boolean clientIsNotLoanOwner(UserRequest userRequest, Loan foundedLoan) {
        return !foundedLoan.getClientIp().equals(userRequest.ip);
    }

    private boolean loanHasBeenPostponed(Loan foundedLoan) {
        return foundedLoan.getStatus().equals(LoanStatus.POSTPONED);
    }

    private PostponementDecision executePostponement(Loan loan) {
        loan.setEndingDate(loan.getEndingDate().plusDays(postponeDays));
        loan.setStatus(LoanStatus.POSTPONED);

        loanRepository.save(loan);

        return new PositivePostponement("Pożyczka została przesunięta o " + postponeDays + " dni",
                loan.getStatus(), loan.getEndingDate());
    }

    @Override
    public Disposition considerLoanRequest(UserRequest userRequest, LoanQuery loanQuery) {
        if(isQualifiedForRejection(userRequest, loanQuery)) {
            return new NegativeDisposition("Nie spełniono kryteriów do wydania pożyczki", LoanStatus.REJECTED);
        }

        if(isNumberOfLoanRequestExceeded(userRequest)) {
            return new NegativeDisposition("Nie można wydać trzeciej pożyczki", LoanStatus.REJECTED);
        }

        Loan loan = registerLoan(userRequest, loanQuery);

        return new PositiveDisposition("Pożyczka została pomyślnie wydana", LoanStatus.GRANTED, loan.getId());
    }

    private boolean isQualifiedForRejection(UserRequest userRequest, LoanQuery loanQuery) {
        return isNotProperAmount(loanQuery.amount) || isInRiskHourForMaxAmount(userRequest, loanQuery) ||
                isNegativePeriodsInMonths(loanQuery.periodInMonths);
    }

    private boolean isNotProperAmount(BigDecimal queryAmount) {
        return isMaxLoanAmountLessThan(queryAmount) || isNegativeAmount(queryAmount);
    }

    private boolean isMaxLoanAmountLessThan(BigDecimal queryAmount) {
        return loanMaxAmount.compareTo(queryAmount) < 0;
    }

    private boolean isNegativeAmount(BigDecimal queryAmount) {
        return !queryAmount.equals(queryAmount.abs());
    }

    private boolean isInRiskHourForMaxAmount(UserRequest userRequest, LoanQuery loanQuery) {
        return maxLoanAmountEquals(loanQuery.amount) && queryWasMadeInRiskHours(userRequest);
    }

    private boolean maxLoanAmountEquals(BigDecimal queryAmount) {
        return loanMaxAmount.compareTo(queryAmount) == 0;
    }

    private boolean queryWasMadeInRiskHours(UserRequest userRequest) {
        LocalDateTime timestamp = LocalDateTime.parse(userRequest.requestTimestamp);

        return timestamp.getHour() >= minRiskHour && timestamp.getHour() < maxRiskHour;
    }

    private boolean isNegativePeriodsInMonths(int periodInMonths) {
        return periodInMonths < 0;
    }

    private boolean isNumberOfLoanRequestExceeded(UserRequest userRequest) {
        List<LoanStatus> countedStatuses = Arrays.asList(LoanStatus.GRANTED, LoanStatus.POSTPONED);
        return loanRepository.countLoansByClientIpAndStatusIsIn(userRequest.ip, countedStatuses) >= allowedNumberOfLoans;
    }

    private Loan registerLoan(UserRequest userRequest, LoanQuery loanQuery) {
        Loan loan = new LoanBuilder()
                .withClientIp(userRequest.ip)
                .withAmount(loanQuery.amount)
                .withStartingDate(LocalDate.parse(userRequest.requestTimestamp))
                .withEndingDate(calculateEndingDate(userRequest, loanQuery))
                .withStatus(LoanStatus.GRANTED)
                .build();

        return loanRepository.save(loan);
    }

    private LocalDate calculateEndingDate(UserRequest userRequest, LoanQuery loanQuery) {
        LocalDate requestDate = LocalDate.parse(userRequest.requestTimestamp);

        return requestDate.plusMonths(loanQuery.periodInMonths);
    }
}
