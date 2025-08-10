package hu.zsoltkiss.bookdepo.data

import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class DepoItem(
    val id: Long,
    val author: String,
    val title: String,
    var borrowTime: ZonedDateTime? = null,
    var userId: Long? = null,
    var claimedBy: Long? = null

) {

    fun toBook(vipUid: Long?): Book =
        Book(
            id = id,
            author = author,
            title = title,
            status = if (userId == vipUid) {
                BookStatus.YouHaveIt
            } else if (borrowTime != null) {
                BookStatus.NotAvailable
            } else {
                BookStatus.Available
            }
        )


}
