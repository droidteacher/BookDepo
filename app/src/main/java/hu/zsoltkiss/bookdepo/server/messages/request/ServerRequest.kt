package hu.zsoltkiss.bookdepo.server.messages.request

import hu.zsoltkiss.konyvkolcsonzo.server.messages.Endpoint

interface ServerRequest {
    val endpoint: Endpoint
}