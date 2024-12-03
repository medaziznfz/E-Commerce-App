package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.OffresModel;


public interface IOffersCallbackListener {
    void OnOffersLoadSuccess(List<OffresModel> offresModels);
    void OnOffersLoadFailed(String message);
}
