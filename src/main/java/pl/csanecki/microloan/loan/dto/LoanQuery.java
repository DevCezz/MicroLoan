package pl.csanecki.microloan.loan.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class LoanQuery {
    private BigDecimal amount;
    private int periodInMonths;

    public LoanQuery() {}

    public LoanQuery(BigDecimal amount, int periodInMonths) {
        this.amount = amount;
        this.periodInMonths = periodInMonths;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getPeriodInMonths() {
        return periodInMonths;
    }

    public void setPeriodInMonths(int periodInMonths) {
        this.periodInMonths = periodInMonths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanQuery loanQuery = (LoanQuery) o;
        return periodInMonths == loanQuery.periodInMonths &&
                Objects.equals(amount, loanQuery.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, periodInMonths);
    }

    @Override
    public String toString() {
        return "LoanQuery{" +
                "amount=" + amount +
                ", periodInMonths=" + periodInMonths +
                '}';
    }
}
