package com.minizuure.todoplannereducationedition.first_layer.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentDetailBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity
import com.minizuure.todoplannereducationedition.model.ParcelableZoneDateTime
import com.minizuure.todoplannereducationedition.services.datetime.DatetimeAppManager

private const val ARG_DETAIL_ID = "task_detail_id"
private const val ARG_DETAIL_TITLE = "title_detail"
private const val ARG_DETAIL_SELECTED_DATE = "selected_datetime_detail_iso"
class DetailFragment : Fragment() {
    val args : DetailFragmentArgs by navArgs()
    private lateinit var navController: NavController

    private val binding by lazy {
        FragmentDetailBinding.inflate(layoutInflater)
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

        setupDate()
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