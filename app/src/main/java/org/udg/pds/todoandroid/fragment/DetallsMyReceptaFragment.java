package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetallsMyReceptaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetallsMyReceptaFragment extends Fragment implements OnReceptaUpdateListener {

    private static final String ARG_RECEPTA_ID = "recepta_id";
    private Long receptaId;
    private Recepta recepta;

    View view;

    TodoApi mTodoService;

    public DetallsMyReceptaFragment() {
        // Required empty public constructor
    }

    public DetallsMyReceptaFragment(Long id) {
        receptaId = id;
    }


    public static DetallsMyReceptaFragment newInstance(Long id) {
        DetallsMyReceptaFragment fragment = new DetallsMyReceptaFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RECEPTA_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receptaId = getArguments().getLong(ARG_RECEPTA_ID);
        }
        mTodoService = ((TodoApp) getActivity().getApplication()).getAPI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_detalls_my_recepta, container, false);

        if (getArguments() != null) {
            receptaId = getArguments().getLong("receptaId");
        } else {
            Log.d("DetallsMyReceptaFragment", "getArguments() es null");
        }

        TextView nom = view.findViewById(R.id.detall_nom_my);
        TextView descripcio = view.findViewById(R.id.detall_descripcio_my);
        TextView titolPassos = view.findViewById(R.id.detall_passos_my);
        titolPassos.setText("Elaboració");
        TextView titolIngredients = view.findViewById(R.id.detall_ingredients_my);
        titolIngredients.setText("Ingredients");
        TextView passos = view.findViewById(R.id.detall_passos_list_my);
        TextView ingredients = view.findViewById(R.id.detall_ingredients_list_my);
        ImageView imatge = view.findViewById(R.id.detall_imatge_my);
        Button button = view.findViewById(R.id.button_detall);

        carregarDetalls(receptaId, nom, descripcio, passos, imatge, ingredients, button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                EditarDadesReceptaFragment editarDadesReceptaFragment = new EditarDadesReceptaFragment(recepta);

                fragmentTransaction.add(R.id.nav_host_fragment, editarDadesReceptaFragment);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void carregarDetalls(Long receptaId, TextView nom, TextView descripcio, TextView passos,
                                 ImageView imatge, TextView ingredients, Button button) {

        Call<Recepta> call = mTodoService.getRecepta(receptaId);
        call.enqueue(new Callback<Recepta>() {
            @Override
            public void onResponse(Call<Recepta> call, Response<Recepta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recepta = response.body();
                    nom.setText(recepta.nom);
                    descripcio.setText(recepta.descripcio);
                    ingredients.setText(recepta.llista_ingredients);
                    passos.setText(recepta.passos);

                    if (!recepta.imageUrl.isEmpty()) {
                        Picasso.get().load(recepta.imageUrl).into(imatge);
                    }

                }
                else {
                    Toast.makeText(DetallsMyReceptaFragment.this.getContext(), "Error obtenint recepta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Recepta> call, Throwable t) {
                Toast.makeText(DetallsMyReceptaFragment.this.getContext(), "Error fent la crida", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void actualitzarDadesRecepta(Recepta receptaActualitzada){
        recepta.nom = receptaActualitzada.nom;
        recepta.passos = receptaActualitzada.passos;
        recepta.descripcio = receptaActualitzada.descripcio;
        recepta.llista_ingredients = receptaActualitzada.llista_ingredients;

        TextView nom = view.findViewById(R.id.detall_nom_my);
        TextView passos = view.findViewById(R.id.detall_passos_list_my);
        TextView ingredients = view.findViewById(R.id.detall_ingredients_list_my);
        TextView descripcio = view.findViewById(R.id.detall_descripcio_my);


        nom.setText(recepta.nom);
        passos.setText(recepta.passos);
        descripcio.setText(recepta.descripcio);
        ingredients.setText(recepta.llista_ingredients);

        view.invalidate();

    }
}
