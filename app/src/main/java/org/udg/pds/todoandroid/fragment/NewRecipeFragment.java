package org.udg.pds.todoandroid.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.databinding.FragmentNewRecipeBinding;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.entity.ReceptaString;
import org.udg.pds.todoandroid.rest.TodoApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRecipeFragment extends Fragment implements Callback<IdObject> {

    TodoApi mTodoService;
    FragmentNewRecipeBinding binding;

    Uri selectedImage = null;
    String imgUrl = "";

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentNewRecipeBinding.inflate(inflater);
        View root = binding.getRoot();

        Spinner llistat_categories = root.findViewById(R.id.categoria_recepta);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        llistat_categories.setAdapter(adapter);

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();

        FragmentManager fm = getParentFragmentManager();

        // Register the launcher and define the callback for the result
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImage = result.getData().getData();
                    binding.ivPreview.setImageURI(selectedImage);
                }
            });

        binding.pujarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String nom = binding.nomRecepta.getText().toString();
                    String descripcio = binding.descripcioRecepta.getText().toString();
                    Collection<String> categories = new ArrayList<>();
                    String imageUrl = imgUrl;
                    categories.add(binding.categoriaRecepta.getSelectedItem().toString());
                    Recepta recepta = new Recepta();
                    recepta.nom = nom;
                    recepta.descripcio = descripcio;
                    recepta.categories = categories;
                    recepta.imageUrl = imageUrl;
                    Call<IdObject> call = mTodoService.addRecepta(recepta);
                    call.enqueue(NewRecipeFragment.this);
                    buida();
                } catch (Exception ex) {
                    Toast.makeText(NewRecipeFragment.this.getContext(), "Error pujant la recepta", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }

    private void buida() {
        binding.nomRecepta.setText("");
        binding.descripcioRecepta.setText("");
        binding.ivPreview.setImageURI(null);
        binding.ivDownload.setImageDrawable(null);
        binding.categoriaRecepta.setSelection(0);

        selectedImage= null;
        imgUrl= "";
    }

    @Override
    public void onStart() {
        super.onStart();

        mTodoService = ((TodoApp) this.getActivity().getApplication()).getAPI();
        binding.bSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imagePickerLauncher.launch(Intent.createChooser(intent, "Selecciona imatge"));
            }
        });

        binding.bUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage != null) {
                    try {
                        InputStream is = getContext().getContentResolver().openInputStream(selectedImage);
                        String extension = "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(getContext().getContentResolver().getType(selectedImage));
                        File tempFile = File.createTempFile("upload", extension, getContext().getCacheDir());
                        FileOutputStream outs = new FileOutputStream(tempFile);
                        IOUtils.copy(is, outs);
                        // create RequestBody instance from file
                        RequestBody requestFile =
                            RequestBody.create(
                                MediaType.parse(getContext().getContentResolver().getType(selectedImage)),
                                tempFile
                            );

                        // MultipartBody.Part is used to send also the actual file name
                        MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

                        Call<String> call = mTodoService.uploadImage(body);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    imgUrl = response.body().toString();
                                    Toast.makeText(getContext(), "Imatge pujada correctament!", Toast.LENGTH_SHORT).show();
                                    Picasso.get().load(response.body()).into(binding.ivDownload);
                                }
                                else
                                    Toast.makeText(getContext(), "Error al pujar", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getContext(), "La pujada ha fallat", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(getContext(), "no has seleccionat res", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onResponse(Call<IdObject> call, Response<IdObject> response) {
        if (response.isSuccessful()) {
            Toast.makeText(this.getContext(), "Pujat correctament", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this.getContext(), "No s'ha pogut pujar", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<IdObject> call, Throwable t) {
        Toast.makeText(this.getContext(), "Error al pujar la recepta", Toast.LENGTH_LONG).show();
    }

}
