package x7030nefzi.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import io.paperdb.Paper;
import x7030nefzi.admin.Common.Common;
import x7030nefzi.admin.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer ;
    private NavController navController;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        {
            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            navigationView.getBackground().setAlpha(200);
            navigationView.setItemIconTintList(null);
            View header = navigationView.getHeaderView(0);
            TextView name = header.findViewById(R.id.user_user);
            name.setText(Common.currentAdmin.getName());
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home,R.id.nav_shippers,
                    R.id.nav_tasks,R.id.nav_discount)
                    .setDrawerLayout(drawer).build();
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            navigationView.setNavigationItemSelectedListener(this);

        }}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawer.closeDrawers();
        switch (menuItem.getItemId())
        {
            case R.id.nav_home:
                navController.navigate(R.id.nav_home);
                break;
            case R.id.nav_shippers:
                navController.navigate(R.id.nav_shippers);
                break;
            case R.id.nav_tasks:
                navController.navigate(R.id.nav_tasks);
                break;
            case R.id.nav_discount:
                navController.navigate(R.id.nav_discount);
                break;
            case R.id.nav_logout:
                Paper.book().destroy();
                Toast.makeText(HomeActivity.this, "Filamen!", Toast.LENGTH_SHORT).show();
                Intent Main = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(Main);
                EventBus.getDefault().removeAllStickyEvents();
                break;
        }
        return true;

    }
}