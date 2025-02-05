package com.example.crudexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.crudexample.databinding.ItemAuthorBinding
import com.example.crudexample.db.Author

class AuthorAdapter(
    private val onEditClick: (Author) -> Unit,
    private val onDeleteClick: (Author) -> Unit
) : ListAdapter<Author, AuthorAdapter.AuthorViewHolder>(AuthorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorViewHolder {
        val binding = ItemAuthorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AuthorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AuthorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AuthorViewHolder(
        private val binding: ItemAuthorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(author: Author) {
            binding.authorNameText.text = author.name
            binding.authorEmailText.text = author.email
            binding.editAuthorButton.setOnClickListener { onEditClick(author) }
            binding.deleteAuthorButton.setOnClickListener { onDeleteClick(author) }
        }
    }

    private class AuthorDiffCallback : DiffUtil.ItemCallback<Author>() {
        override fun areItemsTheSame(oldItem: Author, newItem: Author): Boolean {
            return oldItem.authorId == newItem.authorId
        }

        override fun areContentsTheSame(oldItem: Author, newItem: Author): Boolean {
            return oldItem == newItem
        }
    }
}