package x7030.nefzi.tjinitaw.ui.comments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.CommentModel;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<CommentModel>> mutableLiveDataFoodList;

    public CommentViewModel() {
        mutableLiveDataFoodList = new MutableLiveData<>();
    }
    public MutableLiveData<List<CommentModel>> getMutableLiveDataFoodList(){
        return mutableLiveDataFoodList;
    }

    public void setCommentList(List<CommentModel> commentList)
    {
        mutableLiveDataFoodList.setValue(commentList);

    }

}