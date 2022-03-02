package com.andrea.orangelibrary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "book_table")
data class BookEntity(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "editorial") val editorial: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "price") val price: Double = 100.00,
    @ColumnInfo(name = "category") val category: Category,
    @ColumnInfo(name = "image") val image: Int,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0
):Serializable

enum class Category() {
    Novela,
    Terror,
    Educativo,
    Ficción,
    Infantil
}
