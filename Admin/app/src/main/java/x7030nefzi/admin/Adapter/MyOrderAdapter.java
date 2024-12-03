package x7030nefzi.admin.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030nefzi.admin.Callback.IRecyclerClickListener;
import x7030nefzi.admin.Common.Common;
import x7030nefzi.admin.Model.CartItem;
import x7030nefzi.admin.Model.OrderModel;
import x7030nefzi.admin.R;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

    Context context;
    List<OrderModel> orderModelList;
    SimpleDateFormat simpleDateFormat;

    public MyOrderAdapter(Context context, List<OrderModel> orderModelList){
        this.context = context;
        this.orderModelList = orderModelList;
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    }



    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_order_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        if (orderModelList.get(position).getCartItemList() == null)
        {


            holder.order_nrml.setVisibility(View.GONE);
            holder.service_task.setVisibility(View.VISIBLE);

            if (!orderModelList.get(position).getShipperName().equals("noOne")) {
                holder.txt_service_shipper.setVisibility(View.VISIBLE);
                Common.setSpanStringColor("3and: ", orderModelList.get(position).getShipperName(),
                        holder.txt_service_shipper, Color.parseColor("#0E38CF"));
            }
            else
                holder.txt_service_shipper.setVisibility(View.GONE);

            switch (orderModelList.get(position).getOrderStatus())
            {
                case 0:
                    holder.service_task.setBackgroundResource(R.color.task_0);
                    break;
                case 1:
                    holder.service_task.setBackgroundResource(R.color.tasks_1);
                    break;
                case 2 :
                    holder.service_task.setBackgroundResource(R.color.tasks_2);
                    break;
                case -1 :
                    holder.service_task.setBackgroundResource(R.color.tasks_11);
                    break;
                default:
                    holder.service_task.setBackgroundResource(R.color.task_0);
                    break;


            }

            holder.txt_service_source.setText(orderModelList.get(position).getRestaurantName());

            holder.txt_service_number.setText(orderModelList.get(position).getOrderNumber());


            Common.setSpanStringColor("Date: ",simpleDateFormat.format(orderModelList.get(position).getCreateDate()),
                    holder.txt_time_service, Color.parseColor("#000000"));


            Common.setSpanStringColor("status commande: ",Common.convertStatusToString(orderModelList.get(position).getOrderStatus()),
                    holder.txt_service_status, Color.parseColor("#E91E63"));


            Common.setSpanStringColor("Client: ",orderModelList.get(position).getUserName(),
                    holder.txt_name_service, Color.parseColor("#0E38CF"));

            Common.setSpanStringColor("Addristou: ",orderModelList.get(position).getShippingAddress(),
                    holder.txt_address_service, Color.parseColor("#FF3700B3"));

            if (orderModelList.get(position).getFees() != 0.0)
                holder.txt_service_fees.setText(new StringBuilder("frais de livraison : "+Common.formatPrice(orderModelList.get(position).getFees())+" tnd"));
            else
                holder.txt_service_fees.setText(new StringBuilder("frais de livraison : en train de calculer"));


            holder.txt_service_task.setText(orderModelList.get(position).getService());
            
            
            holder.setRecyclerClickListener(new IRecyclerClickListener() {
                @Override
                public void onItemClickListener(View view, int pos) {
                    Toast.makeText(context, "ma3andouch detail", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else 
        {



            holder.order_nrml.setVisibility(View.VISIBLE);
            holder.service_task.setVisibility(View.GONE);

            if (!orderModelList.get(position).getShipperName().equals("noOne")) {
                holder.txt_order_shipper.setVisibility(View.VISIBLE);
                Common.setSpanStringColor("3and: ", orderModelList.get(position).getShipperName(),
                        holder.txt_order_shipper, Color.parseColor("#0E38CF"));

            }
            else
                holder.txt_order_shipper.setVisibility(View.GONE);



            switch (orderModelList.get(position).getOrderStatus())
            {
                case 0:
                    holder.order_nrml.setBackgroundResource(R.color.task_0);
                    break;
                case 1:
                    holder.order_nrml.setBackgroundResource(R.color.tasks_1);
                    break;
                case 2 :
                    holder.order_nrml.setBackgroundResource(R.color.tasks_2);
                    break;
                case -1 :
                    holder.order_nrml.setBackgroundResource(R.color.tasks_11);
                    break;
                default:
                    holder.order_nrml.setBackgroundResource(R.color.task_0);
                    break;


            }




            Glide.with(context).load(orderModelList.get(position).getCartItemList().get(0).getFoodIamge()).into(holder.img_food_image);

            holder.txt_order_source.setText(orderModelList.get(position).getRestaurantName());

            holder.txt_order_number.setText(orderModelList.get(position).getOrderNumber());


            Common.setSpanStringColor("Date: ",simpleDateFormat.format(orderModelList.get(position).getCreateDate()),
                    holder.txt_time, Color.parseColor("#000000"));


            Common.setSpanStringColor("status commande: ",Common.convertStatusToString(orderModelList.get(position).getOrderStatus()),
                    holder.txt_order_status, Color.parseColor("#E91E63"));

            Common.setSpanStringColor("Commentaire: ",orderModelList.get(position).getComment(),
                    holder.txt_comment, Color.parseColor("#ff4444"));


            Common.setSpanStringColor("Client: ",orderModelList.get(position).getUserName(),
                    holder.txt_name, Color.parseColor("#0E38CF"));

            if (orderModelList.get(position).getDiscount() == 0) {
                String payment=(new StringBuilder("").append(Common.formatPrice(orderModelList.get(position).getFinalPayment())).append(" TND")).toString();

                Common.setSpanStringColor("Total: ", payment,
                        holder.txt_price, Color.parseColor("#dc143c"));

            }
            else {
                String payment=(new StringBuilder("").append(Common.formatPrice(orderModelList.get(position).getFinalPayment())).append(" TND").append(" (-")
                        .append(orderModelList.get(position).getDiscount()).append("%)")).toString();
                Common.setSpanStringColor("Total: ",payment,
                        holder.txt_price, Color.parseColor("#C60808"));



            }
            Common.setSpanStringColor("Addristou: ",orderModelList.get(position).getShippingAddress(),
                    holder.txt_address, Color.parseColor("#FF3700B3"));

            if (orderModelList.get(position).getFees() != 0.0)
                holder.txt_order_fees.setText(new StringBuilder("frais de livraison : "+Common.formatPrice(orderModelList.get(position).getFees())+" tnd"));
            else
                holder.txt_order_fees.setText(new StringBuilder("frais de livraison : en train de calculer"));








            holder.setRecyclerClickListener((view, pos) ->

                    showDialog(orderModelList.get(pos).getCartItemList()));



        }
        
        

    }

    private void showDialog(List<CartItem> cartItemList) {
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_order_detail,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout_dialog);

        Button btn_ok = (Button)layout_dialog.findViewById(R.id.btn_ok);
        RecyclerView recycler_order_detail = (RecyclerView)layout_dialog.findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recycler_order_detail.setLayoutManager(layoutManager);
        recycler_order_detail.addItemDecoration(new DividerItemDecoration(context,layoutManager.getOrientation()));

        MyOrderDetailAdapter myOrderDetailAdapter = new MyOrderDetailAdapter(context,cartItemList);
        recycler_order_detail.setAdapter(myOrderDetailAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        btn_ok.setOnClickListener(view -> dialog.dismiss());
    }
    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public OrderModel getItemPosition(int pos) {
        return orderModelList.get(pos);
    }

    public void removeItem(int pos) {
        orderModelList.remove(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @BindView(R.id.txt_name)
        TextView txt_name;
        @BindView(R.id.txt_time)
        TextView txt_time;
        @BindView(R.id.txt_order_status)
        TextView txt_order_status;
        @BindView(R.id.txt_order_number)
        TextView txt_order_number;
        @BindView(R.id.txt_price)
        TextView txt_price;
        @BindView(R.id.txt_comment)
        TextView txt_comment;
        @BindView(R.id.txt_address)
        TextView txt_address;
        @BindView(R.id.txt_order_source)
        TextView txt_order_source;
        @BindView(R.id.order_nrml)
        LinearLayout order_nrml;
        @BindView(R.id.service_task)
        LinearLayout service_task;
        @BindView(R.id.txt_service_source)
        TextView txt_service_source;
        @BindView(R.id.txt_service_number)
        TextView txt_service_number;
        @BindView(R.id.txt_time_service)
        TextView txt_time_service;
        @BindView(R.id.txt_name_service)
        TextView txt_name_service;
        @BindView(R.id.txt_service_status)
        TextView txt_service_status;
        @BindView(R.id.txt_address_service)
        TextView txt_address_service;
        @BindView(R.id.txt_service_task)
        TextView txt_service_task;
        @BindView(R.id.txt_service_fees)
        TextView txt_service_fees;
        @BindView(R.id.txt_order_fees)
        TextView txt_order_fees;
        @BindView(R.id.txt_order_shipper)
        TextView txt_order_shipper;
        @BindView(R.id.txt_service_shipper)
        TextView txt_service_shipper;
        @BindView(R.id.service_task_boss)
        CardView service_task_boss;
        



        private Unbinder unbinder;



        IRecyclerClickListener recyclerClickListener;

        public void setRecyclerClickListener(IRecyclerClickListener recyclerClickListener) {
            this.recyclerClickListener = recyclerClickListener;
        }

        public MyViewHolder(@NotNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.onItemClickListener(view,getAdapterPosition());

        }
    }
}
