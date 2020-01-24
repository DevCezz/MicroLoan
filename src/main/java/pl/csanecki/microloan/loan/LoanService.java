package pl.csanecki.microloan.loan;

import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.disposition.Disposition;
import pl.csanecki.microloan.loan.model.postponement.PostponementDecision;

public interface LoanService {
    PostponementDecision postponeLoan(UserRequest userRequest, Long loanId);
    Disposition considerLoanRequest(UserRequest userRequest, LoanQuery loanQuery);
}
