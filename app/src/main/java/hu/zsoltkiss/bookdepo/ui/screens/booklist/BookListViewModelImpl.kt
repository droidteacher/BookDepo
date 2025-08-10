package hu.zsoltkiss.bookdepo.ui.screens.booklist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hu.zsoltkiss.bookdepo.data.Book
import hu.zsoltkiss.bookdepo.data.UserAction
import hu.zsoltkiss.bookdepo.data.dao.BookDao
import hu.zsoltkiss.bookdepo.data.repository.BookListRepository
import hu.zsoltkiss.bookdepo.server.BookDepoImpl
import hu.zsoltkiss.bookdepo.server.messages.request.BorrowRequest
import hu.zsoltkiss.bookdepo.server.messages.request.ClaimRequest
import hu.zsoltkiss.bookdepo.server.messages.request.ReturnRequest
import hu.zsoltkiss.bookdepo.server.messages.request.ServerRequest
import hu.zsoltkiss.bookdepo.util.IDProvider
import hu.zsoltkiss.bookdepo.util.IDProviderImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookListViewModelImpl(private val repo: BookListRepository, private val dao: BookDao): ViewModel(), BookListViewModel {

    private val idProvider: IDProvider = IDProviderImpl()

    private val _books = MutableStateFlow(emptyList<Book>())
    override val books: StateFlow<List<Book>> = _books

    private val _startButtonEnabled = MutableStateFlow(false)
    override val startButtonEnabled: StateFlow<Boolean> = _startButtonEnabled


    private val bookTitleComparator = object: Comparator<Book> {
        override fun compare(
            p0: Book,
            p1: Book
        ): Int {
            return p0.title.compareTo(p1.title)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClickStart() {
        BookDepoImpl.getInstance().start()
        _startButtonEnabled.value = false
    }

    override fun onUserAction(type: UserAction, bookId: Long) {
        val request: ServerRequest = when (type) {
            UserAction.Borrow -> BorrowRequest(userId = idProvider.currentUserId, bookId = bookId)
            UserAction.Claim -> ClaimRequest(userId = idProvider.currentUserId, bookId = bookId)
            UserAction.Return -> ReturnRequest(userId = idProvider.currentUserId, bookId = bookId)
        }

        repo.sendServerRequest(request)
    }

    init {
        viewModelScope.launch {
//            dao.numberOfBooks().also { num ->
//                _startButtonEnabled.value = num == 0
//            }

            dao.deleteAll()
            _startButtonEnabled.value = true

            dao.getAll().collect {
                _books.value = it.sortedWith(bookTitleComparator)
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        repo.onClose()
    }

}