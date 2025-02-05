package com.example.crudexample.db

class LibraryRepository(private val database: LibraryDatabase) {
    // Author operations
    suspend fun insertAuthor(name: String, email: String): Long {
        val author = Author(name = name, email = email)
        return database.authorDao().insertAuthor(author)
    }

    suspend fun updateAuthor(author: Author) {
        database.authorDao().updateAuthor(author)
    }

    suspend fun deleteAuthor(author: Author) {
        database.authorDao().deleteAuthor(author)
    }

    suspend fun getAllAuthors(): List<Author> {
        return database.authorDao().getAllAuthors()
    }

    // Book operations
    suspend fun insertBook(title: String, publishYear: Int, authorId: Long): Long {
        val book = Book(title = title, publishYear = publishYear, authorId = authorId)
        return database.bookDao().insertBook(book)
    }

    suspend fun updateBook(book: Book) {
        database.bookDao().updateBook(book)
    }

    suspend fun deleteBook(book: Book) {
        database.bookDao().deleteBook(book)
    }

    suspend fun getBooksByAuthor(authorId: Long): List<Book> {
        return database.bookDao().getBooksByAuthor(authorId)
    }

    // Combined operations
    suspend fun getAuthorWithBooks(authorId: Long): AuthorWithBooks? {
        return database.authorWithBooksDao().getAuthorWithBooks(authorId)
    }

    suspend fun getAllAuthorsWithBooks(): List<AuthorWithBooks> {
        return database.authorWithBooksDao().getAuthorsWithBooks()
    }
}