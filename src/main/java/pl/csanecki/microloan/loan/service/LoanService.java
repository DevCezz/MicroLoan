package pl.csanecki.microloan.loan.service;

import pl.csanecki.microloan.loan.dto.LoanQuery;
import pl.csanecki.microloan.loan.dto.UserRequest;
import pl.csanecki.microloan.loan.model.Disposition;

public interface LoanService {
    Disposition considerLoanRequest(UserRequest userRequest, LoanQuery loanQuery);
}
