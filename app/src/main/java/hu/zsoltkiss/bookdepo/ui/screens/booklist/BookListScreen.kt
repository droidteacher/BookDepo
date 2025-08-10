package hu.zsoltkiss.bookdepo.ui.screens.booklist

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.zsoltkiss.bookdepo.data.Book
import hu.zsoltkiss.bookdepo.data.BookStatus
import hu.zsoltkiss.bookdepo.data.UserAction
import java.nio.file.WatchEvent

@Composable
fun BookListScreen(
    modifier: Modifier = Modifier,
    viewModel: BookListViewModelImpl
) {
    val books by viewModel.books.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(books) { aBook ->
            BookCard(book = aBook, action = viewModel::onUserAction)
        }
    }
}

@Composable
private fun BookCard(
    book: Book,
    action: (UserAction, Long) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = book.status.cardColor)
    ) {

        Row(modifier = Modifier.fillMaxWidth(0.9f).padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            Text("ID: ${book.id}", style = TextStyle(fontWeight = FontWeight.Bold))
        }

        Text(
            text = book.title,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = book.author,
            modifier = Modifier
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )

        ButtonBar(
            status = book.status,
            bookId = book.id,
            actionReturn = {
                action(UserAction.Return, it)
            },
            actionClaim = {
                action(UserAction.Claim, it)
            },
            actionBorrow = {
                action(UserAction.Borrow, it)
            },
        )
    }
}

@Composable
private fun ButtonBar(
    status: BookStatus,
    bookId: Long,
    actionReturn: (Long) -> Unit,
    actionClaim: (Long) -> Unit,
    actionBorrow: (Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TextButton(onClick = {
            actionReturn(bookId)
        }, enabled = status == BookStatus.YouHaveIt) {
            Text("Return")
        }
        TextButton(onClick = {
            actionClaim(bookId)
        }, enabled = status == BookStatus.NotAvailable) {
            Text("Claim")
        }
        TextButton(onClick = {
            actionBorrow(bookId)
        }, enabled = status == BookStatus.Available) {
            Text("Borrow")
        }
    }
}