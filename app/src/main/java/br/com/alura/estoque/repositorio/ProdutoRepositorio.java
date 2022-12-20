package br.com.alura.estoque.repositorio;

import android.content.Context;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.callback.CallBackComRetorno;
import br.com.alura.estoque.retrofit.callback.CallbackSemRetorno;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import retrofit2.Call;

public class ProdutoRepositorio {

    private final ProdutoService produtoService;
    private final ProdutoDAO dao;

    public ProdutoRepositorio(Context context) {
        EstoqueDatabase db = EstoqueDatabase.getInstance(context);
        dao = db.getProdutoDAO();
        produtoService = new EstoqueRetrofit().getProdutoService();
    }

    public void buscaProdutos(DadosCarregadosCallBack<List<Produto>> callBack) {

        buscaProdutosInternos(callBack);
    }

    public void buscaProdutosInternos(DadosCarregadosCallBack<List<Produto>> callBack) {
        //1°- Fazer a busca interna primeiro
        new BaseAsyncTask<>(dao::buscaTodos, resultado -> {

            //notifica que o dado está pronto
            callBack.quandoSucesso(resultado);

            buscaProdutosNaApi(callBack);


        }).execute();
    }

    public void buscaProdutosNaApi(DadosCarregadosCallBack<List<Produto>> callBack) {


        //buscando na API
        Call<List<Produto>> call = produtoService.buscaTodos();
        call.enqueue(new CallBackComRetorno<>(new CallBackComRetorno.RespostaCallBack<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> produtos) {

                atualizarProdutosInterno(produtos, callBack);
            }

            @Override
            public void quandoFalha(String erro) {

                callBack.quandoFalha(erro);
            }
        }));

    }

    private void atualizarProdutosInterno(List<Produto> produtosNovos, DadosCarregadosCallBack<List<Produto>> callBack) {
        new BaseAsyncTask<>(() -> {
            dao.salvaLista(produtosNovos);
            return dao.buscaTodos();
        }, callBack::quandoSucesso).execute();
    }

    public void salva(Produto produto, DadosCarregadosCallBack<Produto> callBack) {

        salvaNaApi(produto, callBack);
    }

    private void salvaNaApi(Produto produto, DadosCarregadosCallBack<Produto> callBack) {

        //primeiro vamos salvar na API(externo)
        Call<Produto> call = produtoService.salvar(produto);
        call.enqueue(new CallBackComRetorno<>(new CallBackComRetorno.RespostaCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoSalvo) {

                salvaInterno(produtoSalvo, callBack);
            }

            @Override
            public void quandoFalha(String erro) {

                callBack.quandoFalha(erro);
            }
        }));
    }

    private void salvaInterno(Produto produtoSalvo, DadosCarregadosCallBack<Produto> callBack) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salvaLista(produtoSalvo);
            return dao.buscaProduto(id);
        }, callBack::quandoSucesso).execute();
    }

    public void edita(Produto produto, DadosCarregadosCallBack<Produto> callBack) {

        editaNaApi(produto, callBack);


    }

    private void editaNaApi(Produto produto, DadosCarregadosCallBack<Produto> callBack) {
        //primeiro vamos editar externamente
        Call<Produto> call = produtoService.editar(produto.getId(), produto);
        call.enqueue(new CallBackComRetorno<>(new CallBackComRetorno.RespostaCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto resultado) {

                editaInterno(produto, callBack);
            }

            @Override
            public void quandoFalha(String erro) {
                callBack.quandoFalha(erro);

            }
        }));
    }

    private void editaInterno(Produto produto, DadosCarregadosCallBack<Produto> callBack) {
        //editar internamente agora
        //notificar a activity
        new BaseAsyncTask<>(() -> {
            dao.atualiza(produto);
            return produto;
        }, callBack::quandoSucesso).execute();
    }

    public void remove(Produto produtoRemovido, DadosCarregadosCallBack<Void> callBack) {


        removerNaApi(produtoRemovido, callBack);

    }

    private void removerNaApi(Produto produtoRemovido, DadosCarregadosCallBack<Void> callBack) {
        //removendo na API WEB
        Call<Void> call = produtoService.remover(produtoRemovido.getId());

        call.enqueue(new CallbackSemRetorno(new CallbackSemRetorno.RespostaCallBack() {
            @Override
            public void quandoSucesso() {

                removerInterno(produtoRemovido, callBack);
            }

            @Override
            public void quandoFalha(String erro) {

                callBack.quandoFalha(erro);
            }
        }));
    }

    private void removerInterno(Produto produtoRemovido, DadosCarregadosCallBack<Void> callBack) {
        new BaseAsyncTask<>(() -> {
            dao.remove(produtoRemovido);
            return null;
        }, callBack::quandoSucesso).execute();
    }

    public interface DadosCarregadosCallBack<T> {

        void quandoSucesso(T resultado);

        void quandoFalha(String erro);
    }
}
