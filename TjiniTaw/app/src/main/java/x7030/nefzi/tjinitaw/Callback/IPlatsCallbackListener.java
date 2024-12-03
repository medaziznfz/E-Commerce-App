package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.OffresModel;
import x7030.nefzi.tjinitaw.Model.PlatsModel;


public interface IPlatsCallbackListener {
    void OnPlatsLoadSuccess(List<PlatsModel> platsModels);
    void OnPlatsLoadFailed(String message);
}
