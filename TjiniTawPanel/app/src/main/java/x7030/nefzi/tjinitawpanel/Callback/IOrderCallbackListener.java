package x7030.nefzi.tjinitawpanel.Callback;


import java.util.List;

import x7030.nefzi.tjinitawpanel.Model.OrderModel;

public interface IOrderCallbackListener {
    void onOrderLoadSuccess(List<OrderModel> orderModelList);
    void onOrderLoadFailed(String message);
}
