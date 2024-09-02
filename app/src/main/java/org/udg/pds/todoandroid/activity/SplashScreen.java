package org.udg.pds.todoandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.TodoApp;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashScreen extends AppCompatActivity {

    private int delaySplashScreen = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

    }

    @Override
    protected void onResume() {
        super.onResume();

        TodoApi todoApi = ((TodoApp) this.getApplication()).getAPI();

        Call<User> call = todoApi.me();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Intent intent;
                if (response.isSuccessful()) {
                    // Usuario autenticado
                    ((TodoApp) SplashScreen.this.getApplication()).setUser(response.body());
                    intent = new Intent(SplashScreen.this, NavigationActivity.class);
                } else {
                    // Usuario no autenticado
                    intent = new Intent(SplashScreen.this, Login.class);
                }

                // Redirigir después de un retraso
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                }, delaySplashScreen);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast toast = Toast.makeText(SplashScreen.this, "Error checking login status", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
