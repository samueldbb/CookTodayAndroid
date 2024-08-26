package org.udg.pds.todoandroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.entity.UserRegister;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.udg.pds.todoandroid.databinding.ActivityRegisterBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {
    TodoApi mTodoService;
    private ActivityRegisterBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTodoService = ((TodoApp) this.getApplication()).getAPI();

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegisterActivity.this.checkCredentials(binding.TextNom.getText().toString(),
                    binding.TextContrasenya.getText().toString(),binding.TextEmail.getText().toString(),binding.TextDescripcio.getText().toString());
            }
        });


        //Anar a la pantalla anterior
        binding.BotoTornar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void checkCredentials(String username, String password,String email,String descripcio) {
        UserRegister ur = new UserRegister();
        ur.username = username;
        ur.password = password;
        ur.email = email;
        ur.descripcio = descripcio;
        Call<String> call = mTodoService.register(ur);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()) {
                    autoLogin(username, password);
                } else {
                    Toast toast = Toast.makeText(RegisterActivity.this, "Error Register", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast toast = Toast.makeText(RegisterActivity.this, "Error Register", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void autoLogin(String username, String password) {
        UserLogin ul = new UserLogin();
        ul.username = username;
        ul.password = password;
        Call<User> call = mTodoService.login(ul);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    ((TodoApp) RegisterActivity.this.getApplication()).setUser(response.body());
                    RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, NavigationActivity.class));
                    RegisterActivity.this.finish();
                } else {
                    Toast toast = Toast.makeText(RegisterActivity.this, "Error al fer login despres del registre", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast toast = Toast.makeText(RegisterActivity.this, "Error al la crida de login despres del registre", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
