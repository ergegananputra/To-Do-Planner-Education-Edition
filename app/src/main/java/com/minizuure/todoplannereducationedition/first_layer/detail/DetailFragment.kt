package com.minizuure.todoplannereducationedition.first_layer.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.minizuure.todoplannereducationedition.model.ParcelableZoneDateTime
import com.minizuure.todoplannereducationedition.recycler.adapter.TodoNotesAdapter
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_MEMO
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_QUIZ
import com.minizuure.todoplannereducationedition.services.database.CATEGORY_TO_PACK
import com.minizuure.todoplannereducationedition.services.database.DEFAULT_NOTE_ID
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModel
import com.minizuure.todoplannereducationedition.services.database.notes.NoteViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionTable
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.task.TaskTable
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModel
import com.minizuure.todoplannereducationedition.services.database.task.TaskViewModelFactory
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_DETAIL_ID = "task_detail_id"
private const val ARG_DETAIL_TITLE = "title_detail"
private const val ARG_DETAIL_SELECTED_DATE = "selected_datetime_detail_iso"

/**
 * Todo List [DetailFragment] :
 *
 *
 * - [ ] Setup tags for task
 * - [ ] Setup rescedule task navigation
 * - [ ] Setup next plan quiz material
 * - [ ] Setup next plan to pack
 * - [ ] Setup memo and connection to database
 * - [ ] Setup bottom more dialog -- reset this task*
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as TaskManagementActivity).setToolbarTitle(this, args.titleDetail)
        navController = Navigation.findNavController(view)
        setupViewModelFactory()

        lifecycleScope.launch {
            val task = taskViewModel.getById(args.taskDetailId) ?: return@launch closeFragment()
            val session = sessionViewModel.getById(task.sessionId) ?: return@launch closeFragment()
            val routine = routineViewModel.getById(session.fkRoutineId) ?: return@launch closeFragment()


            setupDate()
            setupTime(task, session)
            setupLocation(task)
            setupRoutineTemplateText(routine)
            setupQuizMaterial(routine, args.selectedDatetimeDetailIso)
            setupToPack(routine, args.selectedDatetimeDetailIso)

        }
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
                CATEGORY_TO_PACK
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
                currentDate = selectedDatetimeDetailIso.zoneDateTime,
                isNextPlan = false,
                title = getString(R.string.to_pack),
                description = description,
                onClickSaveAction = {isNextPlan, description, title, weekSelected, weeksDictionary ->
                    insertMemoAction(CATEGORY_TO_PACK,isNextPlan, description, title, weekSelected, weeksDictionary)
                }
            )

            bottomSheet.show(parentFragmentManager, "TaskDetailBottomSheetDialogFragment")
        }


        lifecycleScope.launch {
            val toPackNote = notesViewModel.note.getByFKTaskIdAndCategory(
                args.taskDetailId,
                CATEGORY_TO_PACK
            )

            toPackNote?.let {
                binding.textViewToPackDescription.text = it.description
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
                CATEGORY_QUIZ
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
                currentDate = currentDate.zoneDateTime,
                isNextPlan = false,
                title = getString(R.string.quiz_materials_title),
                description = currDesc,
                onClickSaveAction = { isNextPlan, description, title, weekSelected, weeksDictionary ->
                    insertMemoAction(CATEGORY_QUIZ, isNextPlan, description, title, weekSelected, weeksDictionary)
                }
            )

            bottomSheet.show(parentFragmentManager, "TaskDetailBottomSheetDialogFragment")
        }


        lifecycleScope.launch {
            val quizNote = notesViewModel.note.getByFKTaskIdAndCategory(
                args.taskDetailId,
                CATEGORY_QUIZ
            )

            quizNote?.let {
                binding.textViewQuizMaterialDescription.text = it.description
            }
        }
    }


    /**
     * Insert memo action
     *
     *
     * Next plan menggunakan fungsi ini juga
     *
     *
     * @param targetAction : String ([CATEGORY_QUIZ], [CATEGORY_TO_PACK], [CATEGORY_MEMO])
     * @param isNextPlan : Boolean
     * @param description : String
     * @param title : String?
     * @param weekSelected : Int
     * @param weeksDictionary : Map<"days name" : String, [weekSelected] : Int>
     *     - key : String
     *
     */
    private fun insertMemoAction(
        targetAction : String,
        isNextPlan: Boolean,
        description: String,
        title: String?,
        weekSelected: Int,
        weeksDictionary: Map<String, Int>
    ) {
        lifecycleScope.launch {
            // get note id
            var noteId = withContext(Dispatchers.IO) {
                notesViewModel.note.getByFKTaskIdAndCategory(args.taskDetailId, targetAction)?.id
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
                val zoneDateTime = args.selectedDatetimeDetailIso.zoneDateTime
                val date = DatetimeAppManager(zoneDateTime, true).dateISO8601inString
                noteId = withContext(Dispatchers.IO) {
                    notesViewModel.note.insert(
                        fkTaskId = args.taskDetailId,
                        category = targetAction,
                        description = description,
                        date = date
                    )
                }
            }


            when(targetAction) {
                CATEGORY_QUIZ -> {
                    binding.textViewQuizMaterialDescription.text = description
                }
                CATEGORY_TO_PACK -> {
                    binding.textViewToPackDescription.text = description
                }
                CATEGORY_MEMO -> {
                    binding.textViewMemoDescription.setText(description)
                }
            }

            // TODO : Set recycler view for memo

            if (noteId == DEFAULT_NOTE_ID || title == null) return@launch

            // Quiz and To Pack only

            notesViewModel.todo.insert(
                fkNoteTaskId = noteId,
                isChecked = false,
                description = title
            )

            when(targetAction) {
                CATEGORY_QUIZ -> {
                    updateMaterialQuizRecyclerViewData()
                }
                CATEGORY_TO_PACK -> {
                    updateToPackRecyclerViewData()
                }
            }


        }


    }

    private fun setupRoutineTemplateText(routine: RoutineTable) {
        val routineNameText = routine.title
        binding.textViewRoutineName.text = routineNameText
    }

    private fun setupLocation(task: TaskTable) {
        if (task.locationName == null) return

        binding.textViewLocation.text = task.locationName!!

        binding.cardViewSessionInfo.setOnClickListener {
            val link = task.locationAddress ?: return@setOnClickListener

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

    private fun setupTime(task: TaskTable, session: SessionTable) {
        val timeIntervalText = if (task.isCustomSession) {
            "${task.startTime} - ${task.endTime}"
        } else {
            "${session.timeStart} - ${session.timeEnd}"
        }

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
        fun newInstance(taskDetailId: Long = -1L, titleDetail: String, selectedDetailDatetimeISO : ParcelableZoneDateTime) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DETAIL_ID, taskDetailId)
                    putString(ARG_DETAIL_TITLE, titleDetail)
                    putParcelable(ARG_DETAIL_SELECTED_DATE, selectedDetailDatetimeISO)
                }
            }
    }


}