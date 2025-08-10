package hu.zsoltkiss.bookdepo.server.messages.request

import hu.zsoltkiss.konyvkolcsonzo.server.messages.Endpoint

data class ReturnRequest(
    val userId: Long,
    val bookId: Long,
) : ServerRequest {
    override val endpoint: Endpoint
        get() = Endpoint.Return
}