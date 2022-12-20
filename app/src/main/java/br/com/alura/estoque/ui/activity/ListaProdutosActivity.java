package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repositorio.ProdutoRepositorio;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private static final String MENSAGEM_ERRO_EDITAR = "Não foi possível editar o produto";
    private static final String MENSAGEM_ERRO_SALVAR = "Não foi possível salvar o produto";
    private static final String MENSAGEM_ERRO_REMOVER = "Não foi possível remover o produto";
    private static final String MENSAGEM_ERRO_ATUALIZAR = "Não foi possível atualizar o produto";
    private ListaProdutosAdapter adapter;
    private ProdutoRepositorio repositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();


        repositorio = new ProdutoRepositorio(this);


        buscaProdutos();
    }

    private void buscaProdutos() {
        repositorio.buscaProdutos(new ProdutoRepositorio.DadosCarregadosCallBack<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> resultado) {

                adapter.atualiza(resultado);
            }

            @Override
            public void quandoFalha(String erro) {

                mostrarErro(MENSAGEM_ERRO_ATUALIZAR);
            }
        });
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void configuraListaProdutos() {

        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        remover();
    }

    private void remover() {
        adapter.setOnItemClickRemoveContextMenuListener(this::remove);
    }


    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> {
            abreFormularioSalvaProduto();
        });
    }

    private void abreFormularioSalvaProduto() {


        salva();

    }

    private void salva() {
        new SalvaProdutoDialog(this, produtoCriado -> repositorio.salva(produtoCriado,
                new ProdutoRepositorio.DadosCarregadosCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoSalvo) {

                adapter.adiciona(produtoSalvo);
            }

            @Override
            public void quandoFalha(String erro) {

                mostrarErro(MENSAGEM_ERRO_SALVAR);
            }
        })).mostra();
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        edita(posicao, produto);
    }

    private void edita(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto, produtoEditado ->
                repositorio.edita(produtoEditado,
                        new ProdutoRepositorio.DadosCarregadosCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto resultado) {

                adapter.edita(posicao, resultado);
            }

            @Override
            public void quandoFalha(String erro) {

                mostrarErro(MENSAGEM_ERRO_EDITAR);
            }
        })).mostra();
    }

    private void remove(int posicao, Produto produtoEscolhido) {
        repositorio.remove(produtoEscolhido, new ProdutoRepositorio.DadosCarregadosCallBack<Void>() {
            @Override
            public void quandoSucesso(Void resultado) {

                adapter.remove(posicao);
            }

            @Override
            public void quandoFalha(String erro) {

                mostrarErro(MENSAGEM_ERRO_REMOVER);
            }
        });
    }
}
