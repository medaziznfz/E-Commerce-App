package x7030.nefzi.tjinitaw.EventBus;


import x7030.nefzi.tjinitaw.Model.OffresModel;

public class OffersItemClick {

    private OffresModel offresModel;

    public OffersItemClick(OffresModel offresModel) {
        this.offresModel = offresModel;
    }

    public OffresModel getOffersModel() {
        return offresModel;
    }

    public void setOffersModel(OffresModel offresModel) {
        this.offresModel = offresModel;
    }
}
