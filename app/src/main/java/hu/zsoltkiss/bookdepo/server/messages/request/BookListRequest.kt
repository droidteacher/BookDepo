package hu.zsoltkiss.bookdepo.server.messages.request

import hu.zsoltkiss.konyvkolcsonzo.server.messages.Endpoint

data class BookListRequest(
    val userId: Long
) : ServerRequest {
    override val endpoint: Endpoint
        get() = Endpoint.BookList

}
