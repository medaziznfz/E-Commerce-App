package x7030.nefzi.tjinitaw;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Database.CartDataSource;
import x7030.nefzi.tjinitaw.Database.CartDatabase;
import x7030.nefzi.tjinitaw.Database.LocalCartDataSource;
import x7030.nefzi.tjinitaw.EventBus.OffersItemClick;
import x7030.nefzi.tjinitaw.EventBus.CategoryClick;
import x7030.nefzi.tjinitaw.EventBus.CounterCartEvent;
import x7030.nefzi.tjinitaw.EventBus.FoodItemClick;
import x7030.nefzi.tjinitaw.EventBus.HideFABCart;
import x7030.nefzi.tjinitaw.EventBus.MenuItemEvent;
import x7030.nefzi.tjinitaw.EventBus.PharmacieSelectEvent;
import x7030.nefzi.tjinitaw.EventBus.PlaceOrderClicked;
import x7030.nefzi.tjinitaw.EventBus.OthersClick;
import x7030.nefzi.tjinitaw.EventBus.PlatsItemClick;
import x7030.nefzi.tjinitaw.EventBus.ProduitItemClick;
import x7030.nefzi.tjinitaw.EventBus.ReportClicked;
import x7030.nefzi.tjinitaw.EventBus.RestaurantPubClick;
import x7030.nefzi.tjinitaw.Model.CategoryModel;
import x7030.nefzi.tjinitaw.Model.FoodModel;
import x7030.nefzi.tjinitaw.Model.RestaurantModel;

public class Home extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer ;
    private NavController navController;
    private CartDataSource cartDataSource;
    private NavigationView navigationView;

    android.app.AlertDialog dialog;

    @BindView(R.id.fab)
    CounterFab fab;




    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        ButterKnife.bind(this);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_cart);

            }
        });


        {

            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            navigationView.setItemIconTintList(null);
            View header = navigationView.getHeaderView(0);
            TextView name = header.findViewById(R.id.user_user);
            name.setText(Common.currentUser.getName());
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_restaurant,R.id.nav_menu,
                    R.id.nav_food_detail,R.id.nav_foodlist,
                    R.id.nav_cart,R.id.nav_settings,R.id.nav_produit_detail,
                    R.id.nav_magasin,R.id.nav_orders,R.id.nav_others,
                    R.id.nav_contact,R.id.nav_pharmacie,R.id.nav_report)
                    .setDrawerLayout(drawer).build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            navigationView.setNavigationItemSelectedListener(this);

            EventBus.getDefault().postSticky(new HideFABCart(true));



        }}




    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CategoryClick event)
    {
        if(event.isSuccess())
        {
            navController.navigate(R.id.nav_foodlist);
        }


    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPlacedOrderClick(PlaceOrderClicked event)
    {
        if(event.isSuccess())
        {
            navController.navigate(R.id.nav_orders);
        }

    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReportClick(ReportClicked event)
    {
        if(event.isSuccess())
        {
            Toast.makeText(this, "Mercie aala e Reclamation !", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.nav_home);
        }

    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onFoodItemClick(FoodItemClick event)
    {
        if(event.isSuccess())
        {
                navController.navigate(R.id.nav_food_detail);
        }
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onProduitItemClick(ProduitItemClick event)
    {
        if(event.isSuccess())
        {
            navController.navigate(R.id.nav_produit_detail);
        }
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onPharmacieClick(PharmacieSelectEvent event)
    {
        Intent Find = new Intent(Home.this,FindThingsActivity.class);
        startActivity(Find);

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onoffersClick(OffersItemClick event)
    {
        if(event.getOffersModel() != null)

        {
            View loading_layout = LayoutInflater.from(this)
                    .inflate(R.layout.loading_dialog,null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(loading_layout);
            AlertDialog loading_dialog = builder.create();
            loading_dialog.setCancelable(false);
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            loading_dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference(event.getOffersModel().getSource())
                    .child(event.getOffersModel().getRestaurant())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            RestaurantModel tempRestaurant = snapshot.getValue(RestaurantModel.class);
                            tempRestaurant.setUid(event.getOffersModel().getRestaurant());
                            Common.RESTAURANT_REF = event.getOffersModel().getSource();

                            FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                                    .child(event.getOffersModel().getRestaurant())
                                    .child(Common.CATEGORY_REF)
                                    .child(event.getOffersModel().getMenu_id())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists())
                                            {
                                                Common.categorySelected = dataSnapshot.getValue(CategoryModel.class);
                                                Common.categorySelected.setMenu_id(dataSnapshot.getKey());
                                                FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                                                        .child(event.getOffersModel().getRestaurant())
                                                        .child(Common.CATEGORY_REF)
                                                        .child(event.getOffersModel().getMenu_id())
                                                        .child("foods").orderByChild("id")
                                                        .equalTo(event.getOffersModel().getFood_id())
                                                        .limitToLast(1)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists())
                                                                {
                                                                    for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                                                                    {
                                                                        Common.selectedFood = itemSnapShot.getValue(FoodModel.class);
                                                                        Common.selectedFood.setKey(itemSnapShot.getKey());



                                                                    }
                                                                    Common.currentRestaurant = tempRestaurant;
                                                                    EventBus.getDefault().postSticky(new MenuItemEvent(true,Common.currentRestaurant));
                                                                    navController.navigate(R.id.nav_food_detail);
                                                                    loading_dialog.dismiss();







                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(Home.this, "Prooduit moch mawjoud", Toast.LENGTH_SHORT).show();
                                                                    loading_dialog.dismiss();

                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NotNull DatabaseError databaseError) {
                                                                Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                                loading_dialog.dismiss();

                                                            }
                                                        });


                                            }
                                            else
                                            {
                                                Toast.makeText(Home.this, "Produit moch mawjoud!", Toast.LENGTH_SHORT).show();
                                                loading_dialog.dismiss();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NotNull DatabaseError databaseError) {

                                            loading_dialog.dismiss();
                                            Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });





        }


    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onplatsClick(PlatsItemClick event)
    {
        if(event.getPlatsModel() != null)

        {
            View loading_layout = LayoutInflater.from(this)
                    .inflate(R.layout.loading_dialog,null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(loading_layout);
            AlertDialog loading_dialog = builder.create();
            loading_dialog.setCancelable(false);
            loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            loading_dialog.show();

            FirebaseDatabase.getInstance()
                    .getReference(event.getPlatsModel().getSource())
                    .child(event.getPlatsModel().getRestaurant())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            RestaurantModel tempRestaurant = snapshot.getValue(RestaurantModel.class);
                            tempRestaurant.setUid(event.getPlatsModel().getRestaurant());
                            Common.RESTAURANT_REF = event.getPlatsModel().getSource();

                            FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                                    .child(event.getPlatsModel().getRestaurant())
                                    .child(Common.CATEGORY_REF)
                                    .child(event.getPlatsModel().getMenu_id())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists())
                                            {
                                                Common.categorySelected = dataSnapshot.getValue(CategoryModel.class);
                                                Common.categorySelected.setMenu_id(dataSnapshot.getKey());
                                                FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                                                        .child(event.getPlatsModel().getRestaurant())
                                                        .child(Common.CATEGORY_REF)
                                                        .child(event.getPlatsModel().getMenu_id())
                                                        .child("foods").orderByChild("id")
                                                        .equalTo(event.getPlatsModel().getFood_id())
                                                        .limitToLast(1)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists())
                                                                {
                                                                    for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                                                                    {
                                                                        Common.selectedFood = itemSnapShot.getValue(FoodModel.class);
                                                                        Common.selectedFood.setKey(itemSnapShot.getKey());



                                                                    }
                                                                    Common.currentRestaurant = tempRestaurant;
                                                                    EventBus.getDefault().postSticky(new MenuItemEvent(true,Common.currentRestaurant));
                                                                    navController.navigate(R.id.nav_food_detail);
                                                                    loading_dialog.dismiss();







                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(Home.this, "Prooduit moch mawjoud", Toast.LENGTH_SHORT).show();
                                                                    loading_dialog.dismiss();

                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NotNull DatabaseError databaseError) {
                                                                Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                                loading_dialog.dismiss();

                                                            }
                                                        });


                                            }
                                            else
                                            {
                                                Toast.makeText(Home.this, "Produit moch mawjoud!", Toast.LENGTH_SHORT).show();
                                                loading_dialog.dismiss();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NotNull DatabaseError databaseError) {

                                            loading_dialog.dismiss();
                                            Toast.makeText(Home.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });





        }


    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onOthersItemClick(OthersClick event)
    {
        navController.navigate(R.id.nav_others);
        EventBus.getDefault().postSticky(new HideFABCart(true));
    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onRestaurantItemClick(RestaurantPubClick event)
    {
        if(event.getRestaurantPubModel() != null)
        {
            Common.RESTAURANT_REF = "Restaurant";
            Bundle bundle = new Bundle();
            bundle.putString("restaurant",event.getRestaurantPubModel().getUid());
            navController.navigate(R.id.nav_menu, bundle);
            EventBus.getDefault().postSticky(new HideFABCart(false));
            countCartItem();
        }

    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawer.closeDrawers();
        switch (menuItem.getItemId())
        {

            case R.id.nav_home:
                navController.navigate(R.id.nav_home);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;
            case R.id.nav_restaurant:
                Common.RESTAURANT_REF = "Restaurant";
                navController.navigate(R.id.nav_restaurant);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;
            /*case R.id.nav_patisseries:
                Common.RESTAURANT_REF = "Patisseries";
                navController.navigate(R.id.nav_restaurant);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;


             */
            case R.id.nav_others:
                navController.navigate(R.id.nav_others);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;
            case R.id.nav_pharmacie:
                navController.navigate(R.id.nav_pharmacie);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;
            case R.id.nav_orders:
                navController.navigate(R.id.nav_orders);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;
            case R.id.nav_contact:
                navController.navigate(R.id.nav_contact);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;
            case R.id.nav_settings:
                navController.navigate(R.id.nav_settings);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;
            case R.id.nav_report:
                navController.navigate(R.id.nav_report);
                EventBus.getDefault().postSticky(new HideFABCart(true));
                break;
            /*
            case R.id.nav_magasin:
                navController.navigate(R.id.nav_magasin);
                RestaurantModel restaurantModel = new RestaurantModel();
                restaurantModel.setUid("Magasin");
                restaurantModel.setAddress("47 rue essaraha");
                restaurantModel.setImageUrl("nope");
                restaurantModel.setPhone("93477183");
                restaurantModel.setName("Magasin");
                restaurantModel.setStatus(true);

                Common.currentRestaurant = restaurantModel;
                EventBus.getDefault().postSticky(new HideFABCart(false));
                countCartItem();
                break;

             */
            case R.id.nav_logout:
                Paper.book().destroy();
                Toast.makeText(Home.this, "Filamen!", Toast.LENGTH_SHORT).show();
                Intent Main = new Intent(Home.this,MainActivity.class);
                startActivity(Main);
                EventBus.getDefault().unregister(this);
                EventBus.getDefault().removeAllStickyEvents();
                break;
        }
        return true;

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onHideFABEvent(HideFABCart event)
    {
        if(event.isHidden())
        {
            fab.hide();

        }
        else
            fab.show();


    }


    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onCartCounter(CounterCartEvent event)
    {
        if(event.isSuccess())
        {
            if (Common.currentRestaurant != null)
                countCartItem();

        }


    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void countCartAgain(CounterCartEvent event)
    {
        if(event.isSuccess())
        {
            if (Common.currentRestaurant != null)
                countCartItem();

        }


    }


    private void countCartItem() {
        cartDataSource.countItemInCart(Common.currentUser.getName(),Common.currentRestaurant.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Integer integer) {
                        fab.setCount(integer);

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (!e.getMessage().contains("Query returned empty"))
                        {

                            Toast.makeText(Home.this, "{COUNT CART}" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        else
                            fab.setCount(0);


                    }
                });


    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onRestaurantClick(MenuItemEvent event)
    {
        Bundle bundle = new Bundle();
        bundle.putString("restaurant",event.getRestaurantModel().getUid());
        navController.navigate(R.id.nav_menu, bundle);





        EventBus.getDefault().postSticky(new HideFABCart(false));
        countCartItem();



    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This is important, otherwise the result will not be passed to the fragment

        super.onActivityResult(requestCode, resultCode, data);
    }




}