package org.udg.pds.todoandroid;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import android.app.Application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.rest.TodoApi;
import org.udg.pds.todoandroid.util.Global;

import java.time.format.DateTimeFormatter;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by imartin on 13/02/17.
 */
public class TodoApp extends Application {
    public static final DateTimeFormatter AppDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH:mm:ss z");

    TodoApi mTodoService;
    User user;

    @Override
    public void onCreate() {
        super.onCreate();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        ClearableCookieJar cookieJar =
            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));

        OkHttpClient httpClient = new OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(interceptor)
            .build();

        ObjectMapper jacksonMapper =
            new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl(Global.BASE_URL_PORTFORWARDING)
            //.baseUrl(Global.BASE_URL_GENYMOTION)
            .addConverterFactory(JacksonConverterFactory.create(jacksonMapper))
            .build();

        mTodoService = retrofit.create(TodoApi.class);
    }

    public TodoApi getAPI() {
        return mTodoService;
    }

    public void setUser(User u){ user = u;}
    public User getUser(){ return user;}

    public void actualitzarDadesPerfil(String nom, String email, String descripcio){
        user.setUsername(nom);
        user.setEmail(email);
        user.setDescripcio(descripcio);
    }
}
