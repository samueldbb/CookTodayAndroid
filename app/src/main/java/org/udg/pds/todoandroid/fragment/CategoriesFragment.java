package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private long idCat;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_categories, container, false);

        setOnClickListeners(view);

        SearchView buscador = view.findViewById(R.id.searchView);

        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String paraula) {

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ReceptaFragment receptaFragment = new ReceptaFragment(100, paraula);

                fragmentTransaction.replace(R.id.nav_host_fragment, receptaFragment);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return true;
            }
            @Override
            public boolean onQueryTextChange(String paraula) {
                return false;
            }
        });

        return view;
    }


    private void setOnClickListeners(View view) {
        setClickListener(view, R.id.imageTots, 0);
        setClickListener(view, R.id.imageOriental, 1);
        setClickListener(view, R.id.imageVeggie, 2);
        setClickListener(view, R.id.imagePeix, 3);
        setClickListener(view, R.id.imageMediterrani, 4);
        setClickListener(view, R.id.imageCarn, 5);
        setClickListener(view, R.id.imageItalia, 6);
        setClickListener(view, R.id.imageAltres, 7);
    }

    private void setClickListener(View view, int imageViewId, long idCatValue) {
        ImageView imageView = view.findViewById(imageViewId);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ReceptaFragment receptaFragment = new ReceptaFragment(idCatValue);

                fragmentTransaction.replace(R.id.nav_host_fragment, receptaFragment);
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });
    }
}
