package hu.zsoltkiss.bookdepo.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.zsoltkiss.bookdepo.ui.theme.CardBgAvailable
import hu.zsoltkiss.bookdepo.ui.theme.CardBgNotAvailable
import hu.zsoltkiss.bookdepo.ui.theme.CardBgYouHaveIt

@Entity
data class Book(
    @PrimaryKey val id: Long,
    val author: String,
    val title: String,
    val status: BookStatus
)


enum class BookStatus {
    Available, NotAvailable, YouHaveIt;

    val cardColor: Color
        get() = when (this) {
            Available -> CardBgAvailable
            NotAvailable -> CardBgNotAvailable
            YouHaveIt -> CardBgYouHaveIt
        }
}
