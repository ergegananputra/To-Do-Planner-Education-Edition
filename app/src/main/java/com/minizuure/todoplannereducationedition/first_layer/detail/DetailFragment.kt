package com.minizuure.todoplannereducationedition.first_layer.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentDetailBinding
import com.minizuure.todoplannereducationedition.dialog_modal.TaskDetailBottomSheetDialogFragment
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL_GO_TO_PACK
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity.Companion.OPEN_DETAIL_GO_TO_QUIZ
import com.minizuure.todoplannereducationedition.model.ParcelableZoneDateTime
import com.minizuure.todoplannereducationedition.recycler.adapter.TodoNotesAdapter
import com.minizuure.todoplannereducationedition.services.animator.ObjectBlink
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_MEMO
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK
import com.minizuure.todoplannereducationedition.services.database.DEFAULT_NOTE_ID
import com.minizuure.todoplannereducationedition.services.database.join.TaskAndSessionJoin
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModel
import com.minizuure.todoplannereducationedition.services.database.relations_table.SessionTaskProviderViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.notification.ItemAlarmQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val ARG_DETAIL_ID = "task_detail_id"
private const val ARG_DETAIL_TITLE = "title_detail"
private const val ARG_DETAIL_SELECTED_DATE = "selected_datetime_detail_iso"
private const val ARG_DETAIL_SET_GO_TO = "set_go_to"

/**
 * Todo List [DetailFragment] :
 *
 * - [ ] Setup bottom more dialog -- communities setting*
 *
 *
 * '*' : Terdapat hubungan ke kelas lainnya, [ActionMoreTaskBottomDialogFragment], [TaskDetailBottomSheetDialogFragment]
 *
 */
class DetailFragment : Fragment() {
    val args : DetailFragmentArgs by navArgs()
    private lateinit var navController: NavController

    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var taskViewModel : TaskViewModel
    private lateinit var notesViewModel : NoteViewModel
    private lateinit var sessionTaskProviderViewModel: SessionTaskProviderViewModel

    private val binding by lazy {
        FragmentDetailBinding.inflate(layoutInflater)
    }

    private val quizMaterialAdapter by lazy {
        TodoNotesAdapter(
            onClickCheckBoxAction = {id, isChecked ->
                setOnClickCheckBoxTodo(id, isChecked, CATEGORY_QUIZ)
            },
            onClickDeleteAction = {id ->
                setOnClickDeleteTodo(id, CATEGORY_QUIZ)
            }
        )
    }

    private val toPackAdapter by lazy {
        TodoNotesAdapter(
            onClickCheckBoxAction = {id, isChecked ->
                setOnClickCheckBoxTodo(id, isChecked, CATEGORY_TO_PACK)
            },
            onClickDeleteAction = {id ->
                setOnClickDeleteTodo(id, CATEGORY_TO_PACK)
            }
        )
    }

    private fun setOnClickDeleteTodo(id: Long, category : String = "all") {
        lifecycleScope.launch(Dispatchers.IO) {
            notesViewModel.todo.deleteById(id)

            when(category) {
                CATEGORY_QUIZ -> {
                    updateMaterialQuizRecyclerViewData()
                }
                CATEGORY_TO_PACK -> {
                    updateToPackRecyclerViewData()
                }
                else -> {
                    updateMaterialQuizRecyclerViewData()
                    updateToPackRecyclerViewData()
                }
            }
        }
    }

    private fun setOnClickCheckBoxTodo(id: Long, checked: Boolean, category : String = "all") {
        lifecycleScope.launch(Dispatchers.IO) {
            notesViewModel.todo.updateChecked(id, checked)

            when(category) {
                CATEGORY_QUIZ -> {
                    updateMaterialQuizRecyclerViewData()
                }
                CATEGORY_TO_PACK -> {
                    updateToPackRecyclerViewData()
                }
                else -> {
                    updateMaterialQuizRecyclerViewData()
                    updateToPackRecyclerViewData()
                }
            }
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setupViewModelFactory()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as TaskManagementActivity).setToolbarTitle(this, args.titleDetail)
        navController = Navigation.findNavController(view)
        setScrollGoTo()

        lifecycleScope.launch {
            val taskAndSessionJoin = withContext(Dispatchers.IO) {
                taskViewModel.getTaskAndSessionJoinByProviderPrimaryKeys(
                    indexDay = args.indexDay,
                    taskId = args.taskDetailId,
                    sessionId = args.sessionId,
                    paramDateIso8601 = ""
                )
            }?: run {
                Log.e("DetailFragment", "onViewCreated: taskAndSessionJoin is null \n" +
                        "with Primary Keys : ${args.indexDay}, ${args.taskDetailId}, ${args.sessionId}")
                return@launch closeFragment()
            }
            val session = SessionTable(
                id = taskAndSessionJoin.fkSessionId,
                title = taskAndSessionJoin.sessionTitle,
                timeStart = taskAndSessionJoin.sessionTimeStart,
                timeEnd = taskAndSessionJoin.sessionTimeEnd,
                selectedDays = taskAndSessionJoin.sessionSelectedDays,
                fkRoutineId = taskAndSessionJoin.sessionFkRoutineId,
                isCustomSession = taskAndSessionJoin.sessionIsCustomSession
            )

            val routine = routineViewModel.getById(session.fkRoutineId) ?: return@launch closeFragment()

            (activity as TaskManagementActivity).setToolbarTitle(this@DetailFragment, taskAndSessionJoin.title)

            setupChipTags(taskAndSessionJoin, routine)
            setupDate()
            setupTime(session)
            setupLocation(taskAndSessionJoin)
            setupRoutineTemplateText(routine)
            setupQuizMaterial(routine, args.selectedDatetimeDetailIso)
            setupToPack(routine, args.selectedDatetimeDetailIso)
            setupMemo(routine)
            setupRescheduleButton(taskAndSessionJoin, routine)
            setupNextPlanQuizMaterial(routine)
            setupNextPlanToPack(routine)
        }
    }

    private fun setupNextPlanToPack(routine: RoutineTable) {
        binding.buttonToPackNextPlan.setOnClickListener {
            val bottomSheet = TaskDetailBottomSheetDialogFragment(
                taskId = args.taskDetailId,
                routine = routine,
                sessionTaskProviderViewModel = sessionTaskProviderViewModel,
                currentDate = args.selectedDatetimeDetailIso.zoneDateTime,
                isNextPlan = true,
                title = getString(R.string.to_pack),
                description = null,
                onClickSaveAction = { isNextPlan, description, title, weekSelected, weeksDictionary ->
                    Log.d("DetailFragment", "setupNextPlanToPack: $isNextPlan, $description, $title, $weekSelected, $weeksDictionary")
                    insertMemoAction(CATEGORY_TO_PACK, isNextPlan, description, title, weekSelected, weeksDictionary)
                }
            )

            bottomSheet.show(parentFragmentManager, "TaskDetailBottomSheetDialogFragment")
        }
    }

    private fun setupNextPlanQuizMaterial(routine: RoutineTable) {
        binding.buttonQuizNextPlan.setOnClickListener {
            val bottomSheet = TaskDetailBottomSheetDialogFragment(
                taskId = args.taskDetailId,
                routine = routine,
                sessionTaskProviderViewModel = sessionTaskProviderViewModel,
                currentDate = args.selectedDatetimeDetailIso.zoneDateTime,
                isNextPlan = true,
                title = getString(R.string.quiz_materials_title),
                description = null,
                onClickSaveAction = { isNextPlan, description, title, weekSelected, weeksDictionary ->
                    Log.d("DetailFragment", "setupNextPlanQuizMaterial: $isNextPlan, $description, $title, $weekSelected, $weeksDictionary")
                    insertMemoAction(CATEGORY_QUIZ, isNextPlan, description, title, weekSelected, weeksDictionary)
                }
            )

            bottomSheet.show(parentFragmentManager, "TaskDetailBottomSheetDialogFragment")
        }
    }

    private fun setupRescheduleButton(taskProvider: TaskAndSessionJoin, routine: RoutineTable) {
        val destination = DetailFragmentDirections.actionDetailFragmentToRescheduleFragment(
            taskId = taskProvider.id,
            selectedDatetimeDetailIso = args.selectedDatetimeDetailIso,
            routineId = routine.id,
            taskTitle = taskProvider.title,
            indexDay = taskProvider.indexDay,
            sessionId = taskProvider.fkSessionId,
        )
        binding.buttonRescheduleNextPlan.setOnClickListener {
            navController.navigate(destination)
        }
    }

    private fun setScrollGoTo() {
        val scrollTo = args.setGoTo ?: return
        when(scrollTo) {
            OPEN_DETAIL_GO_TO_QUIZ -> {
                lifecycleScope.launch {
                    (activity as TaskManagementActivity).binding.appBarLayoutTaskManagement.setExpanded(false, true)

                    binding.nestedScrollViewDetail.post {
                        binding.nestedScrollViewDetail.smoothScrollTo(0, binding.recyclerViewQuizMaterials.top, 1000)
                    }

                    val quizTitleAnimator = ObjectBlink(binding.textViewQuizMaterialLabel)
                        .setAsBlink(
                            duration = 750L,
                            repeat = 16
                        )

                    quizTitleAnimator.start()
                }

            }
            OPEN_DETAIL_GO_TO_PACK -> {
                lifecycleScope.launch {
                    (activity as TaskManagementActivity).binding.appBarLayoutTaskManagement.setExpanded(false, true)

                    binding.nestedScrollViewDetail.post {
                        binding.nestedScrollViewDetail.smoothScrollTo(0, binding.recyclerViewToPack.top, 1000)
                    }

                    val toPackTitleAnimator = ObjectBlink(binding.textViewToPackLabel)
                        .setAsBlink(
                            duration = 750L,
                            repeat = 16
                        )

                    toPackTitleAnimator.start()
                }

            }
        }
    }

    private fun setupChipTags(taskProvider: TaskAndSessionJoin, routine: RoutineTable) {
        lifecycleScope.launch {
            val day = DatetimeAppManager().dayNameFromDayId(taskProvider.indexDay)
            val dateTimeString = DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString
            binding.chipDay.text = day

            val quizCount = withContext(Dispatchers.IO) {
                notesViewModel.note.getCountByFKTaskIdAndCategory(taskProvider.id, CATEGORY_QUIZ, dateTimeString)
            }
            binding.chipQuiz.visibility = if (quizCount == 0) View.GONE else View.VISIBLE


            val toPackCount = withContext(Dispatchers.IO) {
                notesViewModel.note.getCountByFKTaskIdAndCategory(taskProvider.id, CATEGORY_TO_PACK, dateTimeString)
            }
            binding.chipToPack.visibility = if (toPackCount == 0) View.GONE else View.VISIBLE

            val routineTitle = routine.title
            binding.chipRoutineName.text = routineTitle
        }
    }

    private fun setupMemo(routine: RoutineTable) {
        setupMemoClickAddButton()
        setupMemoClickEditButton()
        setupMemoClickSaveButton(routine)

        lifecycleScope.launch {
            val memoNote = withContext(Dispatchers.IO) {
                notesViewModel.note.getByFKTaskIdAndCategory(
                    args.taskDetailId,
                    CATEGORY_MEMO,
                    DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString
                )
            }
            memoNoteEmptyMode()

            memoNote?.let {
                binding.textViewMemoDescription.setText(it.description)
                memoNoteFilledMode()

                val lastEdit = withContext(Dispatchers.IO) {
                    val note = notesViewModel.note.getById(it.id)
                    val updateAt = note?.updatedAt
                    if (updateAt != null) {
                        val date = DatetimeAppManager(updateAt)
                        date.toReadable() + " | " + date.selectedDetailDatetimeISO.format(
                            DateTimeFormatter.ofPattern("HH:mm"))
                    } else {
                        null
                    }
                } ?: return@launch


                setMemoLastEdit(lastEdit)
            }
        }


    }

    private fun setupMemoClickSaveButton(routine: RoutineTable) {
        binding.buttonSaveMemo.setOnClickListener {
            val description = if (binding.textViewMemoDescription.text.toString().trim().isEmpty()) {
                null
            } else {
                binding.textViewMemoDescription.text.toString().trim()
            }


            if (description.isNullOrEmpty()) {
                memoNoteEmptyMode()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val memoNoteId = withContext(Dispatchers.IO) {
                    notesViewModel.note.getByFKTaskIdAndCategory(
                        args.taskDetailId,
                        CATEGORY_MEMO,
                        DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString
                    )?.id
                }

                val noteId = if (memoNoteId == null) {
                    // insert
                    withContext(Dispatchers.IO) {
                        notesViewModel.note.insert(
                            fkTaskId = args.taskDetailId,
                            category = CATEGORY_MEMO,
                            description = description,
                            date = DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString
                        )
                    }
                } else {
                    // update
                    withContext(Dispatchers.IO) {
                        notesViewModel.note.update(
                            id = memoNoteId,
                            description = description
                        )

                        memoNoteId
                    }

                }

                memoNoteFilledMode()

                val lastEdit = withContext(Dispatchers.IO) {
                    val note = notesViewModel.note.getById(noteId)
                    val updateAt = note?.updatedAt
                    if (updateAt != null) {
                        val date = DatetimeAppManager(updateAt)
                        date.toReadable() + " | " + date.selectedDetailDatetimeISO.format(
                            DateTimeFormatter.ofPattern("HH:mm"))
                    } else {
                        null
                    }
                } ?: return@launch


                setMemoLastEdit(lastEdit)


            }

        }
    }

    private fun setMemoLastEdit(hoursMinuteString: String) {
        val lastEditText = "${getString(R.string.last_edited)} $hoursMinuteString, ${getString(R.string.saved)}"
        binding.textViewMemoTime.text = lastEditText
    }

    private fun setupMemoClickEditButton() {
        binding.buttonEditMemo.setOnClickListener {
            memoNoteEditMode()
            binding.textViewMemoTime.text = getString(R.string.editing)
            binding.textViewMemoDescription.requestFocus()
        }
    }

    private fun setupMemoClickAddButton() {
        binding.buttonAddMemo.setOnClickListener {
            memoNoteEditMode(true)
            binding.textViewMemoTime.text = getString(R.string.editing)
            binding.textViewMemoDescription.requestFocus()
        }
    }

    private fun memoNoteEditMode(isFromClear : Boolean = false) {
        binding.buttonEditMemo.visibility = View.GONE
        binding.buttonAddMemo.visibility = View.GONE
        binding.buttonSaveMemo.visibility = View.VISIBLE
        binding.textViewMemoDescription.isEnabled = true
        binding.textViewMemoTime.visibility = View.VISIBLE

        if (isFromClear) {
            binding.textViewMemoDescription.setText("")
        }
    }

    private fun memoNoteEmptyMode() {
        binding.buttonEditMemo.visibility = View.GONE
        binding.buttonAddMemo.visibility = View.VISIBLE
        binding.buttonSaveMemo.visibility = View.GONE
        binding.textViewMemoDescription.isEnabled = false
        binding.textViewMemoTime.visibility = View.GONE
    }

    private fun memoNoteFilledMode() {
        binding.buttonEditMemo.visibility = View.VISIBLE
        binding.buttonAddMemo.visibility = View.GONE
        binding.buttonSaveMemo.visibility = View.GONE
        binding.textViewMemoDescription.isEnabled = false
        binding.textViewMemoTime.visibility = View.VISIBLE
    }

    private fun setupToPack(routine: RoutineTable, selectedDatetimeDetailIso: ParcelableZoneDateTime) {
        val description = binding.textViewToPackDescription.text.toString().trim().isBlank().let {
            if (it) null else binding.textViewToPackDescription.text.toString()
        }
        setAddToPack(routine, selectedDatetimeDetailIso, description)

        setupToPackRecyclerView()
    }

    private fun setupToPackRecyclerView() {
        binding.recyclerViewToPack.apply{
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = toPackAdapter
        }

        updateToPackRecyclerViewData()
    }

    private fun updateToPackRecyclerViewData() {
        lifecycleScope.launch {
            val toPackNote = notesViewModel.note.getByFKTaskIdAndCategory(
                args.taskDetailId,
                CATEGORY_TO_PACK,
                DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString
            )

            toPackNote?.let {
                val todoList = withContext(Dispatchers.IO) {
                    notesViewModel.todo.getByFKNoteId(toPackNote.id)
                } ?: return@launch

                toPackAdapter.submitList(todoList)
            }

        }
    }

    private fun setAddToPack(routine: RoutineTable, selectedDatetimeDetailIso: ParcelableZoneDateTime, description: String?) {
        binding.buttonAddToPack.setOnClickListener {
            val bottomSheet = TaskDetailBottomSheetDialogFragment(
                taskId = args.taskDetailId,
                routine = routine,
                sessionTaskProviderViewModel = sessionTaskProviderViewModel,
                currentDate = selectedDatetimeDetailIso.zoneDateTime,
                isNextPlan = false,
                title = getString(R.string.to_pack),
                description = description,
                onClickSaveAction = {isNextPlan, description, title, weekSelected, weeksDictionary ->
                    insertMemoAction(CATEGORY_TO_PACK,isNextPlan, description, title, weekSelected, weeksDictionary)
                    binding.chipToPack.visibility = View.VISIBLE
                }
            )

            bottomSheet.show(parentFragmentManager, "TaskDetailBottomSheetDialogFragment")
        }


        lifecycleScope.launch {
            val toPackNote = notesViewModel.note.getByFKTaskIdAndCategory(
                args.taskDetailId,
                CATEGORY_TO_PACK,
                DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString
            )

            toPackNote?.let {
                binding.textViewToPackDescription.setText(it.description)
            }
        }
    }



    private fun setupQuizMaterial(
        routine: RoutineTable,
        selectedDatetimeDetailIso: ParcelableZoneDateTime
    ) {
        setAddQuizMaterial(routine, selectedDatetimeDetailIso)

        setupQuizRecyclerView()
    }

    private fun setupQuizRecyclerView() {
        binding.recyclerViewQuizMaterials.apply{
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = quizMaterialAdapter
        }

        updateMaterialQuizRecyclerViewData()
    }

    private fun updateMaterialQuizRecyclerViewData() {
        lifecycleScope.launch {
            val quizNote = notesViewModel.note.getByFKTaskIdAndCategory(
                args.taskDetailId,
                CATEGORY_QUIZ,
                DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString
            )

            quizNote?.let {
                val todoList = withContext(Dispatchers.IO) {
                    notesViewModel.todo.getByFKNoteId(quizNote.id)
                } ?: return@launch

                quizMaterialAdapter.submitList(todoList)
            }

        }

    }

    private fun setAddQuizMaterial(
        routine: RoutineTable,
        currentDate: ParcelableZoneDateTime,
    ) {
        binding.buttonAddQuizMaterial.setOnClickListener {
            val currDesc = binding.textViewQuizMaterialDescription.text.toString().trim().isBlank().let {
                if (it) null else binding.textViewQuizMaterialDescription.text.toString()
            }

            val bottomSheet = TaskDetailBottomSheetDialogFragment(
                taskId = args.taskDetailId,
                routine = routine,
                sessionTaskProviderViewModel = sessionTaskProviderViewModel,
                currentDate = currentDate.zoneDateTime,
                isNextPlan = false,
                title = getString(R.string.quiz_materials_title),
                description = currDesc,
                onClickSaveAction = { isNextPlan, description, title, weekSelected, weeksDictionary ->
                    insertMemoAction(CATEGORY_QUIZ, isNextPlan, description, title, weekSelected, weeksDictionary)
                    binding.chipQuiz.visibility = View.VISIBLE
                }
            )

            bottomSheet.show(parentFragmentManager, "TaskDetailBottomSheetDialogFragment")
        }


        lifecycleScope.launch {
            val quizNote = notesViewModel.note.getByFKTaskIdAndCategory(
                args.taskDetailId,
                CATEGORY_QUIZ,
                DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString
            )

            quizNote?.let {
                binding.textViewQuizMaterialDescription.setText(it.description)
            }
        }
    }


    /**
     * Insert memo action
     *
     *
     * @param targetAction : String ([CATEGORY_QUIZ], [CATEGORY_TO_PACK], [CATEGORY_MEMO])
     * @param isNextPlan : Boolean
     * @param description : String
     * @param title : String?
     * @param weekSelected : ZoneDateTime
     * @param weeksDictionary : Map<"days name" : String, [weekSelected] : ZonedDateTime>
     *     - key : String
     *
     */
    private fun insertMemoAction(
        targetAction : String,
        isNextPlan: Boolean,
        description: String,
        title: String?,
        weekSelected: ZonedDateTime,
        weeksDictionary: Map<String, ZonedDateTime>
    ) {
        lifecycleScope.launch {
            // get note id
            val zonedDatetime : ZonedDateTime = args.selectedDatetimeDetailIso.zoneDateTime
            val dateTime  = DatetimeAppManager(zonedDatetime, true)

            val dateString = if (isNextPlan) {
                if (weekSelected.isEqual(dateTime.selectedDetailDatetimeISO)) {
                    Toast.makeText(requireContext(), getString(R.string.add_next_plan_memo_invalid), Toast.LENGTH_SHORT).show()
                    return@launch
                }
                DatetimeAppManager(weekSelected, true).dateISO8601inString
            } else {
                dateTime.dateISO8601inString
            }

            var noteId = withContext(Dispatchers.IO) {
                notesViewModel.note.getByFKTaskIdAndCategory(args.taskDetailId, targetAction, dateString)?.id
            }

            if (noteId != null) {
                // update Note
                val result = withContext(Dispatchers.IO) {
                    notesViewModel.note.update(
                        id = noteId!!,
                        description = description,
                    )
                }

                if (result) Log.d("DetailFragment", "update memo success. id : $noteId")
                else Log.d("DetailFragment", "update memo failed. id : $noteId")

            } else {
                // insert Note

                noteId = withContext(Dispatchers.IO) {
                    notesViewModel.note.insert(
                        fkTaskId = args.taskDetailId,
                        category = targetAction,
                        description = description,
                        date = dateString
                    )
                }
            }


            when(targetAction) {
                CATEGORY_QUIZ -> {
                    binding.textViewQuizMaterialDescription.setText(description)
                }
                CATEGORY_TO_PACK -> {
                    binding.textViewToPackDescription.setText(description)
                }
                CATEGORY_MEMO -> {
                    binding.textViewMemoDescription.setText(description)
                }
            }


            if (noteId == DEFAULT_NOTE_ID || title == null) return@launch

            // Quiz and To Pack only

            // Note: see ItemAlarmQueue.kt
            // Use alarm manager inside when statement to be safe
            val zonedDateTime = DatetimeAppManager(dateString).selectedDetailDatetimeISO
            val notificationId = ItemAlarmQueue().createItemId(
                zonedDateTime.dayOfMonth,
                zonedDateTime.monthValue,
                zonedDateTime.year,
                taskId = args.taskDetailId,
            )

            val app = requireActivity().application as ToDoPlannerApplication
            val alarmManager = app.appAlarmManager

            val itemAlarmQueue = ItemAlarmQueue(
                id = notificationId,
                action = targetAction,
                taskId = args.taskDetailId,
                time = zonedDateTime.withHour(5).withMinute(30),
                taskName = args.titleDetail,
                message = description,
                taskDateIdentification = zonedDateTime
            )

            Log.d("DetailFragment - AlarmBroadcastReceiver", "insertMemoAction: $itemAlarmQueue")



            notesViewModel.todo.insert(
                fkNoteTaskId = noteId,
                isChecked = false,
                description = title
            )

            when(targetAction) {
                CATEGORY_QUIZ -> {
                    updateMaterialQuizRecyclerViewData()
                    alarmManager.schedule(itemAlarmQueue)
                }
                CATEGORY_TO_PACK -> {
                    updateToPackRecyclerViewData()
                    alarmManager.schedule(itemAlarmQueue)
                }
            }


        }


    }

    private fun setupRoutineTemplateText(routine: RoutineTable) {
        val routineNameText = routine.title
        binding.textViewRoutineName.text = routineNameText
    }

    private fun setupLocation(taskProvider: TaskAndSessionJoin) {
        if (taskProvider.locationName.isNullOrEmpty()) {
            binding.textViewLocation.visibility = View.GONE
            return
        }

        binding.textViewLocation.text = taskProvider.locationName

        binding.cardViewSessionInfo.setOnClickListener {
            val link = taskProvider.locationAddress ?: return@setOnClickListener

            if(link.contains("maps")) {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Open another app")
                    .setMessage("Do you want to open this link in web browser or goole maps? $link")
                    .setNegativeButton("Yes") { _, _ ->
                        val intentToGoogleMaps = Intent(Intent.ACTION_VIEW, link.toUri())
                        startActivity(intentToGoogleMaps)
                    }
                    .setPositiveButton("No") { _, _ -> }
            }
        }
    }

    private fun setupTime(session: SessionTable) {
        val timeIntervalText = "${session.timeStart} - ${session.timeEnd}"
        binding.textViewSessionTime.text = timeIntervalText
    }

    private fun closeFragment() {
        (requireActivity() as TaskManagementActivity).onSupportNavigateUp()
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]

        val taskFactory = TaskViewModelFactory(app.appRepository)
        taskViewModel = ViewModelProvider(requireActivity(), taskFactory)[TaskViewModel::class.java]

        val notesFactory = NoteViewModelFactory(app.appRepository)
        notesViewModel = ViewModelProvider(requireActivity(), notesFactory)[NoteViewModel::class.java]

        val sessionTaskProviderFactory = SessionTaskProviderViewModelFactory(app.appRepository)
        sessionTaskProviderViewModel = ViewModelProvider(requireActivity(), sessionTaskProviderFactory)[SessionTaskProviderViewModel::class.java]

    }

    private fun setupDate() {
        val date = DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime).toReadable()
        binding.textViewDatetime.text = date
    }

    fun setOnEditTaskAction(): () -> Unit = {
        // Navigate to Task Form
        val destination = DetailFragmentDirections
            .actionDetailFragmentToTaskFragment(
                taskId = args.taskDetailId,
                indexDay = args.indexDay,
                sessionId = args.sessionId,
            )

        navController.navigate(destination)
    }

    fun setOnDeleteTaskAction(): () -> Unit = {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_task_warning_title)
            .setMessage(getString(R.string.delete_task_warning_message_confirmation))
            .setPositiveButton(R.string.cancel) { _, _ -> }
            .setNegativeButton(R.string.delete) { _, _ ->
                lifecycleScope.launch(Dispatchers.Main) {
                    this.launch(Dispatchers.IO) {
                        taskViewModel.deleteById(args.taskDetailId)
                    }
                    closeFragment()
                }
            }
            .show()
    }

    fun setOnResetTaskAction(): () -> Unit =  {
        lifecycleScope.launch(Dispatchers.Main) {
            this.launch(Dispatchers.IO) modelScope@{
                val selectedDate = DatetimeAppManager(args.selectedDatetimeDetailIso.zoneDateTime, true).dateISO8601inString

                val deleteQuiz = async {
                    val quizId = notesViewModel.note.getByFKTaskIdAndCategory(
                        args.taskDetailId,
                        CATEGORY_QUIZ,
                        selectedDate
                    )?.id ?: return@async

                    this.launch(Dispatchers.IO) {
                        notesViewModel.note.deleteById(quizId)
                    }

                    quizMaterialAdapter.submitList(emptyList())
                }

                val deleteToPack = async {
                    val toPackId = notesViewModel.note.getByFKTaskIdAndCategory(
                        args.taskDetailId,
                        CATEGORY_TO_PACK,
                        selectedDate
                    )?.id ?: return@async

                    this.launch(Dispatchers.IO) {
                        notesViewModel.note.deleteById(toPackId)
                    }

                    toPackAdapter.submitList(emptyList())
                }

                val deleteMemo = async {
                    val memoId = notesViewModel.note.getByFKTaskIdAndCategory(
                        args.taskDetailId,
                        CATEGORY_MEMO,
                        selectedDate
                    )?.id ?: return@async

                    this.launch(Dispatchers.IO) {
                        notesViewModel.note.deleteById(memoId)
                    }
                }

                deleteQuiz.start()
                deleteToPack.start()
                deleteMemo.start()
            }
            resetNotesText()
        }
    }

    private fun resetNotesText() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.textViewQuizMaterialDescription.setText("")
            binding.textViewToPackDescription.setText("")
            binding.chipToPack.visibility = View.GONE
            binding.chipQuiz.visibility = View.GONE
            binding.textViewMemoDescription.setText("")
            memoNoteEmptyMode()
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param taskDetailId : Long (default = -1L)
         * @param titleDetail : String (name of the task)
         * @return A new instance of fragment DetailFragment.
         */
        @JvmStatic
        fun newInstance(taskDetailId: Long = -1L, titleDetail: String, selectedDetailDatetimeISO : ParcelableZoneDateTime, setGoTo : String? = null) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DETAIL_ID, taskDetailId)
                    putString(ARG_DETAIL_TITLE, titleDetail)
                    putParcelable(ARG_DETAIL_SELECTED_DATE, selectedDetailDatetimeISO)
                    putString(ARG_DETAIL_SET_GO_TO, setGoTo)
                }
            }
    }


}