package pl.csanecki.microloan.loan.model.disposition;

import pl.csanecki.microloan.loan.model.db.LoanStatus;

public class NegativeDisposition extends Disposition {
    public NegativeDisposition(String message, LoanStatus loanStatus) {
        super(message, loanStatus);
    }
}
