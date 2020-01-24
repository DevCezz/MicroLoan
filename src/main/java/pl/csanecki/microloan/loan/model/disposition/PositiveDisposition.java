package pl.csanecki.microloan.loan.model.disposition;

import pl.csanecki.microloan.loan.model.db.LoanStatus;

public class PositiveDisposition extends Disposition {
    private Long loanId;

    public PositiveDisposition(String message, LoanStatus loanStatus, Long loanId) {
        super(message, loanStatus);
        this.loanId = loanId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
}
