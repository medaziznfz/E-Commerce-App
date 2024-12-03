package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.Order;


public interface ILoadOrderCallbackListener {
    void onLoadOrderSuccess(List<Order> orderList);
    void onLoadOrderFailed(String message);
}
