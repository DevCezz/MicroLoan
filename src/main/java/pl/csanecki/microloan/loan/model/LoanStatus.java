package pl.csanecki.microloan.loan.model;

public enum LoanStatus {
    REJECTED("odrzucona"),
    GRANTED("przyznana");

    private String value;

    LoanStatus(String value) {
        this.value = value;
    }
}
