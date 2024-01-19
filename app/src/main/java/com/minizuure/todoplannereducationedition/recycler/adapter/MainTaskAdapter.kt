package com.minizuure.todoplannereducationedition.recycler.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minizuure.todoplannereducationedition.databinding.CardScheduleBinding
import com.minizuure.todoplannereducationedition.services.animator.ObjectFade
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
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
    private val notesViewModel: NoteViewModel,
    private val onClickOpenDetail : (TaskTable) -> Unit,
    private val onClickOpenQuizInDetail : (TaskTable) -> Unit,
    private val onClickOpenToPackInDetail : (TaskTable) -> Unit
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


            scope.launch {
                val session = withContext(Dispatchers.IO) {
                    sessionViewModel.getById(item.sessionId)
                } ?: return@launch

                setupIconTime(item, session.timeEnd)
                setupTextTime(session)
            }

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
            ObjectFade(binding.cardQuizMaterialDetail)
                .fadeIn()
                .start()
        }

        private fun hideQuizDetail() {
            ObjectFade(binding.cardQuizMaterialDetail)
                .fadeOut()
                .start()
        }

        private fun showToPackDetail() {
            ObjectFade(binding.cardToPackDetail)
                .fadeIn()
                .start()
        }

        private fun hideToPackDetail() {
            ObjectFade(binding.cardToPackDetail)
                .fadeOut()
                .start()
        }

        private fun setupToPackDetail(item: TaskTable) {
            setupToPackOpenInDetail(item)
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

        private fun setupToPackOpenInDetail(taskTable: TaskTable) {
            binding.buttonToPackOpenInDetail.setOnClickListener {
                onClickOpenToPackInDetail(taskTable)
            }
        }

        private fun setupQuizDetail(item: TaskTable) {
            setupQuizOpenInDetail(item)
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

        private fun setupQuizOpenInDetail(item: TaskTable) {
            binding.buttonQuizOpenInDetail.setOnClickListener {
                onClickOpenQuizInDetail(item)
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

        private fun setupTextTime(session: SessionTable) {
            val time = "${session.timeStart} - ${session.timeEnd}"
            binding.textViewDatetimeScheduleCard.text = time
        }


        private fun setupIconTime(item: TaskTable, endTime: String) {
            val timeEnd = DatetimeAppManager().convertStringTimeToMinutes(endTime)
            val localDate = DatetimeAppManager().getLocalDateTime()

            val currTime = localDate.hour * 60 + localDate.minute


            val isForward = localDate.isBefore(currentDate)
            Log.d("MainTaskAdapter", "isForward: $isForward from ${item.title}")
            if (isForward) {
                binding.imageViewIconScheduleCard.isActivated = true
                return
            }

            binding.imageViewIconScheduleCard.isActivated = currTime < timeEnd
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