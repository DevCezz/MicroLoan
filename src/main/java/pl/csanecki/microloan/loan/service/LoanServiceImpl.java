package pl.csanecki.microloan.loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.NegativeDisposition;
import pl.csanecki.microloan.loan.model.PositiveDisposition;
import pl.csanecki.microloan.loan.repository.LoanRepository;

import java.math.BigDecimal;

@Service
public class LoanServiceImpl implements LoanService {

    @Value("${loan.max-amount}")
    private BigDecimal loanMaxAmount;

    private LoanRepository loanRepository;

    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Disposition considerLoanRequest(UserRequest userRequest, LoanQuery loanQuery) {
        if(maxAmountOfValueIsLessThan(loanQuery.getAmount())) {
            return new NegativeDisposition();
        }

        return new PositiveDisposition();
    }

    private boolean maxAmountOfValueIsLessThan(BigDecimal queryAmount) {
        return loanMaxAmount.compareTo(queryAmount) < 0;
    }
}
