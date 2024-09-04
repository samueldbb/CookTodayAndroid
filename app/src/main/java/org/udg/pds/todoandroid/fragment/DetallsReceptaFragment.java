package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.R_recepta;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
        CheckBox checkBox = view.findViewById(R.id.checkbox_detall);
        Button button = view.findViewById(R.id.botonback_detalls);

        carregarDetalls(receptaId, nom, descripcio, passos, imatge, ingredients, checkBox);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CategoriesFragment categoriesFragment = new CategoriesFragment();

                fragmentTransaction.add(R.id.nav_host_fragment, categoriesFragment);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void carregarDetalls(Long receptaId, TextView nom, TextView descripcio, TextView passos,
                                 ImageView imatge, TextView ingredients, CheckBox checkBox) {

        Call<Recepta> call = mTodoService.getRecepta(receptaId); // Crida a la API
        call.enqueue(new Callback<Recepta>() {
            @Override
            public void onResponse(Call<Recepta> call, Response<Recepta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recepta = response.body(); //Rebem les dades de la recepta
                    nom.setText(recepta.nom);
                    descripcio.setText(recepta.descripcio);
                    ingredients.setText(recepta.llista_ingredients);
                    passos.setText(recepta.passos);
                    if (!recepta.imageUrl.isEmpty()) {
                        Picasso.get().load(recepta.imageUrl).into(imatge);
                    }

                    actualitzarCheckbox(checkBox); //obtenim les dades del cor de preferis
                    checkBox.setChecked(recepta.isChecked());
                    // Gestionem el fer click a sobre del cor per afegir/treure de preferits
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        R_recepta r = new R_recepta();
                        r.id = recepta.id;
                        r.posar = isChecked;
                        Call<String> callUpdate = mTodoService.addRemoveReceptaPreferida(r);
                        callUpdate.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    recepta.isChecked = isChecked;
                                    Toast.makeText(getContext(), "Afegit a preferits", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Error al afegir a preferits", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getContext(), "Error a la crida", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
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

    private void actualitzarCheckbox(CheckBox checkBox) {
        Call<List<Recepta>> callFavorites = mTodoService.getReceptesPreferides(); // Crida a la API
        callFavorites.enqueue(new Callback<List<Recepta>>() {
            @Override
            public void onResponse(Call<List<Recepta>> call, Response<List<Recepta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recepta> receptesFavorites = response.body();
                    for (Recepta f : receptesFavorites) {
                        if (f.id.equals(recepta.id)) {
                            checkBox.setChecked(true);
                            return;
                        }
                    }
                    checkBox.setChecked(false);
                }
            }

            @Override
            public void onFailure(Call<List<Recepta>> call, Throwable t) {
                Toast.makeText(getContext(), "Error obteniendo recetas favoritas", Toast.LENGTH_LONG).show();
            }
        });
    }
}
