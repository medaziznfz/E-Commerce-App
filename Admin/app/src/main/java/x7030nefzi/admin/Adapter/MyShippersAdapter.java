package x7030nefzi.admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030nefzi.admin.Model.ShipperModel;
import x7030nefzi.admin.R;

import static x7030nefzi.admin.Common.Common.formatPrice;

public class MyShippersAdapter extends RecyclerView.Adapter<MyShippersAdapter.MyViewHolder> {

    Context context;
    List<ShipperModel> shippersModelList;

    public MyShippersAdapter(Context context, List<ShipperModel> shippersModelList) {
        this.context = context;
        this.shippersModelList = shippersModelList;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_shipper_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.txt_fees.setText(new StringBuilder("").append(formatPrice(shippersModelList.get(position).getFees())).append(" TND"));
        holder.txt_name.setText(new StringBuilder("").append(shippersModelList.get(position).getName()));
        holder.txt_phone.setText(new StringBuilder("Phone: ").append(shippersModelList.get(position).getPhone()));
        holder.txt_password.setText(new StringBuilder("Password : ").append(shippersModelList.get(position).getPassword()));



    }

    @Override
    public int getItemCount() {
        return shippersModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.txt_phone)
        TextView txt_phone;
        @BindView(R.id.txt_password)
        TextView txt_password;
        @BindView(R.id.txt_fees)
        TextView txt_fees;

        private Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}