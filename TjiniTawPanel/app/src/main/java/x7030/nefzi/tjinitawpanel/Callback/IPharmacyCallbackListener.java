package x7030.nefzi.tjinitawpanel.Callback;

import java.util.List;

import x7030.nefzi.tjinitawpanel.Model.PharmacieModel;

public interface IPharmacyCallbackListener {

    void onPharmacyLoadSuccess(List<PharmacieModel> pharmacieModelList);
    void onPharmacyLoadFailed(String message);
}
