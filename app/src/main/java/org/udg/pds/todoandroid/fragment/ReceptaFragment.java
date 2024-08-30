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

    public ReceptaFragment() {
    }

    public ReceptaFragment(long id){
        idCat = id;
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

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            receptaRecyclerViewAdapter = new ReceptaRecyclerViewAdapter();
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
        Call<List<Recepta>> call = mTodoService.receptesCategoria(idCat);

        call.enqueue(new Callback<List<Recepta>>() {
            @Override
            public void onResponse(Call<List<Recepta>> call, Response<List<Recepta>> response) {
                if (response.isSuccessful()) {
                    ReceptaFragment.this.showReceptes(response.body());
                    Toast.makeText(ReceptaFragment.this.getContext(), "Mira les receptes!", Toast.LENGTH_LONG).show();
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

}
