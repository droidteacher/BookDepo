package hu.zsoltkiss.bookdepo.server.messages.request

import hu.zsoltkiss.bookdepo.server.messages.Endpoint

data class ClaimRequest(
    val userId: Long,
    val bookId: Long,
) : ServerRequest {
    override val endpoint: Endpoint
        get() = Endpoint.Claim

}