package x7030.nefzi.tjinitawpanel.Callback;


import java.util.List;

import x7030.nefzi.tjinitawpanel.Model.DiscountModel;

public interface IDiscountCallbackListener {

    void onListDiscountLoadSuccess(List<DiscountModel> discountModelList);
    void onListDiscountLoadFailed(String message);


}
