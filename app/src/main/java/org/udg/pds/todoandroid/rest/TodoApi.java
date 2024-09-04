package org.udg.pds.todoandroid.rest;

import org.udg.pds.todoandroid.entity.Categoria;
import org.udg.pds.todoandroid.entity.IdObject;
import org.udg.pds.todoandroid.entity.R_recepta;
import org.udg.pds.todoandroid.entity.Recepta;
import org.udg.pds.todoandroid.entity.ReceptaString;
import org.udg.pds.todoandroid.entity.Task;
import org.udg.pds.todoandroid.entity.User;
import org.udg.pds.todoandroid.entity.UserLogin;
import org.udg.pds.todoandroid.entity.UserRegister;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by imartin on 13/02/17.
 */
public interface TodoApi {
    @POST("/users/login")
    Call<User> login(@Body UserLogin login);

    @POST("/users/register")
    Call<String> register(@Body UserRegister register);

    @POST("/users/logout")
    Call<String> logout();

    @GET("/users/check")
    Call<User> check();

    @GET("/users/me")
    Call<User> me();

    @PUT("/users/me")
    Call<User> update(@Body User usuariNou);

    @PUT("/users/me/preferits")
    Call<String> addRemoveReceptaPreferida(@Body R_recepta recepta);

    @GET("/users/me/preferits")
    Call<List<Recepta>> getReceptesPreferides();

    @GET ("/users/me/receptesPujades")
    Call<List<Recepta>> getMyReceptes();

    @POST("/users/me/addRecepta")
    Call<IdObject> addRecepta(@Body ReceptaString recepta);

    @GET("/users/me/receptesAltres")
    Call<List<Recepta>> listAllReceptes();

    @PUT("/receptes/edit/{id}")
    Call<IdObject> editRecepta(@Path("id") Long id, @Body ReceptaString recepta);

    @GET("/receptes/conte/{paraula}")
    Call<List<Recepta>> getReceptesAmbParaula(@Path("paraula") String paraula);

    @GET("/receptes/{id}")
    Call<Recepta> getRecepta(@Path("id") Long id);

    @POST("/images")
    @Multipart
    Call<String> uploadImage(@Part MultipartBody.Part file);

    @GET("/categories")
    Call<List<Categoria>> llistarCategories();

    @GET("/categories/{id}/receptes")
    Call<List<Recepta>> receptesCategoria(@Path("id") Long id);



}

