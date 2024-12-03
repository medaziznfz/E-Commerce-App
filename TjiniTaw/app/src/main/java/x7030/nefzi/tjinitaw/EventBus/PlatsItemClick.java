package x7030.nefzi.tjinitaw.EventBus;


import x7030.nefzi.tjinitaw.Model.OffresModel;
import x7030.nefzi.tjinitaw.Model.PlatsModel;

public class PlatsItemClick {

    private PlatsModel platsModel;

    public PlatsItemClick(PlatsModel platsModel) {
        this.platsModel = platsModel;
    }

    public PlatsModel getPlatsModel() {
        return platsModel;
    }

    public void setPlatsModel(PlatsModel platsModel) {
        this.platsModel = platsModel;
    }
}
