package x7030.nefzi.tjinitaw.ui.view_orders;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.Order;


public class ViewOrdersViewModel extends ViewModel {
    private MutableLiveData<List<Order>> mutableLiveDataOrderList;

    public ViewOrdersViewModel() {

        mutableLiveDataOrderList = new MutableLiveData<>();
    }


    public MutableLiveData<List<Order>> getMutableLiveDataOrderList() {
        return mutableLiveDataOrderList;
    }

    public void setMutableLiveDataOrderList(List<Order> orderList) {
        mutableLiveDataOrderList.setValue(orderList);
    }
}