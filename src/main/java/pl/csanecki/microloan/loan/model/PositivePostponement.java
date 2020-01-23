package pl.csanecki.microloan.loan.model;

public class PositivePostponement extends PostponementDecision {
    private LoanStatus loanStatus;

    public PositivePostponement(String message, LoanStatus loanStatus) {
        super(message);
        this.loanStatus = loanStatus;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }
}
