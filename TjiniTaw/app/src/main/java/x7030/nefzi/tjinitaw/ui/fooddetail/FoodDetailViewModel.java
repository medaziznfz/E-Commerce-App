package x7030.nefzi.tjinitaw.ui.fooddetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Model.CommentModel;
import x7030.nefzi.tjinitaw.Model.FoodModel;

public class FoodDetailViewModel extends ViewModel {

    private MutableLiveData<FoodModel> mutableLiveDataFood;
    private MutableLiveData<CommentModel> mutableLiveDataComment;

    public void setCommentModel(CommentModel commentModel)
    {
        if (mutableLiveDataComment != null)
            mutableLiveDataComment.setValue(commentModel);


    }

    public MutableLiveData<CommentModel> getMutableLiveDataComment() {
        return mutableLiveDataComment;
    }

    public FoodDetailViewModel() {
        mutableLiveDataComment = new MutableLiveData<>();

    }

    public MutableLiveData<FoodModel> getMutableLiveDataFood() {

        if (mutableLiveDataFood == null)
            mutableLiveDataFood = new MutableLiveData<>();
        mutableLiveDataFood.setValue(Common.selectedFood);

        return mutableLiveDataFood;
    }

    public void setFoodModel(FoodModel foodModel) {
        if (mutableLiveDataFood != null)
            mutableLiveDataFood.setValue(foodModel);
    }
}
