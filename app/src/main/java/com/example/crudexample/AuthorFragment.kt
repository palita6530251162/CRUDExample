package com.example.crudexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudexample.adapter.AuthorAdapter
import com.example.crudexample.databinding.FragmentAuthorBinding
import com.example.crudexample.db.Author
import com.example.crudexample.db.LibraryDatabase
import com.example.crudexample.db.LibraryRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AuthorFragment : Fragment() {

    private lateinit var binding: FragmentAuthorBinding
    private lateinit var authorAdapter: AuthorAdapter
    private lateinit var repository: LibraryRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAuthorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = LibraryDatabase.getDatabase(requireContext())
        repository = LibraryRepository(database)

        setupRecyclerView()
        setupClickListeners()
        loadAuthors()
    }

    private fun setupRecyclerView() {
        authorAdapter = AuthorAdapter(
            onEditClick = { author -> showEditAuthorDialog(author) },
            onDeleteClick = { author -> deleteAuthor(author) }
        )
        binding.authorsRecyclerView.apply {
            adapter = authorAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupClickListeners() {
        binding.addAuthorButton.setOnClickListener {
            val name = binding.authorNameInput.text.toString()
            val email = binding.authorEmailInput.text.toString()

            if (name.isNotBlank() && email.isNotBlank()) {
                addAuthor(name, email)
                clearInputs()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearInputs() {
        binding.authorNameInput.text?.clear()
        binding.authorEmailInput.text?.clear()
    }

    private fun addAuthor(name: String, email: String) {
        lifecycleScope.launch {
            try {
                repository.insertAuthor(name, email)
                loadAuthors()
            } catch (e: Exception) {
                Toast.makeText(context, "Error adding author", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAuthor(author: Author) {
        lifecycleScope.launch {
            try {
                repository.deleteAuthor(author)
                loadAuthors()
            } catch (e: Exception) {
                Toast.makeText(context, "Error deleting author", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAuthors() {
        lifecycleScope.launch {
            try {
                val authors = repository.getAllAuthors()
                authorAdapter.submitList(authors)
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading authors", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditAuthorDialog(author: Author) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Author")
            .setView(R.layout.dialog_edit_author)
            .setPositiveButton("Save") { dialog, _ -> }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        val nameInput = dialog.findViewById<TextInputEditText>(R.id.editAuthorNameInput)
        val emailInput = dialog.findViewById<TextInputEditText>(R.id.editAuthorEmailInput)

        nameInput?.setText(author.name)
        emailInput?.setText(author.email)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newName = nameInput?.text.toString()
            val newEmail = emailInput?.text.toString()

            if (newName.isNotBlank() && newEmail.isNotBlank()) {
                updateAuthor(author.copy(name = newName, email = newEmail))
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAuthor(author: Author) {
        lifecycleScope.launch {
            try {
                repository.updateAuthor(author)
                loadAuthors()
            } catch (e: Exception) {
                Toast.makeText(context, "Error updating author", Toast.LENGTH_SHORT).show()
            }
        }
    }


}