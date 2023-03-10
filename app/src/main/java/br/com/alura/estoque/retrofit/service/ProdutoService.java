package br.com.alura.estoque.retrofit.service;

import java.util.List;

import br.com.alura.estoque.model.Produto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProdutoService {

    //aqui vai ter todas as requisições HTTP


    @GET("produto")
    Call<List<Produto>> buscaTodos();

    @POST("produto")
    Call<Produto> salvar(@Body Produto produto);


    @PUT("produto/{id}")
    Call<Produto> editar(@Path("id") long id, @Body Produto produto);

    @DELETE("produto/{id}")
    Call<Void> remover(@Path("id") long id);
}
