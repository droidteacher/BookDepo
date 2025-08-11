package hu.zsoltkiss.bookdepo.server.messages.response

import hu.zsoltkiss.bookdepo.server.messages.Endpoint

data class BookListResponse(
    val type: Endpoint,
    val timestamp: Long,
    val content: String
)

