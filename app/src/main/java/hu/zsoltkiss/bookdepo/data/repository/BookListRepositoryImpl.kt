package hu.zsoltkiss.bookdepo.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.zsoltkiss.bookdepo.data.DepoItem
import hu.zsoltkiss.bookdepo.data.dao.BookDao
import hu.zsoltkiss.bookdepo.server.BookDepoImpl
import hu.zsoltkiss.bookdepo.server.messages.request.BookListRequest
import hu.zsoltkiss.bookdepo.server.messages.request.ServerRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val tick: Long = 5000L
const val negyedPerc = 3 * tick
const val felPerc = 6 * tick
const val egyPerc = 12 * tick
const val ketPerc = 24 * tick

@RequiresApi(Build.VERSION_CODES.O)
class BookListRepositoryImpl(
    private val currentUserId: Long,
    private val bookDao: BookDao
): BookListRepository {

    private val job = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + job)

    init {
        repositoryScope.launch {
            refresh()
        }
    }

    private suspend fun refresh() {
        while (true) {
            val response = BookDepoImpl.getInstance().bookList(BookListRequest(userId = currentUserId))

            val wrapperListType = object: TypeToken<List<DepoItem>>() {

            }.type

            val items: List<DepoItem> = Gson().fromJson(response.content, wrapperListType)

            items.forEach { depoItem ->
                val bookSnapshot = depoItem.toBook(currentUserId)
                bookDao.findById(depoItem.id)?.let { bookInDb ->
                    val updatedBook = bookInDb.copy(status = bookSnapshot.status)
                    bookDao.updateBook(updatedBook)
                } ?: bookDao.insertAll(bookSnapshot)
            }

            delay(negyedPerc)
        }
    }

    override fun sendServerRequest(request: ServerRequest) {
        println("7777 REPO ::sendServerRequest >> $request")
        repositoryScope.launch {
            BookDepoImpl.getInstance().processRequest(request)
        }
    }

    override fun onClose() {
        repositoryScope.cancel()
    }

}