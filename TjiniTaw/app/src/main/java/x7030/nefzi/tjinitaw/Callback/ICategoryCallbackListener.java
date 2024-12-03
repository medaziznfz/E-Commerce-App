package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.CategoryModel;


public interface ICategoryCallbackListener {
    void OnCategoryLoadSuccess(List<CategoryModel> categoryModelList);
    void OnCategoryLoadFailed(String message);
}
