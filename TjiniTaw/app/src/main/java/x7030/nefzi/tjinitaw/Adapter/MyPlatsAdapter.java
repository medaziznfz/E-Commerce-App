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
import x7030.nefzi.tjinitaw.EventBus.PlatsItemClick;
import x7030.nefzi.tjinitaw.Model.PlatsModel;
import x7030.nefzi.tjinitaw.R;

public class MyPlatsAdapter extends LoopingPagerAdapter<PlatsModel> {

    @BindView(R.id.img_best_deal)
    ImageView img_best_deal ;
    @BindView(R.id.txt_restaurant_tag)
    TextView txt_restaurant_tag ;

    Unbinder unbinder ;

    public MyPlatsAdapter(Context context, List<PlatsModel> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }

    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition)
    {
        return LayoutInflater.from(context).inflate(R.layout.layout_restaurant_produit,container,false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        unbinder = ButterKnife.bind(this,convertView);
        Glide.with(convertView).load(itemList.get(listPosition).getImage())
                .placeholder(R.drawable.load)
                .into(img_best_deal);
        txt_restaurant_tag.setText(itemList.get(listPosition).getRestaurantTag());

        convertView.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new PlatsItemClick(itemList.get(listPosition)));
        });

    }
}
