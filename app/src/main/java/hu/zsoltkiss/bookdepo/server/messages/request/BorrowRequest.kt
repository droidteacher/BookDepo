package hu.zsoltkiss.bookdepo.server.messages.request

import hu.zsoltkiss.bookdepo.server.messages.Endpoint

data class BorrowRequest(
    val userId: Long,
    val bookId: Long,
) : ServerRequest {
    override val endpoint: Endpoint
        get() = Endpoint.Borrow

}