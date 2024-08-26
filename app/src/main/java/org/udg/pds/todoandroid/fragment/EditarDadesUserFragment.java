package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarDadesUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarDadesUserFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TodoApi mTodoService;

    private NavigationActivity navigationActivity;
    private User user;
    private Button guardarDadesButton, backButton;

    private String newNom = "";
    private String newEmail = "";
    private String newContrasenya = "";
    private String newDescripcio = "";

    private OnUserUpdateListener userUpdateListener;

    public EditarDadesUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserUpdateListener) {
            userUpdateListener = (OnUserUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnUserUpdateListener");
        }
    }

    public void setOnUserUpdateListener(OnUserUpdateListener listener){
        this.userUpdateListener = listener;
    }

    public static EditarDadesUserFragment newInstance(String param1, String param2) {
        EditarDadesUserFragment fragment = new EditarDadesUserFragment();
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
        newNom = user.username;
        newEmail = user.email;
        newDescripcio = user.descripcio;
        navigationActivity = (NavigationActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editar_dades_user, container, false);

        guardarDadesButton = view.findViewById(R.id.guardarEdicio);
        EditText editarNom = view.findViewById(R.id.editNom);
        EditText editarEmail = view.findViewById(R.id.editMail);
        EditText editarDescripcio = view.findViewById(R.id.editDescripcio);
        editarNom.setText(user.username);
        editarEmail.setText(user.email);
        editarDescripcio.setText(user.descripcio);

        guardarDadesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User userNovesDades = new User();
                userNovesDades.username = editarNom.getText().toString();
                userNovesDades.email = editarEmail.getText().toString();
                userNovesDades.descripcio = editarDescripcio.getText().toString();
                ((TodoApp) getActivity().getApplication()).actualitzarDadesPerfil(userNovesDades.username, userNovesDades.email, userNovesDades.descripcio);
                Call<User> callUser = mTodoService.update(userNovesDades);
                callUser.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.isSuccessful()){
                            getFragmentManager().beginTransaction().remove(EditarDadesUserFragment.this).commit();

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            ProfileFragment profileFragment1 = new ProfileFragment();

                            fragmentTransaction.add(R.id.nav_host_fragment, profileFragment1);
                            fragmentTransaction.setReorderingAllowed(true);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        else {
                            Toast toast = Toast.makeText(getContext(), "No shan pogut actualitzar les dades del perfil", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast toast = Toast.makeText(getContext(), "Error en la crida per editar les dades del perfil", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });

        backButton = view.findViewById(R.id.tornarAperfilBoto);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
