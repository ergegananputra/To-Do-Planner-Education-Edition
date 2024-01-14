package com.minizuure.todoplannereducationedition.recycler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.CardScheduleBinding
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
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

/**
 * TODO LIST [MainTaskAdapter]
 * [MainTaskAdapter] needs to be able to show and navigate the quiz and toPack detail
 */
class MainTaskAdapter(
    private var currentDate : ZonedDateTime,
    private val scope : CoroutineScope,
    private val routineViewModel : RoutineViewModel,
    private val sessionViewModel: SessionViewModel,
    private val taskViewModel : TaskViewModel,
    private val notesViewModel: NoteViewModel,
    private val onClickOpenDetail : (TaskTable) -> Unit
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

            binding.cardViewMainItem.setOnClickListener {
                onClickOpenDetail(item)
            }

            setupQuizDetail(item)
            setupToPackDetail(item)
            setupTagsVisibility(item)

            //TODO : Open Quiz and ToPack Detail
        }

        private fun setupTagsVisibility(item: TaskTable) {
            scope.launch {
                val countQuiz = withContext(Dispatchers.IO) {
                    notesViewModel.note.getCountByFKTaskIdAndCategory(item.id, CATEGORY_QUIZ)
                }
                val countToPack = withContext(Dispatchers.IO) {
                    notesViewModel.note.getCountByFKTaskIdAndCategory(item.id, CATEGORY_TO_PACK)
                }

                if (countQuiz > 0) {
                    binding.groupQuizMaterialDetail.visibility = View.VISIBLE
                    hideQuizDetail()

                    setQuizTagsListener()
                } else {
                    binding.groupQuizMaterialDetail.visibility = View.GONE
                    hideQuizDetail()
                }

                if (countToPack > 0) {
                    binding.groupToPackDetail.visibility = View.VISIBLE
                    hideToPackDetail()

                    setToPackTagsListener()
                } else {
                    binding.groupToPackDetail.visibility = View.GONE
                    hideToPackDetail()
                }
            }
        }

        private fun setQuizTagsListener() {
            binding.buttonTagsQuiz.setOnClickListener {
                if (binding.cardQuizMaterialDetail.visibility == View.VISIBLE) {
                    hideQuizDetail()
                } else {
                    showQuizDetail()
                    hideToPackDetail()
                }
            }
        }

        private fun setToPackTagsListener() {
            binding.buttonTagsToPack.setOnClickListener {
                if (binding.cardToPackDetail.visibility == View.VISIBLE) {
                    hideToPackDetail()
                } else {
                    showToPackDetail()
                    hideQuizDetail()
                }
            }
        }

        private fun showQuizDetail() {
            binding.cardQuizMaterialDetail.visibility = View.VISIBLE
        }

        private fun hideQuizDetail() {
            binding.cardQuizMaterialDetail.visibility = View.GONE
        }

        private fun showToPackDetail() {
            binding.cardToPackDetail.visibility = View.VISIBLE
        }

        private fun hideToPackDetail() {
            binding.cardToPackDetail.visibility = View.GONE
        }

        private fun setupToPackDetail(item: TaskTable) {
            val toPackMaterialAdapter = TodoNotesAdapter(
                onClickCheckBoxAction = { _, _ -> },
                onClickDeleteAction = { _ -> }
            )

            scope.launch {
                val toPackNote = withContext(Dispatchers.IO) {
                    notesViewModel.note.getByFKTaskIdAndCategory(item.id, CATEGORY_TO_PACK)
                }

                toPackNote?.let {
                    setAdapterOnClickListener(toPackMaterialAdapter, it.id)

                    binding.textViewToPackShortDescription.text = it.description
                    setupToPack(toPackMaterialAdapter, it.id)
                }
            }
        }

        private fun setupQuizDetail(item: TaskTable) {
            val quizMaterialAdapter = TodoNotesAdapter(
                onClickCheckBoxAction = { _, _ -> },
                onClickDeleteAction = { _ -> }
            )


            scope.launch {
                val quizNote = withContext(Dispatchers.IO) {
                    notesViewModel.note.getByFKTaskIdAndCategory(item.id, CATEGORY_QUIZ)
                }

                quizNote?.let {
                    setAdapterOnClickListener(quizMaterialAdapter, it.id)

                    binding.textViewQuizShortDescription.text = it.description
                    setupQuizMaterial(quizMaterialAdapter, it.id)
                }
            }
        }

        private fun setAdapterOnClickListener(quizMaterialAdapter: TodoNotesAdapter, noteId: Long) {
            quizMaterialAdapter.apply {
                onClickCheckBoxAction = {id, isChecked ->
                    setOnClickCheckBoxTodo(id, noteId, isChecked, quizMaterialAdapter)
                }
                onClickDeleteAction = {id ->
                    setOnClickDeleteTodo(id, noteId, quizMaterialAdapter)
                }
            }
        }



        private fun setOnClickDeleteTodo(
            id: Long,
            noteId: Long,
            notesAdapter: TodoNotesAdapter
        ) {
            scope.launch(Dispatchers.IO) {
                notesViewModel.todo.deleteById(id)
                updateRecyclerViewData(notesAdapter, noteId)

            }
        }


        private fun setOnClickCheckBoxTodo(
            id: Long,
            noteId: Long,
            checked: Boolean,
            quizMaterialAdapter: TodoNotesAdapter
        ) {
            scope.launch(Dispatchers.IO) {
                notesViewModel.todo.updateChecked(id, checked)
                updateRecyclerViewData(quizMaterialAdapter, noteId)
            }
        }

        private fun setupToPack(toPackAdapter: TodoNotesAdapter, noteId: Long) {
            setupToPackRecyclerView(toPackAdapter, noteId)
        }

        private fun setupToPackRecyclerView(toPackAdapter: TodoNotesAdapter, noteId: Long) {
            binding.recyclerViewtoPack.apply{
                layoutManager = LinearLayoutManager(context)
                adapter = toPackAdapter
            }

            updateRecyclerViewData(toPackAdapter, noteId)
        }

        private fun setupQuizMaterial(quizMaterialAdapter: TodoNotesAdapter, noteId: Long) {
            setupQuizRecyclerView(quizMaterialAdapter, noteId)
        }

        private fun setupQuizRecyclerView(quizMaterialAdapter: TodoNotesAdapter, noteId: Long) {
            binding.recyclerViewQuiz.apply{
                layoutManager = LinearLayoutManager(context)
                adapter = quizMaterialAdapter
            }

            updateRecyclerViewData(quizMaterialAdapter, noteId)
        }

        private fun updateRecyclerViewData(notesAdapter: TodoNotesAdapter, noteId: Long) {
            scope.launch {
                val todoList = withContext(Dispatchers.IO) {
                    notesViewModel.todo.getByFKNoteId(noteId)
                } ?: return@launch

                notesAdapter.submitList(todoList)

            }

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