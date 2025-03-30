package com.example.crudexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudexample.adapter.BookAdapter
import com.example.crudexample.databinding.DialogEditBookBinding
import com.example.crudexample.databinding.FragmentBookBinding
import com.example.crudexample.db.Author
import com.example.crudexample.db.Book
import com.example.crudexample.db.LibraryDatabase
import com.example.crudexample.db.LibraryRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


class BookFragment : Fragment() {

    private lateinit var binding: FragmentBookBinding
    private lateinit var bookAdapter: BookAdapter
    private lateinit var repository: LibraryRepository
    private var authors: List<Author> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = LibraryDatabase.getDatabase(requireContext())
        repository = LibraryRepository(database)

        setupRecyclerView()
        loadAuthors()
        setupSpinner()
        setupClickListeners()
        loadBooks()
    }

    // Add this function to refresh author data
    fun refreshAuthorData() {
        lifecycleScope.launch {
            try {
                // Reload authors for spinner
                val authors = repository.getAllAuthors()
                val authorAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    authors.map { it.name }
                )
                authorAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
                binding.authorSpinner.adapter = authorAdapter

                // Also refresh books to get updated author information
                loadBooks()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error refreshing data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Add this function to handle visibility changes
    override fun onResume() {
        super.onResume()
        // Refresh when fragment becomes visible
        refreshAuthorData()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onEditClick = { book -> showEditBookDialog(book) },
            onDeleteClick = { book -> deleteBook(book) }
        )
        binding.booksRecyclerView.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSpinner() {
        lifecycleScope.launch {
            authors = repository.getAllAuthors()
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                authors.map { it.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.authorSpinner.adapter = adapter
        }
    }

    private fun setupClickListeners() {
        binding.addBookButton.setOnClickListener {
            val title = binding.bookTitleInput.text.toString()
            val yearStr = binding.publishYearInput.text.toString()
            val isbn = binding.bookIsbnInput.text.toString()
            val purchaseDate = binding.bookPurchaseDateInput.text.toString()
            val notes = binding.bookNotesInput.text.toString()
            val authorPosition = binding.authorSpinner.selectedItemPosition

            if (title.isNotBlank() && yearStr.isNotBlank() && isbn.isNotBlank()
                && purchaseDate.isNotBlank() && authorPosition != -1
            ) {
                val year = yearStr.toIntOrNull()
                if (year != null) {
                    val authorId = authors[authorPosition].authorId
                    addBook(title, year, isbn, purchaseDate, notes, authorId)
                    clearInputs()
                } else {
                    Toast.makeText(context, "Invalid year", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun clearInputs() {
        binding.bookTitleInput.text?.clear()
        binding.publishYearInput.text?.clear()
        binding.bookIsbnInput.text?.clear()
        binding.bookPurchaseDateInput.text?.clear()
        binding.bookNotesInput.text?.clear()
    }


    private fun addBook(
        title: String,
        year: Int,
        isbn: String,
        purchaseDate: String,
        notes: String?,
        authorId: Long
    ) {
        lifecycleScope.launch {
            try {
                repository.insertBook(title, year, isbn, purchaseDate, notes, authorId)
                loadBooks()
            } catch (e: Exception) {
                Toast.makeText(context, "Error adding book", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun deleteBook(book: Book) {
        lifecycleScope.launch {
            try {
                repository.deleteBook(book)
                loadBooks()
            } catch (e: Exception) {
                Toast.makeText(context, "Error deleting book", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBooks() {
        lifecycleScope.launch {
            try {
                val authorWithBooks = repository.getAllAuthorsWithBooks()
                val allBooks = authorWithBooks.flatMap { it.books }
                bookAdapter.submitList(allBooks)
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading books", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAuthors() {
        lifecycleScope.launch {
            try {
                authors = repository.getAllAuthors()
                setupSpinner()
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading authors", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditBookDialog(book: Book) {
        // Inflate the dialog layout
        val dialogBinding = DialogEditBookBinding.inflate(layoutInflater)

        // Pre-fill the current book data
        dialogBinding.editBookTitleInput.setText(book.title)
        dialogBinding.editPublishYearInput.setText(book.publishYear.toString())

        // Setup author spinner
        lifecycleScope.launch {
            val authors = repository.getAllAuthors()
            val authorAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                authors.map { it.name }
            )

            dialogBinding.editAuthorSpinner.setAdapter(authorAdapter)

            // Find and set the current author
            val currentAuthorIndex = authors.indexOfFirst { it.authorId == book.authorId }
            if (currentAuthorIndex != -1) {
                dialogBinding.editAuthorSpinner.setText(authors[currentAuthorIndex].name, false)
            }
        }

        // Create and show the dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Book")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { dialog, _ ->
                val newTitle = dialogBinding.editBookTitleInput.text.toString()
                val newYearStr = dialogBinding.editPublishYearInput.text.toString()
                val selectedAuthorName = dialogBinding.editAuthorSpinner.text.toString()

                lifecycleScope.launch {
                    try {
                        // Validate inputs
                        if (newTitle.isBlank() || newYearStr.isBlank() || selectedAuthorName.isBlank()) {
                            throw IllegalArgumentException("Please fill all fields")
                        }

                        val newYear = newYearStr.toIntOrNull()
                            ?: throw IllegalArgumentException("Invalid year")

                        if (newYear < 1000 || newYear > 9999) {
                            throw IllegalArgumentException("Year must be between 1000 and 9999")
                        }

                        // Find selected author
                        val authors = repository.getAllAuthors()
                        val selectedAuthor = authors.find { it.name == selectedAuthorName }
                            ?: throw IllegalArgumentException("Please select a valid author")

                        // Update book
                        val updatedBook = book.copy(
                            title = newTitle,
                            publishYear = newYear,
                            authorId = selectedAuthor.authorId
                        )

                        repository.updateBook(updatedBook)
                        Toast.makeText(requireContext(), "Book updated", Toast.LENGTH_SHORT).show()

                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}