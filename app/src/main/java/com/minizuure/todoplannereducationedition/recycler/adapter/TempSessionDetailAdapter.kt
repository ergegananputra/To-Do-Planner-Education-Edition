package com.minizuure.todoplannereducationedition.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.minizuure.todoplannereducationedition.databinding.ItemSessionDetailBinding
import com.minizuure.todoplannereducationedition.model.TempSession

class TempSessionDetailAdapter(
    var sessions : MutableList<TempSession>,
    private val onClick : (TempSession) -> Unit,
    private val onLongClick : (TempSession) -> Unit,
) : RecyclerView.Adapter<TempSessionDetailAdapter.TempSessionViewHolder>() {
    inner class TempSessionViewHolder(
        private val binding : ItemSessionDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(session: TempSession) {

            binding.textViewSessionTitleItemDetail.text = session.title

            setupTime(session.startTime, session.endTime)
            setupTags(session.daysSelected)

            binding.cardViewSessionDetail.setOnClickListener {
                onClick(session)
            }

            binding.cardViewSessionDetail.setOnLongClickListener {
                onLongClick(session)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempSessionViewHolder {
        val binding = ItemSessionDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TempSessionViewHolder(binding)
    }

    override fun getItemCount(): Int = sessions.size

    override fun onBindViewHolder(holder: TempSessionViewHolder, position: Int) {
        holder.bind(sessions[position])
    }
}