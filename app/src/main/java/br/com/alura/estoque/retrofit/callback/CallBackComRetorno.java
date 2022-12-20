package br.com.alura.estoque.retrofit.callback;

import static br.com.alura.estoque.retrofit.callback.MensagemCallBack.MENSAGEM_ERRO_FALHA_DE_COMUNICACAO;
import static br.com.alura.estoque.retrofit.callback.MensagemCallBack.MENSAGEM_ERRO_RESPOSTA_NAO_SUCEDIDA;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallBackComRetorno<T> implements Callback<T> {


    private final RespostaCallBack<T> callBack;

    public CallBackComRetorno(RespostaCallBack<T> callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {


        if (response.isSuccessful()) {

            T resultado = response.body();

            if (resultado != null) {

                callBack.quandoSucesso(resultado);


            }

        } else {
            callBack.quandoFalha(MENSAGEM_ERRO_RESPOSTA_NAO_SUCEDIDA);

        }

    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {

        callBack.quandoFalha(MENSAGEM_ERRO_FALHA_DE_COMUNICACAO + t.getMessage());

    }

    public interface RespostaCallBack<T> {

        void quandoSucesso(T resultado);

        void quandoFalha(String erro);
    }
}
