package hu.zsoltkiss.bookdepo.server

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import hu.zsoltkiss.bookdepo.data.DepoItem
import hu.zsoltkiss.bookdepo.data.repository.egyPerc
import hu.zsoltkiss.bookdepo.data.repository.felPerc
import hu.zsoltkiss.bookdepo.data.repository.negyedPerc
import hu.zsoltkiss.bookdepo.data.repository.tick
import hu.zsoltkiss.bookdepo.server.messages.request.BookListRequest
import hu.zsoltkiss.bookdepo.server.messages.request.BorrowRequest
import hu.zsoltkiss.bookdepo.server.messages.request.ClaimRequest
import hu.zsoltkiss.bookdepo.server.messages.request.ReturnRequest
import hu.zsoltkiss.bookdepo.server.messages.request.ServerRequest
import hu.zsoltkiss.bookdepo.server.messages.response.BookListResponse
import hu.zsoltkiss.bookdepo.util.IDProvider
import hu.zsoltkiss.bookdepo.util.IDProviderImpl
import hu.zsoltkiss.konyvkolcsonzo.server.messages.Endpoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.Date
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class BookDepoImpl private constructor() : BookDepo {

    companion object {
        @Volatile
        private var instance: BookDepoImpl? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: BookDepoImpl().also { instance = it }
            }
    }

    private val idProvider: IDProvider = IDProviderImpl()
    private val job = SupervisorJob()
    private val depoScope = CoroutineScope(Dispatchers.IO + job)

    private val myScope = CoroutineScope(Dispatchers.IO)

    override fun start() {

        myScope.launch {
            launch { replenishment() }
            launch { generateBorrows() }
            launch { automaticReturns() }
            launch { checkClaims() }
        }
    }

    private val storage = ArrayDeque(
        listOf(
            DepoItem(
                id = 7514,
                title = "Thinking in Java",
                author = "Bruce Eckel"
            ),

            DepoItem(
                id = 7529,
                title = "Atomic Kotlin",
                author = "Bruce Eckel, Svetlana Isakova"
            ),

            DepoItem(
                id = 7509,
                title = "Java Cookbook",
                author = "Ian F. Darwin"
            ),

            DepoItem(
                id = 7522,
                title = "Head Rush Ajax",
                author = "Brett McLaughlin"
            ),

            DepoItem(
                id = 7356,
                title = "Surviving off off-grid",
                author = "Michael Bunker"
            ),

            DepoItem(
                id = 7300,
                title = "The Encyclopedia of Country Living",
                author = "Carla Emery"
            ),

            DepoItem(
                id = 7412,
                title = "Arduino for Dummies",
                author = "John Nussey"
            ),


            DepoItem(
                id = 7009,
                title = "Pennsylvania 1",
                author = "Michael Bunker"
            ),

            DepoItem(
                id = 7010,
                title = "Pennsylvania 2",
                author = "Michael Bunker"
            ),

            )
    )

    private val availableItems = mutableListOf<DepoItem>()

    override suspend fun bookList(request: BookListRequest): BookListResponse {
        println("7777 DEPO ::bookList")
        return BookListResponse(
            type = Endpoint.BookList,
            timestamp = Date().time,
            content = Gson().toJson(availableItems)
        )
    }

    override suspend fun processRequest(req: ServerRequest) {
        when (req) {
            is BorrowRequest -> processBorrowRequest(req)
            is ClaimRequest -> processClaimRequest(req)
            is ReturnRequest -> processReturnRequest(req)
        }
    }

    private fun processClaimRequest(request: ClaimRequest) {
        availableItems.firstOrNull { it.id == request.bookId }?.let { item ->
            if (item.claimedBy == null) {
                item.claimedBy = request.userId

                println("7777 DEPO ::claim, userId: ${request.userId}, bookId: ${request.bookId}")
            }
        }
    }

    private fun processBorrowRequest(request: BorrowRequest) {
        availableItems.firstOrNull { it.id == request.bookId }?.let { item ->
            if (item.claimedBy == null && item.userId == null && item.borrowTime == null) {
                item.borrowTime = ZonedDateTime.now()
                item.userId = request.userId

                println("7777 DEPO ::borrow, userId: ${request.userId}, bookId: ${request.bookId}")
            }
        }
    }

    private fun processReturnRequest(request: ReturnRequest) {
        availableItems.firstOrNull { it.id == request.bookId && it.userId == request.userId }
            ?.let { item ->
                item.borrowTime = null
                item.userId = null

                println("7777 DEPO ::return, userId: ${request.userId}, bookId: ${request.bookId}")
            }
    }

    override fun onClose() {
        println("7777 DEPO ::onClose")
        job.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun generateBorrows() {
        while (true) {

            delay(felPerc)
            if (Random.nextBoolean()) {
                val availableBooks =
                    availableItems.filter { it.borrowTime == null && it.claimedBy == null }
                if (availableBooks.isNotEmpty()) {
                    val idx = Random.nextInt(0, availableBooks.size)
                    val userId = idProvider.randomUserId()
                    availableBooks[idx].also { selectedItem ->
                        selectedItem.borrowTime = ZonedDateTime.now()
                        selectedItem.userId = userId

                        println("7777 DEPO ::generateBorrows >> ${selectedItem.title} borrowed by $userId")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun automaticReturns() {
        while (true) {

            delay(negyedPerc)
            val expiredBooks =
                availableItems.filter { it.borrowTime != null && it.userId != idProvider.currentUserId }
                    .filter {
                        it.borrowTime!!.plusMinutes(2).isBefore(
                            ZonedDateTime.now()
                        )
                    }
            expiredBooks.forEach {
                // visszavetel
                println("7777 DEPO ::automaticReturns >> title: ${it.title}, user: ${it.userId}")

                if (it.claimedBy != null) {
                    it.userId = it.claimedBy
                    it.borrowTime = ZonedDateTime.now()
                    it.claimedBy = null

                } else {
                    it.borrowTime = null
                    it.userId = null
                }

            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun checkClaims() {
        while (true) {
            println("7777 DEPO ::checkClaims")
            delay(egyPerc)
            availableItems.filter { it.claimedBy != null && it.borrowTime == null }
                .forEach { item ->
                    item.borrowTime = ZonedDateTime.now()
                    item.userId = item.claimedBy
                    item.claimedBy = null

                }
        }

    }

    private suspend fun replenishment() {
        while (storage.isNotEmpty()) {
            println("7777 DEPO ::replenishment")
            delay(tick)
            val someItem = storage.removeFirst()
            availableItems.add(someItem)
        }
    }

}