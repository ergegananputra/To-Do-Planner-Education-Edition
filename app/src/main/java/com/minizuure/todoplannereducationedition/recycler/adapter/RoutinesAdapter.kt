package com.minizuure.todoplannereducationedition.recycler.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.databinding.ItemRoutinesBinding
import com.minizuure.todoplannereducationedition.recycler.model.RoutinesItemPreview

class RoutinesAdapter(
    var routines : MutableList<RoutinesItemPreview>,
    private val onClick : (RoutinesItemPreview) -> Unit,
    private val onClickDelete : (RoutinesItemPreview) -> Unit,
) : ListAdapter<RoutinesItemPreview, RoutinesAdapter.RoutinesViewHolder>(RoutinesItemPreviewDiffUtil()) {
    inner class RoutinesViewHolder(
        private val binding : ItemRoutinesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routinesItemPreview: RoutinesItemPreview) {
            binding.textViewItemTitleRoutine.text = routinesItemPreview.title
            binding.textViewItemRoutineDescription.text = routinesItemPreview.description
            binding.textViewCountItem.text = routinesItemPreview.totalUsed.toString()

            binding.cardViewDefaultRoutine.setOnClickListener {
                onClick(routinesItemPreview)
            }

            binding.buttonDeleteItemRoutine.setOnClickListener {
                onClickDelete(routinesItemPreview)
            }
        }

    }

    class RoutinesItemPreviewDiffUtil : DiffUtil.ItemCallback<RoutinesItemPreview>(){
        override fun areItemsTheSame(
            oldItem: RoutinesItemPreview,
            newItem: RoutinesItemPreview,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: RoutinesItemPreview,
            newItem: RoutinesItemPreview,
        ): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    fun getIndexById(id: Long) : Int {
        val index = routines.indexOfFirst { it.id == id }
        Log.d("RoutinesAdapter", "getIndexById: $index")
        return index
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutinesViewHolder {
        val binding = ItemRoutinesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RoutinesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutinesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}