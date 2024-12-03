package x7030.nefzi.tjinitaw.Callback;

import java.util.List;

import x7030.nefzi.tjinitaw.Model.CommentModel;


public interface ICommentCallbackListener {
    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String message);
}
