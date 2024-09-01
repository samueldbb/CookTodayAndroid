package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Use the {@link DetallsReceptaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetallsReceptaFragment extends Fragment {

    private static final String ARG_RECEPTA_ID = "recepta_id";
    private Long receptaId;
    private Recepta recepta;

    TodoApi mTodoService;

    public DetallsReceptaFragment() {
        // Required empty public constructor
    }

    public DetallsReceptaFragment(Long id) {
        receptaId = id;
    }

    public static DetallsReceptaFragment newInstance(Long id) {
        DetallsReceptaFragment fragment = new DetallsReceptaFragment();
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
        View view = inflater.inflate(R.layout.fragment_detalls_recepta, container, false);

        if (getArguments() != null) {
            receptaId = getArguments().getLong("receptaId");
            Log.d("DetallsReceptaFragment", "receptaId: " + receptaId);
        } else {
            Log.d("DetallsReceptaFragment", "getArguments() es null");
        }

        TextView nom = view.findViewById(R.id.detall_nom);
        TextView descripcio = view.findViewById(R.id.detall_descripcio);
        TextView titolPassos = view.findViewById(R.id.detall_passos);
        titolPassos.setText("Elaboració");
        TextView titolIngredients = view.findViewById(R.id.detall_ingredients);
        titolIngredients.setText("Ingredients");
        TextView passos = view.findViewById(R.id.detall_passos_list);
        TextView ingredients = view.findViewById(R.id.detall_ingredients_list);
        ImageView imatge = view.findViewById(R.id.detall_imatge);

        carregarDetalls(receptaId, nom, descripcio, passos, imatge, ingredients);

        return view;
    }

    private void carregarDetalls(Long receptaId, TextView nom, TextView descripcio, TextView passos,
                                 ImageView imatge, TextView ingredients) {

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
                    Toast.makeText(DetallsReceptaFragment.this.getContext(), "Error obtenint recepta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Recepta> call, Throwable t) {
                Toast.makeText(DetallsReceptaFragment.this.getContext(), "Error fent la crida", Toast.LENGTH_LONG).show();
            }
        });
    }

}
