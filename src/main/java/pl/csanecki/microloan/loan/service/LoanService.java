package pl.csanecki.microloan.loan.service;

import pl.csanecki.microloan.loan.dto.LoanPostponementQuery;
import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.PostponementDecision;

public interface LoanService {
    PostponementDecision postponeLoan(LoanPostponementQuery loanPostponementQuery);
    Disposition considerLoanRequest(UserRequest userRequest, LoanQuery loanQuery);
}
