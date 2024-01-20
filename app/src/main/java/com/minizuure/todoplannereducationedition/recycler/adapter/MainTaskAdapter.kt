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
import com.minizuure.todoplannereducationedition.services.database.join.TaskAndSessionJoin
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime


class MainTaskAdapter(
    private var currentDate : ZonedDateTime,
    private val scope : CoroutineScope,
    private val notesViewModel: NoteViewModel,
    private val onClickOpenDetail : (TaskAndSessionJoin) -> Unit,
    private val onClickOpenQuizInDetail : (TaskAndSessionJoin) -> Unit,
    private val onClickOpenToPackInDetail : (TaskAndSessionJoin) -> Unit
) : ListAdapter<TaskAndSessionJoin, MainTaskAdapter.MainTaskViewHolder>(MainTaskDiffUtil()){
    fun setNewCurrentDate(currentDate: ZonedDateTime) {
        this.currentDate = currentDate
    }
    class MainTaskDiffUtil : DiffUtil.ItemCallback<TaskAndSessionJoin>(){
        override fun areItemsTheSame(oldItem: TaskAndSessionJoin, newItem: TaskAndSessionJoin): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskAndSessionJoin, newItem: TaskAndSessionJoin): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    inner class MainTaskViewHolder(
        private val binding : CardScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: TaskAndSessionJoin) {

            setupTagsVisibility(item)
            setupQuizDetail(item)
            setupToPackDetail(item)

            setupIconTime(item, item.sessionTimeEnd)
            setupTextTime(item)

            setupIconCommunity(item.isSharedToCommunity)
            setupTextDay(item.indexDay)
            setupTextTitle(item.title)
            setupTextLocation(item.locationName)

            binding.cardViewMainItem.setOnClickListener {
                onClickOpenDetail(item)
            }



        }

        private fun setupTagsVisibility(item: TaskAndSessionJoin) {
            scope.launch {
                val dateTimeString = DatetimeAppManager(currentDate, true).dateISO8601inString
                val countQuiz = withContext(Dispatchers.IO) {
                    notesViewModel.note.getCountByFKTaskIdAndCategory(item.id, CATEGORY_QUIZ, dateTimeString)
                }
                val countToPack = withContext(Dispatchers.IO) {
                    notesViewModel.note.getCountByFKTaskIdAndCategory(item.id, CATEGORY_TO_PACK, dateTimeString)
                }

                if (countQuiz > 0) {
                    binding.buttonTagsQuiz.visibility = View.VISIBLE
                    hideQuizDetail()

                    setQuizTagsListener()
                } else {
                    binding.buttonTagsQuiz.visibility = View.GONE
                    hideQuizDetail()
                }

                if (countToPack > 0) {
                    binding.buttonTagsToPack.visibility = View.VISIBLE
                    hideToPackDetail()

                    setToPackTagsListener()
                } else {
                    binding.buttonTagsToPack.visibility = View.GONE
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

        private fun setupToPackDetail(item: TaskAndSessionJoin) {
            setupToPackOpenInDetail(item)
            val toPackMaterialAdapter = TodoNotesAdapter(
                onClickCheckBoxAction = { _, _ -> },
                onClickDeleteAction = { _ -> }
            )

            scope.launch {
                val dateTimeString = DatetimeAppManager(currentDate, true).dateISO8601inString

                val toPackNote = withContext(Dispatchers.IO) {
                    notesViewModel.note.getByFKTaskIdAndCategory(item.id, CATEGORY_TO_PACK, dateTimeString)
                }

                toPackNote?.let {
                    setAdapterOnClickListener(toPackMaterialAdapter, it.id)

                    binding.textViewToPackShortDescription.text = it.description
                    setupToPack(toPackMaterialAdapter, it.id)
                }
            }
        }

        private fun setupToPackOpenInDetail(taskAndSessionJoinTable: TaskAndSessionJoin) {
            binding.buttonToPackOpenInDetail.setOnClickListener {
                onClickOpenToPackInDetail(taskAndSessionJoinTable)
            }
        }

        private fun setupQuizDetail(item: TaskAndSessionJoin) {
            setupQuizOpenInDetail(item)
            val quizMaterialAdapter = TodoNotesAdapter(
                onClickCheckBoxAction = { _, _ -> },
                onClickDeleteAction = { _ -> }
            )


            scope.launch {
                val quizNote = withContext(Dispatchers.IO) {
                    val dateTimeString = DatetimeAppManager(currentDate, true).dateISO8601inString

                    notesViewModel.note.getByFKTaskIdAndCategory(item.id, CATEGORY_QUIZ, dateTimeString)
                }

                quizNote?.let {
                    setAdapterOnClickListener(quizMaterialAdapter, it.id)

                    binding.textViewQuizShortDescription.text = it.description
                    setupQuizMaterial(quizMaterialAdapter, it.id)

                }
            }
        }

        private fun setupQuizOpenInDetail(item: TaskAndSessionJoin) {
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
                if (locationName.isBlank()) {
                    binding.cardIconLocationOn.visibility = View.GONE
                    binding.cardTextLocation.visibility = View.GONE
                    return@let
                }
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

        private fun setupTextTime(item: TaskAndSessionJoin) {
            val time = "${item.sessionTimeStart} - ${item.sessionTimeEnd}"
            binding.textViewDatetimeScheduleCard.text = time
        }


        private fun setupIconTime(item: TaskAndSessionJoin, endTime: String) {
            val timeEnd = DatetimeAppManager().convertStringTimeToMinutes(endTime)
            val localDate = DatetimeAppManager().selectedDetailDatetimeISO


            val isForward = localDate.isBefore(currentDate)
            Log.d(
                "MainTaskAdapter",
                "\n${item.title}" +
                        "\nisForward: $isForward" +
                        "\nlocalDate: $localDate" +
                        "\ncurrentDate: $currentDate"
            )
            if (isForward) {
                binding.imageViewIconScheduleCard.isActivated = true
                return
            }

            val currTime = localDate.hour * 60 + localDate.minute
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