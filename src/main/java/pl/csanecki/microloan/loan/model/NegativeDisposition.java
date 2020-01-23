package pl.csanecki.microloan.loan.model;

public class NegativeDisposition extends Disposition {
    public NegativeDisposition(String message, LoanStatus loanStatus) {
        super(message, loanStatus);
    }
}
