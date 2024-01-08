package com.minizuure.todoplannereducationedition.first_layer.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.ToDoPlannerApplication
import com.minizuure.todoplannereducationedition.databinding.FragmentTaskBinding
import com.minizuure.todoplannereducationedition.dialog_modal.GlobalBottomSheetDialogFragment
import com.minizuure.todoplannereducationedition.dialog_modal.GlobalBottomSheetDialogFragment.Companion.isDialogOpen
import com.minizuure.todoplannereducationedition.dialog_modal.adapter.GlobalAdapter
import com.minizuure.todoplannereducationedition.dialog_modal.model_interfaces.GlobalMinimumInterface
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.services.customtextfield.CustomTextField
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineTable
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModel
import com.minizuure.todoplannereducationedition.services.database.routine.RoutineViewModelFactory
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModel
import com.minizuure.todoplannereducationedition.services.database.session.SessionViewModelFactory
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager
import com.minizuure.todoplannereducationedition.services.errormsgs.ErrorMsgManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_TASK_ID = "com.minizuure.todoplannereducationedition.first_layer.task.taskId"
class TaskFragment : Fragment() {
    val args : TaskFragmentArgs by navArgs()
    private var taskId: Long? = null
    private lateinit var routineViewModel : RoutineViewModel
    private lateinit var sessionViewModel: SessionViewModel


    private val binding by lazy {
        FragmentTaskBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getLong(ARG_TASK_ID)
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
        (activity as TaskManagementActivity).setToolbarTitle(this)
        setupViewModelFactory()

        setupDropdownTextField()
        setupTimePicker()
        setupCommunitySwitch()
        setupErrorMessages()
    }

    private fun setupViewModelFactory() {
        val app : ToDoPlannerApplication = (requireActivity().application as ToDoPlannerApplication)

        val routineFactory = RoutineViewModelFactory(app.appRepository)
        routineViewModel = ViewModelProvider(requireActivity(), routineFactory)[RoutineViewModel::class.java]

        val sessionFactory = SessionViewModelFactory(app.appRepository)
        sessionViewModel = ViewModelProvider(requireActivity(), sessionFactory)[SessionViewModel::class.java]
    }

    private fun setupCommunitySwitch() {
        // TODO: setup community switch
    }

    private fun setupDropdownTextField() {
        CustomTextField(requireContext()).setDropdownTextField(
            binding.textInputLayoutRoutineTemplate,
            onClick = spinnerRoutineTemplateOnClick()
        )

        CustomTextField(requireContext()).setDropdownTextField(
            binding.textInputLayoutSelectDay,
            onClick = spinnerSelectDayOnClick()
        )

        CustomTextField(requireContext()).setDropdownTextField(
            binding.textInputLayoutSelectSession,
            onClick = spinnerSelectSessionOnClick()
        )
    }

    private fun spinnerSelectSessionOnClick(): () -> Unit = {
        // TODO: open bottom sheet dialog and show all available sessions
    }

    private fun spinnerSelectDayOnClick(): () -> Unit = {
        // TODO: open bottom sheet dialog and show all available days
    }

    private fun spinnerRoutineTemplateOnClick(): () -> Unit = {
        lifecycleScope.launch(Dispatchers.Main) {
            val routines = withContext(Dispatchers.IO) {
                routineViewModel.getAll()
            }

            val routineTemplateAdapter = GlobalAdapter(
                useIndexes = true,
                onClickAction = onClickRoutineItem()
            )

            routineTemplateAdapter.submitList(routines)

            val bottomSheet = GlobalBottomSheetDialogFragment(
                title = getString(R.string.default_select_routines_template),
                globalAdapter = routineTemplateAdapter
            )

            if (!isDialogOpen) {
                bottomSheet.show(parentFragmentManager, "routine_template_bottom_sheet")
            }
        }
    }

    private fun onClickRoutineItem(): (GlobalMinimumInterface) -> Unit = {
        if (it is RoutineTable) {
            binding.textInputLayoutRoutineTemplate.editText?.setText(it.title)
        } else {
            Log.wtf("TaskFragment", "onClickRoutineItem: it is not RoutineTemplateImpl")
        }
    }

    private fun setupTimePicker() {
        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutStartTime
        )

        DatetimeAppManager().setEditTextTimePickerDialog(
            requireContext(),
            parentFragmentManager,
            binding.textInputLayoutEndTime
        )
    }

    private fun setupErrorMessages() {
        ErrorMsgManager().setErrorMessages(
            requireContext(),
            binding.teksInputLayoutLocation,
            binding.teksInputLayoutLocation.editText,
            "20 characters max"
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param taskId : Long (default = -1L)
         * @return A new instance of fragment TaskFragment.
         */
        @JvmStatic
        fun newInstance(taskId: Long = -1L) =
            TaskFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TASK_ID, taskId)
                }
            }
    }

}