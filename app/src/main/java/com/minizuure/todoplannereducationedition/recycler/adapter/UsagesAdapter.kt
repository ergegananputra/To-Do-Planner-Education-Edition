package com.minizuure.todoplannereducationedition.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.ItemUsagesCardBinding
import com.minizuure.todoplannereducationedition.recycler.model.UsagesPreviewModel

class UsagesAdapter(
    private val context : Context,
    private val onClick : (UsagesPreviewModel) -> Unit,
) : ListAdapter<UsagesPreviewModel, UsagesAdapter.UsagesViewHolder>(UsagesDiffUtils()) {
    class UsagesDiffUtils : DiffUtil.ItemCallback<UsagesPreviewModel>() {
        override fun areItemsTheSame(
            oldItem: UsagesPreviewModel,
            newItem: UsagesPreviewModel,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UsagesPreviewModel,
            newItem: UsagesPreviewModel,
        ): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    inner class UsagesViewHolder(
        private val binding : ItemUsagesCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(previewModel: UsagesPreviewModel) {
            binding.textViewDaysOfTheWeek.text = previewModel.name
            binding.buttonDaysPic.text = previewModel.name.first().toString()

            val usedText = "${previewModel.used} ${context.getString(R.string.session_used)}"
            binding.textViewSessionUsed.text = usedText

            binding.cardViewUsages.setOnClickListener {
                onClick(previewModel)
            }

            binding.buttonDaysPic.isActivated = previewModel.id != 0
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsagesViewHolder {
        val binding = ItemUsagesCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return UsagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsagesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}