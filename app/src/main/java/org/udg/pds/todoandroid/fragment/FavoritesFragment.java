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
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;


    ReceptaRecyclerViewAdapter receptaRecyclerViewAdapter;
    TodoApi mTodoService;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static ReceptaFragment newInstance(int columnCount) {
        ReceptaFragment fragment = new ReceptaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recepta_recycler_view_adapter, container, false);

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
        Call<List<Recepta>> call = mTodoService.getReceptesPreferides();

        call.enqueue(new Callback<List<Recepta>>() {
            @Override
            public void onResponse(Call<List<Recepta>> call, Response<List<Recepta>> response) {
                if (response.isSuccessful()) {
                    for (Recepta r : response.body()) r.setChecked(true);
                    FavoritesFragment.this.showReceptes(response.body());
                    Toast.makeText(FavoritesFragment.this.getContext(), "Mira les receptes preferides!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FavoritesFragment.this.getContext(), "Error llegint receptes", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recepta>> call, Throwable t) {
                Toast.makeText(FavoritesFragment.this.getContext(), "Error making call", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showReceptes(List<Recepta> receptes) {
        receptaRecyclerViewAdapter.setReceptes(receptes);
    }
}
