package hu.zsoltkiss.bookdepo.ui.screens.booklist

import hu.zsoltkiss.bookdepo.data.Book
import hu.zsoltkiss.bookdepo.data.UserAction
import kotlinx.coroutines.flow.StateFlow

interface BookListViewModel {
    val books: StateFlow<List<Book>>
    val startButtonEnabled: StateFlow<Boolean>

    fun onClickStart()
    fun onUserAction(type: UserAction, bookId: Long)
}