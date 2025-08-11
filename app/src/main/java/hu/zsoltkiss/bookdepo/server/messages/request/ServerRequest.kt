package hu.zsoltkiss.bookdepo.server.messages.request

import hu.zsoltkiss.bookdepo.server.messages.Endpoint

interface ServerRequest {
    val endpoint: Endpoint
}