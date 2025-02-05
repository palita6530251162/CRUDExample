package com.example.crudexample.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface AuthorDao {
    @Insert
    suspend fun insertAuthor(author: Author): Long

    @Query("SELECT * FROM authors")
    suspend fun getAllAuthors(): List<Author>

    @Query("SELECT * FROM authors WHERE authorId = :id")
    suspend fun getAuthorById(id: Long): Author?

    @Update
    suspend fun updateAuthor(author: Author)

    @Delete
    suspend fun deleteAuthor(author: Author)
}

@Dao
interface BookDao {
    @Insert
    suspend fun insertBook(book: Book): Long

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book>

    @Query("SELECT * FROM books WHERE authorId = :authorId")
    suspend fun getBooksByAuthor(authorId: Long): List<Book>

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
}

@Dao
interface AuthorWithBooksDao {
    @Transaction
    @Query("SELECT * FROM authors")
    suspend fun getAuthorsWithBooks(): List<AuthorWithBooks>

    @Transaction
    @Query("SELECT * FROM authors WHERE authorId = :authorId")
    suspend fun getAuthorWithBooks(authorId: Long): AuthorWithBooks?
}