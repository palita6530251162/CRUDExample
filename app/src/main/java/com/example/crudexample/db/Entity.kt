package com.example.crudexample.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.ForeignKey

@Entity(tableName = "authors")
data class Author(
    @PrimaryKey(autoGenerate = true)
    val authorId: Long = 0,
    val name: String,
    val email: String
)

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = Author::class,
            parentColumns = ["authorId"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )]
)

data class Book(
    @PrimaryKey(autoGenerate = true)
    val bookId: Long = 0,
    val title: String,
    val publishYear: Int,
    val isbn: String,
    val purchaseDate: String,
    val notes: String?,
    val authorId: Long,

    )

data class AuthorWithBooks(
    @Embedded val author: Author,
    @Relation(
        parentColumn = "authorId",
        entityColumn = "authorId"
    )
    val books: List<Book>
)