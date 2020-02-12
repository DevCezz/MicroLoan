package pl.csanecki.microloan.loan.model.db;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Loan {
    private @Id @GeneratedValue Long id;
    private @NotNull String clientIp;
    private @NotNull LocalDate startingDate;
    private @NotNull LocalDate endingDate;
    private @NotNull BigDecimal amount;
    private @NotNull @Enumerated(EnumType.STRING) LoanStatus status;

    protected Loan() {}

    public Loan(LoanBuilder builder) {
        this.clientIp = builder.clientIp;
        this.startingDate = builder.startingDate;
        this.endingDate = builder.endingDate;
        this.amount = builder.amount;
        this.status = builder.status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public LocalDate getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDate startingDate) {
        this.startingDate = startingDate;
    }

    public LocalDate getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDate endingDate) {
        this.endingDate = endingDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}
