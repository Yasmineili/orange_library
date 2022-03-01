package com.andrea.orangelibrary

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete

@Dao
interface BookDao {
    @Query("SELECT * FROM book_table ORDER BY title ASC")
    fun getAll(): LiveData<List<BookEntity>>

    @Query("SELECT * FROM book_table WHERE id = :id")
    fun getById(id: Int): LiveData<BookEntity>

    @Query("SELECT * FROM book_table WHERE title LIKE '%' || :title || '%'")
    fun getByTitle(title: String): LiveData<List<BookEntity>>

    @Query("SELECT * FROM book_table WHERE author LIKE '%' || :author || '%'")
    fun getByAuthor(author: String): LiveData<List<BookEntity>>

    @Query("SELECT * FROM book_table WHERE editorial LIKE '%' || :editorial || '%'")
    fun getByEditorial(editorial: String): LiveData<List<BookEntity>>

    @Query("SELECT * FROM book_table WHERE CAST(year AS VARCHAR(9)) LIKE '%' || :year || '%'")
    fun getByYear(year: String): LiveData<List<BookEntity>>

    @Insert//(onConflict = OnConflictStrategy.REPLACE)
    fun insertNew(vararg contact: BookEntity):List<Long>

    @Update
    fun update(contact: BookEntity)

    @Delete
    fun delete(contact: BookEntity)
}
