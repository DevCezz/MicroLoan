package pl.csanecki.microloan.loan.dto;

public class LoanPostponementQuery {
    private String clientId;
    private Long loanId;

    public LoanPostponementQuery(String clientId, Long loanId) {
        this.clientId = clientId;
        this.loanId = loanId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
}
