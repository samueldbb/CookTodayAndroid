package org.udg.pds.todoandroid.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.databinding.MainBinding;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.fragment.DetallsMyReceptaFragment;
import org.udg.pds.todoandroid.fragment.OnReceptaUpdateListener;
import org.udg.pds.todoandroid.fragment.OnUserUpdateListener;
import org.udg.pds.todoandroid.fragment.ProfileFragment;

// This is the main activity that contains the bottom navigation
// This class SHOULD NOT BE CHANGED except for very specific features
public class NavigationActivity extends AppCompatActivity implements OnUserUpdateListener, OnReceptaUpdateListener {

    MainBinding binding;
    NavHostFragment navHostFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarMain);
        setUpNavigation();
    }

    public void setUpNavigation(){
        navHostFragment = (NavHostFragment)getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavigation,
            navHostFragment.getNavController());
        NavigationUI.setupWithNavController(binding.toolbarMain,
            navHostFragment.getNavController());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settingsFragment) {
            // NavigationUI.onNavDestinationSelected(item, NavHostFragment.findNavController(navHostFragment));
            NavHostFragment.findNavController(navHostFragment).navigate(R.id.settingsFragment);
        }

        return super.onOptionsItemSelected(item);
    }

    public void navigateTo(int id) {
        NavHostFragment.findNavController(navHostFragment).navigate(R.id.settingsFragment);
    }

    @Override
    public void actualitzarDadesPerfil(User updatedUser) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ProfileFragment profileFragment = (ProfileFragment) fragmentManager.findFragmentByTag("ProfileFragmentTag");
        if (profileFragment != null) {
            profileFragment.actualitzarDadesPerfil(updatedUser);
        }
    }

    @Override
    public void actualitzarDadesRecepta(Recepta updatedRecepta) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DetallsMyReceptaFragment detallsMyReceptaFragment = (DetallsMyReceptaFragment) fragmentManager.findFragmentByTag("DetallsMyReceptaFragmentTag");
        if (detallsMyReceptaFragment != null) {
            detallsMyReceptaFragment.actualitzarDadesRecepta(updatedRecepta);
        }
    }

    /*public void showProfileFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ProfileFragment profileFragment = new ProfileFragment();
        fragmentTransaction.replace(R.id.nav_host_fragment, profileFragment, "ProfileFragmentTag");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }*/

}
