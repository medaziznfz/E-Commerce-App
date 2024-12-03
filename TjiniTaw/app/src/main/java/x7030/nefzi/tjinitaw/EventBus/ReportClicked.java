package x7030.nefzi.tjinitaw.EventBus;

public class ReportClicked {
    private boolean success;

    public ReportClicked(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
