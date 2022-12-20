package br.com.alura.estoque.retrofit.callback;

import static br.com.alura.estoque.retrofit.callback.MensagemCallBack.MENSAGEM_ERRO_FALHA_DE_COMUNICACAO;
import static br.com.alura.estoque.retrofit.callback.MensagemCallBack.MENSAGEM_ERRO_RESPOSTA_NAO_SUCEDIDA;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallbackSemRetorno implements Callback<Void> {



    private final RespostaCallBack callBack;

    public CallbackSemRetorno(RespostaCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

        if (response.isSuccessful()) {

            callBack.quandoSucesso();

        } else {

            callBack.quandoFalha(MENSAGEM_ERRO_RESPOSTA_NAO_SUCEDIDA);

        }
    }

    @Override
    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

        callBack.quandoFalha(MENSAGEM_ERRO_FALHA_DE_COMUNICACAO + t.getMessage());
    }


    public interface RespostaCallBack {

        void quandoSucesso();

        void quandoFalha(String erro);
    }
}
