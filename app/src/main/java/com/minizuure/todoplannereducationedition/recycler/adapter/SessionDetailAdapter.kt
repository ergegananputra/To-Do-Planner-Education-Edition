package com.minizuure.todoplannereducationedition.recycler.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.minizuure.todoplannereducationedition.databinding.ItemSessionDetailBinding
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable

class SessionDetailAdapter(
    private val onClick : (SessionTable) -> Unit,
    private val onLongClick : (SessionTable, MaterialButton) -> Unit,
) : ListAdapter<SessionTable, SessionDetailAdapter.SessionDetailViewHolder>(SessionDetailDiffUtils()){
    inner class SessionDetailViewHolder(
        private val binding : ItemSessionDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sessionTable: SessionTable) {

            binding.textViewSessionTitleItemDetail.text = sessionTable.title

            setupTime(sessionTable.timeStart, sessionTable.timeEnd)
            setupTags(sessionTable.selectedDays)

            binding.cardViewSessionDetail.setOnClickListener {
                onClick(sessionTable)
            }

            binding.cardViewSessionDetail.setOnLongClickListener {
                if (binding.buttonDeleteItemSessionDetail.visibility == MaterialTextView.VISIBLE) {
                    binding.buttonDeleteItemSessionDetail.visibility = MaterialTextView.GONE
                }
                else {
                    onLongClick(sessionTable, binding.buttonDeleteItemSessionDetail)
                }
                true
            }

        }

        private fun setupTime(startTime: String, endTime: String) {
            val time = "$startTime - $endTime"
            binding.textViewTimeSessionItemDetail.text = time
        }

        private fun setupTags(daysSelected : String) {
            val days = listOf(
                binding.tagsSunday,
                binding.tagsMonday,
                binding.tagsTuesday,
                binding.tagsWednesday,
                binding.tagsThursday,
                binding.tagsFriday,
                binding.tagsSaturday,
            )

            days.forEachIndexed { index, tags ->
                if (daysSelected[index] == '1') {
                    tags.visibility = MaterialTextView.VISIBLE
                } else {
                    tags.visibility = MaterialTextView.GONE
                }
            }
        }

    }

    class SessionDetailDiffUtils : DiffUtil.ItemCallback<SessionTable>() {
        override fun areItemsTheSame(oldItem: SessionTable, newItem: SessionTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SessionTable, newItem: SessionTable): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    fun getIndexById(id: Long) : Int {
        val index = currentList.indexOfFirst { it.id == id }
        Log.d("SessionAdapter", "getIndexById: $index")
        return index
    }

    fun removeIndex(index: Int) {
        currentList.toMutableList().removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionDetailViewHolder {
        val binding = ItemSessionDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SessionDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}