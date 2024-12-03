package x7030.nefzi.tjinitaw.ui.menumagasin;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import x7030.nefzi.tjinitaw.Adapter.MyCategoriesAdapter;
import x7030.nefzi.tjinitaw.Common.Common;
import x7030.nefzi.tjinitaw.Common.SpaceItemDecoration;
import x7030.nefzi.tjinitaw.EventBus.HideFABCart;
import x7030.nefzi.tjinitaw.Model.CategoryModel;
import x7030.nefzi.tjinitaw.R;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;
    Unbinder unbinder ;
    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    LayoutAnimationController layoutAnimationController;
    MyCategoriesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                new ViewModelProvider(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu_magasin,container,false);
        unbinder = ButterKnife.bind(this,root);
        View loading_layout = LayoutInflater.from(getContext())
                .inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setView(loading_layout);
        AlertDialog loading_dialog = builder.create();
        loading_dialog.setCancelable(false);
        loading_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading_dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        loading_dialog.show();


        initViews();


        menuViewModel.getMessageError().observe(getViewLifecycleOwner(),s -> {
            loading_dialog.dismiss();

            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();


        });
        menuViewModel.getCategoryListMutable().observe(getViewLifecycleOwner() ,categoryModelList -> {

            adapter = new MyCategoriesAdapter(getContext(),categoryModelList);
            recycler_menu.setAdapter(adapter);
            recycler_menu.setLayoutAnimation(layoutAnimationController);
            loading_dialog.dismiss();
        });
        return root;
    }

    private void initViews() {

        setHasOptionsMenu(true);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter != null)
                {
                    switch (adapter.getItemViewType(position))
                    {
                        case Common.DEFAULT_COLUMN_COUNT:return 1;
                        case Common.FULL_WIDTH_COLUMN:return 2;
                        default:return -1;

                    }

                }

                return -1;

            }
        });
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.addItemDecoration(new SpaceItemDecoration(8));






    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(view -> {
            EditText ed = (EditText)searchView.findViewById(R.id.search_src_text);
            ed.setText("");
            searchView.setQuery("",false);
            searchView.onActionViewCollapsed();
            menuItem.collapseActionView();
            menuViewModel.loadCategories();
        });
    }

    private void startSearch(String s) {
        List<CategoryModel> resultList = new ArrayList<>();
        for (int i=0 ; i<adapter.getListCategory().size();i++)
        {

            CategoryModel categoryModel = adapter.getListCategory().get(i);
            if (categoryModel.getName().toLowerCase().contains(s))
                resultList.add(categoryModel);



        }
        menuViewModel.getCategoryListMutable().setValue(resultList);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().postSticky(new HideFABCart(false));
        super.onStart();
    }
}
