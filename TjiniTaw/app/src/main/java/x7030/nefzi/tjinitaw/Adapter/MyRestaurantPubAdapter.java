package x7030.nefzi.tjinitaw.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import x7030.nefzi.tjinitaw.Callback.IRecyclerClickListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.EventBus.RestaurantPubClick;
import x7030.nefzi.tjinitaw.Model.RestaurantModel;
import x7030.nefzi.tjinitaw.R;

public class MyRestaurantPubAdapter extends RecyclerView.Adapter<MyRestaurantPubAdapter.MyViewHolder> {

    Context context;
    List<RestaurantModel> restaurantCategoryModelList;

    public MyRestaurantPubAdapter(Context context, List<RestaurantModel> restaurantCategoryModelList) {
        this.context = context;
        this.restaurantCategoryModelList = restaurantCategoryModelList;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_restaurant_pub_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
        Glide.with(context).load(restaurantCategoryModelList.get(position).getImageUrl())
                .placeholder(R.drawable.load)
                .into(holder.category_image);
        if (restaurantCategoryModelList.get(position).getStatus())
            holder.category_image.setBorderColor(Color.GREEN);
        else
            holder.category_image.setBorderColor(Color.RED);
        holder.txt_category_name.setText(restaurantCategoryModelList.get(position).getName());
        holder.setListener((view, pos) ->
                {
                    Common.currentRestaurant = restaurantCategoryModelList.get(pos);
                    EventBus.getDefault().postSticky(new RestaurantPubClick(restaurantCategoryModelList.get(pos)));
                }






        );


    }

    @Override
    public int getItemCount() {
        return restaurantCategoryModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;

        @BindView(R.id.txt_category_name)
        TextView txt_category_name;
        @BindView(R.id.category_images)
        CircleImageView category_image;


        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener recyclerClickListener) {
            this.listener = recyclerClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view,getAdapterPosition());

        }
    }
}
