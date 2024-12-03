package x7030.nefzi.tjinitaw.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import x7030.nefzi.tjinitaw.EventBus.MenuItemEvent;
import x7030.nefzi.tjinitaw.Model.RestaurantModel;
import x7030.nefzi.tjinitaw.R;

public class MyRestaurantAdapter extends RecyclerView.Adapter<MyRestaurantAdapter.MyViewHolder> {

    Context context;
    List<RestaurantModel> restaurantModelList;

    public MyRestaurantAdapter(Context context, List<RestaurantModel> restaurantModelList) {
        this.context = context;
        this.restaurantModelList = restaurantModelList;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_restaurant,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(restaurantModelList.get(position).getImageUrl())
                .placeholder(R.drawable.load)
                .into(holder.img_restaurant);
        holder.txt_restaurant_name.setText(new StringBuilder(restaurantModelList.get(position).getName()));
        holder.txt_restaurant_Address.setText(new StringBuilder(restaurantModelList.get(position).getAddress()));
        if (!restaurantModelList.get(position).getStatus())
        {
            Glide.with(context)
                    .load(R.drawable.rouge)
                    .into(holder.restaurant_status);
        }



        holder.setListener((view, pos) -> {
            Common.currentRestaurant = restaurantModelList.get(pos);
            EventBus.getDefault().postSticky(new MenuItemEvent(true,restaurantModelList.get(pos)));

        });
    }

    @Override
    public int getItemCount() {
        return restaurantModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_restaurant_name)
        TextView txt_restaurant_name;
        @BindView(R.id.txt_restaurant_Address)
        TextView txt_restaurant_Address;
        @BindView(R.id.img_restaurant)
        ImageView img_restaurant;
        @BindView(R.id.restaurant_status)
        CircleImageView restaurant_status;

        Unbinder unbinder;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
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
