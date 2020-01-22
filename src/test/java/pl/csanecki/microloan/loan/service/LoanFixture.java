package pl.csanecki.microloan.loan.service;

import pl.csanecki.microloan.loan.model.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;

class LoanFixture {
    static class LoanBuilder {
        private Long id;
        private String clientIp;
        private LocalDate startingDate;
        private LocalDate endingDate;
        private BigDecimal amount;

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

        Loan build() {
            Loan loan = new Loan();
            loan.setId(id);
            loan.setClientIp(clientIp);
            loan.setStartingDate(startingDate);
            loan.setEndingDate(endingDate);
            loan.setAmount(amount);

            return loan;
        }
    }
}
