package hu.zsoltkiss.bookdepo.server

import hu.zsoltkiss.bookdepo.server.messages.request.BookListRequest
import hu.zsoltkiss.bookdepo.server.messages.request.ServerRequest
import hu.zsoltkiss.bookdepo.server.messages.response.BookListResponse

interface BookDepo {
    fun start()
    suspend fun bookList(request: BookListRequest): BookListResponse
    suspend fun processRequest(req: ServerRequest)
    fun onClose()
}