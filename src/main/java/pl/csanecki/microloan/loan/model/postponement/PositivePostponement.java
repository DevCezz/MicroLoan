package pl.csanecki.microloan.loan.model.postponement;

import pl.csanecki.microloan.loan.model.db.LoanStatus;

import java.time.LocalDate;

public class PositivePostponement extends PostponementDecision {
    private LoanStatus loanStatus;
    private LocalDate endingDate;

    public PositivePostponement(String message, LoanStatus loanStatus, LocalDate endingDate) {
        super(message);
        this.loanStatus = loanStatus;
        this.endingDate = endingDate;
    }

    public LoanStatus getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(LoanStatus loanStatus) {
        this.loanStatus = loanStatus;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }
}
