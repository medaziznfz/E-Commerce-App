package x7030.nefzi.tjinitaw.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import x7030.nefzi.tjinitaw.Callback.IRecyclerClickListener;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Database.CartDataSource;
import x7030.nefzi.tjinitaw.Database.CartDatabase;
import x7030.nefzi.tjinitaw.Database.CartItem;
import x7030.nefzi.tjinitaw.Database.LocalCartDataSource;
import x7030.nefzi.tjinitaw.EventBus.CounterCartEvent;
import x7030.nefzi.tjinitaw.EventBus.FoodItemClick;
import x7030.nefzi.tjinitaw.EventBus.ProduitItemClick;
import x7030.nefzi.tjinitaw.Model.FoodModel;
import x7030.nefzi.tjinitaw.R;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder> {

    private Context context;
    private List<FoodModel> foodModelList;
    private CompositeDisposable compositeDisposable ;
    private CartDataSource cartDataSource;

    public MyFoodListAdapter(Context context, List<FoodModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());

    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodModelList.get(position).getImage())
                .placeholder(R.drawable.load)
                .into(holder.img_food_img);
        holder.txt_food_price.setText(new StringBuilder("").append(Common.formatPrice(foodModelList.get(position).getPrice())));
        holder.txt_food_name.setText(new StringBuilder("").append(foodModelList.get(position).getName()));

        holder.setListener((view, pos) -> {
            Common.selectedFood = foodModelList.get(pos);
            Common.selectedFood.setKey(String.valueOf(pos));
            if (Common.selectedFood.getAddon() != null && Common.selectedFood.getSize() != null)
                EventBus.getDefault().postSticky(new FoodItemClick(true,foodModelList.get(pos)));
            else
                EventBus.getDefault().postSticky(new ProduitItemClick(true,foodModelList.get(pos)));

        });


    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.img_food_image)
        ImageView img_food_img;

        IRecyclerClickListener listener ;

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
