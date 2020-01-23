package pl.csanecki.microloan.loan.model;

public abstract class Disposition {
    private String message;
    private LoanStatus loanStatus;

    protected Disposition(String message, LoanStatus loanStatus) {
        this.message = message;
        this.loanStatus = loanStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }
}
