package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.OthersModel;


public interface IOthersCallbackListener {
    void OnOthersLoadSuccess(List<OthersModel> popular_categoryModels);
    void OnOthersLoadFailed(String message);
}
