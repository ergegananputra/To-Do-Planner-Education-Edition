package com.minizuure.todoplannereducationedition.recycler.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.databinding.ItemRoutinesBinding
import com.minizuure.todoplannereducationedition.recycler.model.RoutinesItemPreview

class RoutinesAdapter(
    private val onClick : (RoutinesItemPreview) -> Unit,
    private val onClickDelete : (RoutinesItemPreview) -> Unit,
    private val onLongClick : (RoutinesItemPreview) -> Unit,
    private var _isSelectMode : Boolean = false,
    private val onClickSelect : (RoutinesItemPreview) -> Unit,
) : ListAdapter<RoutinesItemPreview, RoutinesAdapter.RoutinesViewHolder>(RoutinesItemPreviewDiffUtil()) {

    var isSelectMode : Boolean
        get() = _isSelectMode
        set(value) {
            _isSelectMode = value
            notifyDataSetChanged()
        }

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

            binding.cardViewDefaultRoutine.setOnLongClickListener {
                onLongClick(routinesItemPreview)
                true
            }

            binding.buttonDeleteItemRoutine.setOnClickListener {
                onClickDelete(routinesItemPreview)
            }

            // Select Mode
            if (isSelectMode) {
                binding.buttonDeleteItemRoutine.visibility = View.GONE
                binding.radioButtonSelectModeItemRoutine.visibility = View.VISIBLE
                binding.radioButtonSelectModeItemRoutine.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        onClickSelect(routinesItemPreview)
                    }
                }
            } else {
                binding.radioButtonSelectModeItemRoutine.visibility = View.GONE
                binding.buttonDeleteItemRoutine.visibility = View.VISIBLE
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
        val index = currentList.indexOfFirst { it.id == id }
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