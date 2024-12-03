package x7030.nefzi.tjinitawpanel.Common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import x7030.nefzi.tjinitawpanel.EventBus.LoadOrderEvent;
import x7030.nefzi.tjinitawpanel.R;

public class BottomSheetOrderFragment extends BottomSheetDialogFragment {

    @OnClick(R.id.placed_filter)
    public void onPlacedFilterClick(){
        EventBus.getDefault().postSticky(new LoadOrderEvent(0));
        dismiss();

    }

    @OnClick(R.id.shipped_filter)
    public void onSHippedFilterClick(){
        EventBus.getDefault().postSticky(new LoadOrderEvent(2));
        dismiss();

    }

    @OnClick(R.id.cancelled_filter)
    public void onCancelledFilterClick(){
        EventBus.getDefault().postSticky(new LoadOrderEvent(-1));
        dismiss();

    }

    private Unbinder unbinder ;

    private static BottomSheetDialogFragment instance;

    public static BottomSheetDialogFragment getInstance() {
        return instance == null ? new BottomSheetOrderFragment() : instance;
    }

    public BottomSheetOrderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_order_filter,container,false);
        unbinder = ButterKnife.bind(this,itemView);
        return itemView;
    }
}
