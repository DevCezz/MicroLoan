package pl.csanecki.microloan.loan.service;

import pl.csanecki.microloan.loan.model.Loan;
import pl.csanecki.microloan.loan.model.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

class LoanFixture {
    private static long GRANTED_LOAN_ID = 34L;

    static Loan grantedLoan() {
        return LoanBuilder
                .newLoan()
                .withId(GRANTED_LOAN_ID)
                .withStatus(LoanStatus.GRANTED)
                .build();
    }

    static Loan grantedLoanForClientIp(String clientIp) {
        return LoanBuilder
                .newLoan()
                .withId(GRANTED_LOAN_ID)
                .withClientIp(clientIp)
                .withStatus(LoanStatus.GRANTED)
                .build();
    }

    static Loan grantedLoanWithEndingDateForClientIp(LocalDate endingDate, String clientIp) {
        return LoanBuilder
                .newLoan()
                .withId(GRANTED_LOAN_ID)
                .withClientIp(clientIp)
                .withStatus(LoanStatus.GRANTED)
                .withEndingDate(endingDate)
                .build();
    }

    static Loan postponedLoanForClientIp(String clientIp) {
        return LoanBuilder
                .newLoan()
                .withId(GRANTED_LOAN_ID)
                .withClientIp(clientIp)
                .withStatus(LoanStatus.POSTPONED)
                .build();
    }

    static class LoanBuilder {
        private Long id;
        private String clientIp;
        private LocalDate startingDate;
        private LocalDate endingDate;
        private BigDecimal amount;
        private LoanStatus status;

        static LoanBuilder newLoan() {
            return new LoanBuilder();
        }

        LoanBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        LoanBuilder withClientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        LoanBuilder withStartingDate(LocalDate startingDate) {
            this.startingDate = startingDate;
            return this;
        }

        LoanBuilder withEndingDate(LocalDate endingDate) {
            this.endingDate = endingDate;
            return this;
        }

        LoanBuilder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        LoanBuilder withStatus(LoanStatus status) {
            this.status = status;
            return this;
        }

        Loan build() {
            Loan loan = new Loan();
            loan.setId(id);
            loan.setClientIp(clientIp);
            loan.setStartingDate(startingDate);
            loan.setEndingDate(endingDate);
            loan.setAmount(amount);
            loan.setStatus(status);

            return loan;
        }
    }
}
