package x7030.nefzi.tjinitaw.EventBus;


import x7030.nefzi.tjinitaw.Model.PharmacieModel;

public class PharmacieSelectEvent {
    private boolean success;
    private PharmacieModel pharmacieModel;

    public PharmacieSelectEvent(PharmacieModel pharmacieModel) {
        this.success = success;
        this.pharmacieModel = pharmacieModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public PharmacieModel getPharmacieModel() {
        return pharmacieModel;
    }

    public void setPharmacieModel(PharmacieModel pharmacieModel) {
        this.pharmacieModel = pharmacieModel;
    }
}
