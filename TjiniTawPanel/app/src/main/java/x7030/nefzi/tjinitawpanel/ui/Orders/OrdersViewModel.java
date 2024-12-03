package x7030.nefzi.tjinitawpanel.ui.Orders;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.List;

import x7030.nefzi.tjinitawpanel.Callback.IOrderCallbackListener;
import x7030.nefzi.tjinitawpanel.Model.OrderModel;


public class OrdersViewModel extends ViewModel {

    private MutableLiveData<List<OrderModel>> mutableLiveDataOrderList;

    public OrdersViewModel() {

        mutableLiveDataOrderList = new MutableLiveData<>();
    }


    public MutableLiveData<List<OrderModel>> getMutableLiveDataOrderList() {
        return mutableLiveDataOrderList;
    }

    public void setMutableLiveDataOrderList(List<OrderModel> orderList) {
        mutableLiveDataOrderList.setValue(orderList);
    }

}