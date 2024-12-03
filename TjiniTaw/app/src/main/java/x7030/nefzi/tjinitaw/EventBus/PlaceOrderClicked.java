package x7030.nefzi.tjinitaw.EventBus;

public class PlaceOrderClicked {
    private boolean success;

    public PlaceOrderClicked(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
