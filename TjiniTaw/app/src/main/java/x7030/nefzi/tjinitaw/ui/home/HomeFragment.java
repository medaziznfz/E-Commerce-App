package x7030.nefzi.tjinitaw.ui.home;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asksira.loopingviewpager.LoopingViewPager;
import org.greenrobot.eventbus.EventBus;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.Adapter.MyPlatsAdapter;
import x7030.nefzi.tjinitaw.Adapter.MyOffersAdapter;
import x7030.nefzi.tjinitaw.Adapter.MyOthersAdapter;
import x7030.nefzi.tjinitaw.Adapter.MyRestaurantPubAdapter;
import x7030.nefzi.tjinitaw.EventBus.HideFABCart;
import x7030.nefzi.tjinitaw.R;

public class HomeFragment extends Fragment {

    Unbinder unbinder ;
    @BindView(R.id.viewpager_others)
    LoopingViewPager viewpager_others;
    @BindView(R.id.viewpager_offre)
    LoopingViewPager viewpager_offre;
    @BindView(R.id.viewpager_plats)
    LoopingViewPager viewpager_plats;
    @BindView(R.id.recycler_popular)
    RecyclerView recycler_popular;
    @BindView(R.id.txt_qadhya)
    TextView txt_qadhya;
    @BindView(R.id.txt_afar)
    TextView txt_afar;

    private HomeViewModel homeViewModel;

    LayoutAnimationController layoutAnimationController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home,container,false);


        unbinder = ButterKnife.bind(this,root);
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();


        homeViewModel.getRestaurantList().observe(getViewLifecycleOwner(),restaurantPubModels ->{

            MyRestaurantPubAdapter adapter = new MyRestaurantPubAdapter(getContext(),restaurantPubModels);
            recycler_popular.setAdapter(adapter);
            recycler_popular.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();

        } );


        homeViewModel.getOthersList().observe(getViewLifecycleOwner(),othersModels -> {
            MyOthersAdapter adapter = new MyOthersAdapter(getContext(),othersModels,true);
            viewpager_others.setAdapter(adapter);
            loading_dialog.dismiss();

        } );


        homeViewModel.getOffresShop().observe(getViewLifecycleOwner(),offresModels -> {
            MyOffersAdapter adapter = new MyOffersAdapter(getContext(),offresModels,true);
            viewpager_offre.setAdapter(adapter);
            loading_dialog.dismiss();



        });


        homeViewModel.getPlatsList().observe(getViewLifecycleOwner(),platsModels -> {
            MyPlatsAdapter adapter = new MyPlatsAdapter(getContext(),platsModels,true);
            viewpager_plats.setAdapter(adapter);
            loading_dialog.dismiss();



        });
        init();



        return root;
    }

    private void init() {

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        recycler_popular.setHasFixedSize(true);
        recycler_popular.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().postSticky(new HideFABCart(true));
        viewpager_plats.resumeAutoScroll();
        viewpager_offre.resumeAutoScroll();
        viewpager_others.resumeAutoScroll();
    }

    @Override
    public void onPause() {
        viewpager_plats.pauseAutoScroll();
        viewpager_offre.pauseAutoScroll();
        viewpager_others.pauseAutoScroll();
        super.onPause();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().postSticky(new HideFABCart(true));
        super.onStart();
    }
}