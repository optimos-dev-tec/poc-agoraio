package br.med.televida.pocagoraio.domain

/**
 * Representa eventos pontuais que ocorrem durante uma chamada.
 * Estes eventos são usados para notificar a UI sobre acontecimentos
 * que não representam uma mudança de estado permanente.
 */
sealed class CallEvent {
    /**
     * Evento disparado quando o usuário local entra no canal com sucesso.
     */
    object LocalJoined : CallEvent()

    /**
     * Evento disparado quando um usuário remoto entra no canal.
     * @param uid O ID do usuário remoto.
     */
    data class RemoteUserJoined(val uid: Int) : CallEvent()

    /**
     * Evento disparado quando um usuário remoto sai do canal.
     * @param uid O ID do usuário que saiu.
     */
    data class RemoteUserLeft(val uid: Int) : CallEvent()

    /**
     * Evento disparado quando ocorre um erro no SDK do Agora.
     * @param message A mensagem de erro.
     */
    data class Error(val message: String) : CallEvent() // <-- NOVO E IMPORTANTE

    /**
     * Evento disparado quando o token de acesso expira.
     * A UI deve solicitar um novo token e reconectar.
     */
    object TokenExpired : CallEvent()
}
