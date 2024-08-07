package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.databinding.FragmentNewRecipeBinding;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.entity.ReceptaString;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.util.ArrayList;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewRecipeFragment extends Fragment implements Callback<IdObject> {

    TodoApi mTodoService;

    FragmentNewRecipeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        binding = FragmentNewRecipeBinding.inflate(inflater);
        View root = binding.getRoot();

        Spinner llistat_categories = root.findViewById(R.id.categoria_recepta);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        llistat_categories.setAdapter(adapter);

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        FragmentManager fm = getParentFragmentManager();

        binding.pujarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String nom = binding.nomRecepta.getText().toString();
                    String descripcio = binding.descripcioRecepta.getText().toString();
                    Collection<String> categories = new ArrayList<>();
                    categories.add(binding.categoriaRecepta.getSelectedItem().toString());
                    ReceptaString recepta = new ReceptaString();
                    recepta.nom = nom;
                    recepta.descripcio = descripcio;
                    recepta.categories = categories;
                    Call<IdObject> call = mTodoService.addRecepta(recepta);
                    call.enqueue(NewRecipeFragment.this);
                } catch (Exception ex) {
                    Toast.makeText(NewRecipeFragment.this.getContext(), "Error pujant la recepta", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }
    @Override
    public void onFailure(Call<IdObject> call, Throwable t) {
        Toast.makeText( this.getContext(), "Error al pujar la recepta", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Call<IdObject> call, Response<IdObject> response) {
        if (response.isSuccessful()) {
            Toast.makeText(this.getContext(), "Pujat correctament", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this.getContext(), "No s'ha pogut pujar", Toast.LENGTH_LONG).show();
        }
    }


}
