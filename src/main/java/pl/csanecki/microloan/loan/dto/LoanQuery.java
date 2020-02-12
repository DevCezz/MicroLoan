package pl.csanecki.microloan.loan.dto;

import java.math.BigDecimal;

public class LoanQuery {
    public final BigDecimal amount;
    public final Integer periodInMonths;

    public LoanQuery(BigDecimal amount, int periodInMonths) {
        this.amount = amount;
        this.periodInMonths = periodInMonths;
    }
}
