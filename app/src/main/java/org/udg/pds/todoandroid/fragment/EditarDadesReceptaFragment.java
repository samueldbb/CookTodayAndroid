package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.activity.NavigationActivity;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.entity.ReceptaString;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarDadesReceptaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarDadesReceptaFragment extends Fragment {

    private static final String ARG_RECEPTA = "recepta";

    TodoApi mTodoService;

    private NavigationActivity navigationActivity;
    private Recepta recepta;
    private Button guardarDadesButton, backButton, seleccionarImatge, pujarImatge;

    private String newNom = "";
    private String newDescripcio = "";
    private String newPassos = "";
    private String newIngredients = "";
    String imgUrl = "";

    OnReceptaUpdateListener receptaUpdateListener;

    public EditarDadesReceptaFragment() {
        // Required empty public constructor
    }

    public EditarDadesReceptaFragment(Recepta recept) {
        recepta = recept;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReceptaUpdateListener) {
            receptaUpdateListener = (OnReceptaUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnReceptaUpdateListener");
        }
    }

    public void setOnReceptaUpdateListener(OnReceptaUpdateListener listener){
        this.receptaUpdateListener = listener;
    }

    public static EditarDadesReceptaFragment newInstance(Recepta recepta) {
        EditarDadesReceptaFragment fragment = new EditarDadesReceptaFragment();
        Bundle args = new Bundle();
        //args.putSerializable(ARG_RECEPTA, recepta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // recepta = (Recepta) getArguments().getSerializable(ARG_RECEPTA);
        }

        mTodoService = ((TodoApp) getActivity().getApplication()).getAPI();
        newNom = recepta.nom;
        newDescripcio = recepta.descripcio;
        newIngredients = recepta.llista_ingredients;
        newPassos = recepta.passos;
        navigationActivity = (NavigationActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editar_dades_recepta, container, false);

        guardarDadesButton = view.findViewById(R.id.pujar_button_edit);
        EditText editarNom = view.findViewById(R.id.nom_recepta_edit);
        EditText editarDescripcio = view.findViewById(R.id.descripcio_recepta_edit);
        EditText editarPassos = view.findViewById(R.id.passos_recepta_edit);
        EditText editarIngredients = view.findViewById(R.id.ingredients_recepta_edit);

        editarNom.setText(recepta.nom);
        editarDescripcio.setText(recepta.descripcio);
        editarIngredients.setText(recepta.llista_ingredients);
        editarPassos.setText(recepta.passos);


        guardarDadesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceptaString receptaNovesDades = new ReceptaString();
                receptaNovesDades.nom = editarNom.getText().toString();
                receptaNovesDades.descripcio = editarDescripcio.getText().toString();
                receptaNovesDades.passos = editarPassos.getText().toString();
                receptaNovesDades.ingredients = editarIngredients.getText().toString();
                Call<IdObject> call = mTodoService.editRecepta(recepta.id, receptaNovesDades);
                call.enqueue(new Callback<IdObject>() {
                    @Override
                    public void onResponse(Call<IdObject> call, Response<IdObject> response) {
                        if(response.isSuccessful()){
                            getFragmentManager().beginTransaction().remove(EditarDadesReceptaFragment.this).commit();

                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            DetallsMyReceptaFragment detallsMyReceptaFragment = new DetallsMyReceptaFragment(recepta.id);

                            fragmentTransaction.add(R.id.nav_host_fragment, detallsMyReceptaFragment);
                            fragmentTransaction.setReorderingAllowed(true);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        else {
                            Toast toast = Toast.makeText(getContext(), "No shan pogut actualitzar les dades de la recepta", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<IdObject> call, Throwable t) {
                        Toast toast = Toast.makeText(getContext(), "Error en la crida per editar les dades de la recepta", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });

        return view;
    }
}
