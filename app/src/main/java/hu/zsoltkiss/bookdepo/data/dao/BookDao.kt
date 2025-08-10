package hu.zsoltkiss.bookdepo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import hu.zsoltkiss.bookdepo.data.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM book")
    fun getAll(): Flow<List<Book>>

    @Query("SELECT count(*) FROM book")
    suspend fun numberOfBooks(): Int

    @Insert
    suspend fun insertAll(vararg books: Book)

    @Query("SELECT * FROM book WHERE id = :bookId")
    suspend fun findById(bookId: Long): Book?

    @Query("DELETE FROM book")
    suspend fun deleteAll()

    @Update
    suspend fun updateBook(book: Book)
}