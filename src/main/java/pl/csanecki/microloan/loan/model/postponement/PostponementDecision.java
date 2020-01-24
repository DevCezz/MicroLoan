package pl.csanecki.microloan.loan.model.postponement;

public abstract class PostponementDecision {
    private String message;

    public PostponementDecision(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
