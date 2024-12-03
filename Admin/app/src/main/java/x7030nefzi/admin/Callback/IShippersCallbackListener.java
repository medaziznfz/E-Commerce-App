package x7030nefzi.admin.Callback;

import java.util.List;

import x7030nefzi.admin.Model.ShipperModel;

public interface IShippersCallbackListener {
    void onListShippersLoadSuccess(List<ShipperModel> shippersModelList);
    void onListShippersLoadFailed(String message);
}
