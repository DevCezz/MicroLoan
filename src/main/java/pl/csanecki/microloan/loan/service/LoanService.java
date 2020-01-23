package pl.csanecki.microloan.loan.service;

import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;
import pl.csanecki.microloan.loan.model.PostponementDecision;

public interface LoanService {
    PostponementDecision postponeLoan(UserRequest userRequest, Long loanId);
    Disposition considerLoanRequest(UserRequest userRequest, LoanQuery loanQuery);
}
