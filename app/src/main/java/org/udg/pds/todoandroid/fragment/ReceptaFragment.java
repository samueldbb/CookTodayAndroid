package org.udg.pds.todoandroid.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.fragment.placeholder.PlaceholderContent;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class ReceptaFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private static final String ARG_ID_CAT = "idCat";

    ReceptaRecyclerViewAdapter receptaRecyclerViewAdapter;

    TodoApi mTodoService;

    private Long idCat;
    private String paraula;

    public ReceptaFragment() {
    }

    public ReceptaFragment(long id){
        idCat = id;
    }

    public ReceptaFragment(long id, String p){
        idCat = id;
        paraula = p;
    }




    public static ReceptaFragment newInstance(long idCat) {
        ReceptaFragment fragment = new ReceptaFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID_CAT, idCat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            idCat = getArguments().getLong(ARG_ID_CAT);        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recepta_recycler_view_adapter , container, false);

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            receptaRecyclerViewAdapter = new ReceptaRecyclerViewAdapter(mTodoService);
            recyclerView.setAdapter(receptaRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        updateReceptes();

    }



    private void updateReceptes() {
        //en cas que utilitzem el buscador
        Call<List<Recepta>> call = mTodoService.getReceptesAmbParaula(paraula);

        //en cas que utilitzem filtrem per categoria
        if(idCat<10) call = mTodoService.receptesCategoria(idCat);

        call.enqueue(new Callback<List<Recepta>>() {
            @Override
            public void onResponse(Call<List<Recepta>> call, Response<List<Recepta>> response) {
                if (response.isSuccessful()) {
                    if(usuariLogejat()){
                        List<Recepta> receptes = response.body();
                        updateReceptesWithFavorites(receptes);
                    }
                    else{
                        Call<List<Recepta>> call2 = mTodoService.getReceptesPreferides();
                        call2.enqueue(new Callback<List<Recepta>>() {
                            @Override
                            public void onResponse(Call<List<Recepta>> call2, Response<List<Recepta>> response2) {
                                if (response2.isSuccessful()) {
                                    for (Recepta recepta: response.body()) {
                                        for(Recepta f: response2.body())
                                        {
                                            if (f.id == recepta.id)
                                            {
                                                recepta.setChecked(true);
                                            }
                                        }
                                    }
                                    ReceptaFragment.this.showReceptes(response.body());
                                    Toast.makeText(ReceptaFragment.this.getContext(), "Mira les receptes!", Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<List<Recepta>> call2, Throwable t) {
                                Toast.makeText(ReceptaFragment.this.getContext(), "Error fent la crida", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                } else {
                    Toast.makeText(ReceptaFragment.this.getContext(), "Error obtenint les receptes", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recepta>> call, Throwable t) {
                Toast.makeText(ReceptaFragment.this.getContext(), "Error fent la crida", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showReceptes(List<Recepta> receptes) {
        receptaRecyclerViewAdapter.setReceptes(receptes);
    }

    private boolean usuariLogejat(){
        final boolean[] registrat = {true};
        Call<User> call = mTodoService.check();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    if(response.body()==null) registrat[0] =false;
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ReceptaFragment.this.getContext(), "Error making call", Toast.LENGTH_LONG).show();
            }
        });
        return registrat[0];
    }

    private void updateReceptesWithFavorites(List<Recepta> receptes) {
        Call<List<Recepta>> callFavorites = mTodoService.getReceptesPreferides();
        callFavorites.enqueue(new Callback<List<Recepta>>() {
            @Override
            public void onResponse(Call<List<Recepta>> call, Response<List<Recepta>> response) {
                if (response.isSuccessful()) {
                    List<Recepta> receptesFavorites = response.body();
                    for (Recepta recepta : receptes) {
                        for (Recepta f : receptesFavorites) {
                            if (f.id == recepta.id) {
                                recepta.setChecked(true);
                            }
                        }
                    }
                    showReceptes(receptes);
                }
            }

            @Override
            public void onFailure(Call<List<Recepta>> call, Throwable t) {
                Toast.makeText(ReceptaFragment.this.getContext(), "Error obtenint les receptes favorites", Toast.LENGTH_LONG).show();
            }
        });
    }

}
