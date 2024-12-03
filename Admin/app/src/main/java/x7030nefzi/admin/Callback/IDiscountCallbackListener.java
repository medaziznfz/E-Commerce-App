package x7030nefzi.admin.Callback;


import java.util.List;

import x7030nefzi.admin.Model.DiscountModel;


public interface IDiscountCallbackListener {

    void onListDiscountLoadSuccess(List<DiscountModel> discountModelList);
    void onListDiscountLoadFailed(String message);


}
