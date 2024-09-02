package org.udg.pds.todoandroid.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.Login;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements OnUserUpdateListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TodoApi mTodoService;
    private NavigationActivity nAct;
    private User user;

    private ProfileAdapter adapter;

    private OnUserUpdateListener userUpdateListener;

    View view;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public void setOnUserUpdateListener(OnUserUpdateListener listener){
        this.userUpdateListener = listener;
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mTodoService = ((TodoApp) getActivity().getApplication()).getAPI();
        user = ((TodoApp) getContext().getApplicationContext()).getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button logoutButton = view.findViewById(R.id.logout_button);
        Button editButton = view.findViewById(R.id.botoEditar);

        nAct = (NavigationActivity) getActivity();
        mTodoService = ((TodoApp) getActivity().getApplication()).getAPI();

        // LOGOUT

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<String> call = mTodoService.logout();
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Intent i = new Intent(getActivity(), Login.class);
                            ((TodoApp) getContext().getApplicationContext()).setUser(null);
                            startActivity(i);
                            getActivity().finish();
                        }
                        else {
                            Toast toast = Toast.makeText(getActivity(), "No s'ha pogut fer logout", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("ProfileFragment", "Error fent logout", t);
                        Toast toast = Toast.makeText(getActivity(), "Error fent logout: " + t.getMessage(), Toast.LENGTH_SHORT);
                        toast.show();

                    }


                });
            }
        });

        // DADES PERFIL
        if(user != null){
            TextView nom = view.findViewById(R.id.nomPerfil);
            TextView email = view.findViewById(R.id.correuPerfil);
            TextView descripcio = view.findViewById(R.id.descripcioPerfil);
            RecyclerView rv = view.findViewById(R.id.recyclerViewProfile);

            nom.setText(user.username);
            email.setText(user.email);
            descripcio.setText(user.descripcio);

            rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            adapter = new ProfileAdapter();
            rv.setAdapter(adapter);
            fetchReceptesUsuari();
        }
        else {
            Toast t = Toast.makeText(getActivity(), "Error mostrant les dades l'usuari", Toast.LENGTH_LONG);
            t.show();
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                EditarDadesUserFragment editarDadesUserFragment = new EditarDadesUserFragment();

                fragmentTransaction.add(R.id.nav_host_fragment, editarDadesUserFragment);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return view;
    }

    //EDITAR DADES
    public void actualitzarDadesPerfil(User userActualitzat){
        user.username = userActualitzat.username;
        user.email = userActualitzat.email;
        user.descripcio = userActualitzat.descripcio;

        TextView nom = view.findViewById(R.id.nomPerfil);
        TextView email = view.findViewById(R.id.correuPerfil);
        TextView descripcio = view.findViewById(R.id.descripcioPerfil);

        nom.setText(user.username);
        email.setText(user.email);
        descripcio.setText(user.descripcio);

        view.invalidate();

    }

    private void fetchReceptesUsuari() {
        Call<List<Recepta>> call = mTodoService.getMyReceptes();

        call.enqueue(new Callback<List<Recepta>>() {
            @Override
            public void onResponse(Call<List<Recepta>> call, Response<List<Recepta>> response) {
                if (response.isSuccessful()) {
                    adapter.setProducts(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error fetching user uploaded products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recepta>> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching user uploaded products", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
