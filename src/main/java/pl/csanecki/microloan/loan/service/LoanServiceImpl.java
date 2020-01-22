package pl.csanecki.microloan.loan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.PositiveDisposition;
import pl.csanecki.microloan.loan.repository.LoanRepository;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Disposition considerLoanRequest(UserRequest mockUserRequest, LoanQuery mockLoanQuery) {
        return new PositiveDisposition();
    }
}
