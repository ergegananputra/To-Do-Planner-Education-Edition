package com.minizuure.todoplannereducationedition.dialog_modal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.databinding.ItemGeneralBottomSheetBinding
import com.minizuure.todoplannereducationedition.dialog_modal.model_interfaces.GlobalMinimumInterface

class GlobalAdapter(
    var onClickAction : (GlobalMinimumInterface) -> Unit = {},
    var onLongClickAction : (GlobalMinimumInterface) -> Unit = {},
    private val useIndexes : Boolean = false,
    private val firstDiffrenetColor : Boolean = false,
    private val startFromIndexZero : Boolean = false,
    private val useCustomName : Boolean = false,
    private val customNameLogic : (GlobalMinimumInterface) -> String = {""}
) : ListAdapter<GlobalMinimumInterface, GlobalAdapter.GlobalViewHolder>(GlobalDiffUtils()) {

    inner class GlobalViewHolder(
        private val binding : ItemGeneralBottomSheetBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GlobalMinimumInterface, index: Int) {
            binding.textViewItemBottomSheet.text = if (useCustomName) {
                customNameLogic(item)
            } else {
                item.title
            }

            if (useIndexes) {
                binding.buttonItemBottomSheet.text = index.toString()
            } else {
                binding.buttonItemBottomSheet.text = item.title.first().toString()
            }

            if (firstDiffrenetColor) {
                Log.d("GlobalAdapter", "bind: $index ${item.title}")
                binding.buttonItemBottomSheet.isActivated = index != 0
            } else {
                binding.buttonItemBottomSheet.isActivated = true
            }

            binding.cardViewItemGeneral.setOnClickListener {
                onClickAction(item)
            }

            binding.cardViewItemGeneral.setOnLongClickListener {
                onLongClickAction(item)
                true
            }
        }

    }

    class GlobalDiffUtils : DiffUtil.ItemCallback<GlobalMinimumInterface>() {
        override fun areItemsTheSame(oldItem: GlobalMinimumInterface, newItem: GlobalMinimumInterface): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GlobalMinimumInterface, newItem: GlobalMinimumInterface): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalViewHolder {
        val binding = ItemGeneralBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return GlobalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GlobalViewHolder, position: Int) {
        val indexPosition = if (startFromIndexZero) position else position + 1
        holder.bind(getItem(position), indexPosition)
    }




}
