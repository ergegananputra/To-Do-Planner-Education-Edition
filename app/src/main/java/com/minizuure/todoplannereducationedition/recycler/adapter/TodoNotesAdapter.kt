package com.minizuure.todoplannereducationedition.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.databinding.ItemDetailBinding
import com.minizuure.todoplannereducationedition.services.database.notes.TodoNoteTable

class TodoNotesAdapter(
    var onClickCheckBoxAction : (Long, Boolean) -> Unit,
    var onClickDeleteAction : (Long) -> Unit,
) : ListAdapter<TodoNoteTable, TodoNotesAdapter.TodoNoteViewHolder>(TodoNoteDiffUtils()) {
    class TodoNoteDiffUtils : DiffUtil.ItemCallback<TodoNoteTable>() {
        override fun areItemsTheSame(oldItem: TodoNoteTable, newItem: TodoNoteTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoNoteTable, newItem: TodoNoteTable): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    inner class TodoNoteViewHolder(
        private val binding : ItemDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todoNoteTable: TodoNoteTable) {
            binding.checkboxItemDetail.apply {
                isChecked = todoNoteTable.isChecked
                text = todoNoteTable.description
            }

            binding.checkboxItemDetail.setOnClickListener {
                onClickCheckBoxAction(todoNoteTable.id, binding.checkboxItemDetail.isChecked)
            }

            binding.cardItemDetail.setOnLongClickListener {
                longClickAction()

            }

            binding.checkboxItemDetail.setOnLongClickListener {
                longClickAction()
            }

            binding.buttonDeleteItemDetail.setOnClickListener {
                onClickDeleteAction(todoNoteTable.id)
            }

        }

        private fun longClickAction() : Boolean {
            binding.groupDeleteItemDetail.visibility = if (
                binding.groupDeleteItemDetail.visibility == ViewGroup.VISIBLE) {
                ViewGroup.GONE
            } else {
                ViewGroup.VISIBLE
            }
            return true
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoNoteViewHolder {
        val binding = ItemDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TodoNoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoNoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}