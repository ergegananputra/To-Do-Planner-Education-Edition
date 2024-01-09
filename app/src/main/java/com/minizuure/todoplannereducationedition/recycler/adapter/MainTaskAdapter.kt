package com.minizuure.todoplannereducationedition.recycler.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.CardScheduleBinding
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

class MainTaskAdapter(
    private var currentDate : ZonedDateTime,
    private val scope : CoroutineScope,
    private val routineViewModel : RoutineViewModel,
    private val sessionViewModel: SessionViewModel,
    private val taskViewModel : TaskViewModel,
) : ListAdapter<TaskTable, MainTaskAdapter.MainTaskViewHolder>(MainTaskDiffUtil()){
    class MainTaskDiffUtil : DiffUtil.ItemCallback<TaskTable>(){
        override fun areItemsTheSame(oldItem: TaskTable, newItem: TaskTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskTable, newItem: TaskTable): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    inner class MainTaskViewHolder(
        private val binding : CardScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: TaskTable) {
            setupIconTime(item)
            setupTextTIme(item.sessionId, item.isCustomSession, item.startTime, item.endTime)
            setupIconCommunity(item.isSharedToCommunity)
            setupTextDay(item.indexDay)
            setupTextTitle(item.title)
            setupTextLocation(item.locationName)

            //TODO: setup Quiz and To-Packs
        }

        private fun setupTextLocation(locationName: String?) {
            locationName?.let {
                binding.cardTextLocation.text = it
            }
        }

        private fun setupTextTitle(title: String) {
            binding.textViewTitle.text = title
        }

        private fun setupTextDay(indexDay: Int) {
            val dayName =DatetimeAppManager().dayNameFromDayId(indexDay)
            binding.chipCurrentDay.text = dayName
        }

        private fun setupIconCommunity(toCommunity: Boolean) {
            if (toCommunity) {
                binding.buttonCommunitiesScheduleCard.visibility = ViewGroup.VISIBLE
            } else {
                binding.buttonCommunitiesScheduleCard.visibility = ViewGroup.GONE
            }
        }

        private fun setupTextTIme(
            sessionId: Long,
            customSession: Boolean,
            startTime: String?,
            endTime: String?
        ) {
            if (customSession) {
                val time = "$startTime - $endTime"
                binding.textViewDatetimeScheduleCard.text = time
                return
            }

            scope.launch {
                val session = withContext(Dispatchers.IO) { sessionViewModel.getById(sessionId) }
                session?.let {
                    val time = "${session.timeStart} - ${session.timeEnd}"
                    binding.textViewDatetimeScheduleCard.text = time
                }
            }
        }


        private fun setupIconTime(item: TaskTable) {
            val timeEnd = DatetimeAppManager().convertStringTimeToMinutes(item.endTime!!)
            val localDate = DatetimeAppManager().getLocalDateTime()
            val currTime = DatetimeAppManager().convertStringTimeToMinutes(
                "${localDate.hour}:${localDate.minute}"
            )

            val isForward = localDate.isAfter(currentDate)
            if (isForward) {
                binding.imageViewIconScheduleCard.setImageResource(R.drawable.ic_schedule_fill)
                return
            }

            if (currTime < timeEnd) {
                binding.imageViewIconScheduleCard.setImageResource(R.drawable.ic_schedule_fill)
            } else {
                binding.imageViewIconScheduleCard.setImageResource(R.drawable.ic_schedule_outline)
            }
        }


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainTaskViewHolder {
        val binding = CardScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MainTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}