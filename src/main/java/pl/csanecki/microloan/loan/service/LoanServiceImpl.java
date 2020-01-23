package pl.csanecki.microloan.loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.csanecki.microloan.loan.dto.LoanPostponementQuery;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.*;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

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
    public PostponementDecision postponeLoan(LoanPostponementQuery loanPostponementQuery) {
        Optional<Loan> loanEntity = loanRepository.findById(loanPostponementQuery.getLoanId());

        if(loanEntity.isPresent()) {
            Loan foundedLoan = loanEntity.get();

            if(!foundedLoan.getClientIp().equals(loanPostponementQuery.getClientId())) {
                return new NegativePostponement("Nie można odroczyć pożyczki o id " + loanPostponementQuery.getLoanId());
            }

            if(foundedLoan.getStatus().equals(LoanStatus.POSTPONED)) {
                return new NegativePostponement("Nie można odroczyć już odroczonej pożyczki");
            }

            return executePostponement(foundedLoan);
        }

        return new NegativePostponement("Nie można odroczyć pożyczki o id " + loanPostponementQuery.getLoanId());
    }

    private PostponementDecision executePostponement(Loan loan) {
        loan.setEndingDate(loan.getEndingDate().plusDays(postponeDays));
        loan.setStatus(LoanStatus.POSTPONED);

        loanRepository.save(loan);

        return new PositivePostponement("Pożyczka została przesunięta o " + postponeDays + " dni", loan.getStatus());
    }

    @Override
    public Disposition considerLoanRequest(UserRequest userRequest, LoanQuery loanQuery) {
        if(isQualifiedForRejection(userRequest, loanQuery)) {
            return new NegativeDisposition("Nie spełniono kryteriów do wydania pożyczki", LoanStatus.REJECTED);
        }

        if(isThirdLoanRequest(userRequest)) {
            return new NegativeDisposition("Nie można wydać trzeciej pożyczki", LoanStatus.REJECTED);
        }

        Loan loan = registerLoan(userRequest, loanQuery);
        return new PositiveDisposition("Pożyczka została pomyślnie wydana", LoanStatus.GRANTED, loan.getId());
    }

    private boolean isQualifiedForRejection(UserRequest userRequest, LoanQuery loanQuery) {
        return isMaxLoanAmountLessThan(loanQuery.getAmount()) || isInRiskHourForMaxAmount(userRequest, loanQuery);
    }

    private boolean isMaxLoanAmountLessThan(BigDecimal queryAmount) {
        return loanMaxAmount.compareTo(queryAmount) < 0;
    }

    private boolean isInRiskHourForMaxAmount(UserRequest userRequest, LoanQuery loanQuery) {
        return maxLoanAmountEquals(loanQuery.getAmount()) && queryWasMadeInRiskHours(userRequest);
    }

    private boolean queryWasMadeInRiskHours(UserRequest userRequest) {
        return userRequest.getRequestTimestamp().getHour() >= minRiskHour &&
                userRequest.getRequestTimestamp().getHour() < maxRiskHour;
    }

    private boolean maxLoanAmountEquals(BigDecimal queryAmount) {
        return loanMaxAmount.compareTo(queryAmount) == 0;
    }

    private boolean isThirdLoanRequest(UserRequest userRequest) {
        return loanRepository.countLoansByClientIpAndStatus(userRequest.getIp(), LoanStatus.GRANTED) + 1 >= 3;
    }

    private Loan registerLoan(UserRequest userRequest, LoanQuery loanQuery) {
        Loan loan = new Loan();
        loan.setClientIp(userRequest.getIp());
        loan.setAmount(loanQuery.getAmount());
        loan.setStartingDate(userRequest.getRequestTimestamp().toLocalDate());
        loan.setEndingDate(calculateEndingDate(userRequest, loanQuery));
        loan.setStatus(LoanStatus.GRANTED);

        return loanRepository.save(loan);
    }

    private LocalDate calculateEndingDate(UserRequest userRequest, LoanQuery loanQuery) {
        return userRequest.getRequestTimestamp().toLocalDate().plusMonths(loanQuery.getPeriodInMonths());
    }
}
