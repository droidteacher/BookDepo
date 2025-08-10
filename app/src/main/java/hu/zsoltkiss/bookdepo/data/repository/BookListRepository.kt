package hu.zsoltkiss.bookdepo.data.repository

import hu.zsoltkiss.bookdepo.server.messages.request.ServerRequest

interface BookListRepository {
    fun onClose()
    fun sendServerRequest(request: ServerRequest)
}