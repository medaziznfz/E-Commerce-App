package x7030.nefzi.tjinitaw.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.EventBus.OthersClick;
import x7030.nefzi.tjinitaw.Model.OthersModel;
import x7030.nefzi.tjinitaw.R;

public class MyOthersAdapter extends LoopingPagerAdapter<OthersModel> {

    @BindView(R.id.img_others)
    ImageView img_others ;

    Unbinder unbinder ;

    public MyOthersAdapter(Context context, List<OthersModel> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }

    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition)
    {
        return LayoutInflater.from(context).inflate(R.layout.layout_others_pub,container,false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        unbinder = ButterKnife.bind(this,convertView);
        Glide.with(convertView).load(itemList.get(listPosition).getImage())
                .placeholder(R.drawable.load)
                .into(img_others);

        convertView.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new OthersClick(itemList.get(listPosition)));
        });

    }
}
