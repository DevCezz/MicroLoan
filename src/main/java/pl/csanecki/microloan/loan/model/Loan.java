package pl.csanecki.microloan.loan.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Loan {
    private @Id @GeneratedValue Long id;
    private String clientIp;
    private LocalDate startingDate;
    private LocalDate endingDate;
    private BigDecimal amount;

    public Loan() {}

    public Loan(String clientIp, LocalDate startingDate, LocalDate endingDate, BigDecimal amount) {
        this.clientIp = clientIp;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.amount = amount;
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
}
