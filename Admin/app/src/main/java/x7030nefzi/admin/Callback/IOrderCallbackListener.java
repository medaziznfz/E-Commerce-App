package x7030nefzi.admin.Callback;


import java.util.List;

import x7030nefzi.admin.Model.OrderModel;

public interface IOrderCallbackListener {
    void onOrderLoadSuccess(List<OrderModel> orderModelList);
    void onOrderLoadFailed(String message);
}
