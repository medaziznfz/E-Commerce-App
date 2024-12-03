package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.PharmacieModel;


public interface IPharmacieCallbackListener {
    void OnPharmacieLoadSuccess(List<PharmacieModel> pharmacieModelList);
    void OnPharmacieLoadFailed(String message);
}
