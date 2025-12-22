package br.med.televida.pocagoraio.domain

/**
 * Representa os diferentes estados possíveis de uma videochamada.
 * A UI reage a estes estados para mostrar a tela correta (loading, chamada ativa, etc.).
 */
sealed class CallState {
    /**
     * Estado inicial, antes de qualquer ação. A chamada está inativa.
     */
    object Inactive : CallState() // CORRIGIDO: Nomeclatura e erro de digitação

    /**
     * O motor do Agora está sendo inicializado.
     */
    object Initializing : CallState()

    /**
     * Tentando se conectar a um canal. A UI deve mostrar um indicador de carregamento.
     */
    object Connecting : CallState()

    /**
     * Conectado com sucesso a um canal. A chamada está ativa.
     */
    object Connected : CallState()

    /**
     * A conexão foi perdida e o SDK está tentando se reconectar automaticamente.
     */
    object Reconnecting : CallState()

    /**
     * A chamada foi finalizada.
     */
    object Ended : CallState()
}
