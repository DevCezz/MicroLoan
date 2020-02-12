package pl.csanecki.microloan.loan.model.db;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanBuilder {
    private String clientIp;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private BigDecimal amount;
    private LoanStatus status;

    public LoanBuilder() {}

    public LoanBuilder withClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public LoanBuilder withStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
        return this;
    }

    public LoanBuilder withEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
        return this;
    }

    public LoanBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public LoanBuilder withStatus(LoanStatus status) {
        this.status = status;
        return this;
    }

    public Loan build() {
        Loan loan = new Loan();
        loan.setClientIp(clientIp);
        loan.setAmount(amount);
        loan.setStartingDate(startingDate);
        loan.setEndingDate(endingDate);
        loan.setStatus(status);

        return loan;
    }
}
