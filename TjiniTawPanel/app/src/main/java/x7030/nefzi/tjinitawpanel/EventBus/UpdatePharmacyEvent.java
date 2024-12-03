package x7030.nefzi.tjinitawpanel.EventBus;


import x7030.nefzi.tjinitawpanel.Model.PharmacieModel;

public class UpdatePharmacyEvent {
    private PharmacieModel pharmacieModel;
    private boolean active;

    public UpdatePharmacyEvent(PharmacieModel pharmacieModel, boolean active) {
        this.pharmacieModel = pharmacieModel;
        this.active = active;
    }


    public PharmacieModel getPharmacieModel() {
        return pharmacieModel;
    }

    public void setPharmacieModel(PharmacieModel pharmacieModel) {
        this.pharmacieModel = pharmacieModel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
