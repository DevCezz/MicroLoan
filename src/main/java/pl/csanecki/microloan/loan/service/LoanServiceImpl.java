package pl.csanecki.microloan.loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.Loan;
import pl.csanecki.microloan.loan.model.NegativeDisposition;
import pl.csanecki.microloan.loan.model.PositiveDisposition;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import java.math.BigDecimal;

@Service
public class LoanServiceImpl implements LoanService {

    @Value("${loan.max-amount}")
    private BigDecimal loanMaxAmount;

    @Value("${loan.risk.max-hour}")
    private int maxRiskHour;

    @Value("${loan.risk.min-hour}")
    private int minRiskHour;

    private LoanRepository loanRepository;

    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Disposition considerLoanRequest(UserRequest userRequest, LoanQuery loanQuery) {
        if(maxLoanAmountIsLessThan(loanQuery.getAmount())) {
            return new NegativeDisposition();
        }

        if(maxLoanAmountEquals(loanQuery.getAmount()) && queryWasMadeInRiskHours(userRequest)) {
            return new NegativeDisposition();
        }

        loanRepository.save(new Loan(userRequest.getIp(), userRequest.getRequestTimestamp().toLocalDate(),
                userRequest.getRequestTimestamp().toLocalDate().plusMonths(loanQuery.getPeriodInMonths()),
                loanQuery.getAmount()));
        return new PositiveDisposition();
    }

    private boolean maxLoanAmountIsLessThan(BigDecimal queryAmount) {
        return loanMaxAmount.compareTo(queryAmount) < 0;
    }

    private boolean maxLoanAmountEquals(BigDecimal queryAmount) {
        return loanMaxAmount.compareTo(queryAmount) == 0;
    }

    private boolean queryWasMadeInRiskHours(UserRequest userRequest) {
        return userRequest.getRequestTimestamp().getHour() >= minRiskHour &&
                userRequest.getRequestTimestamp().getHour() < maxRiskHour;
    }
}
