package com.minizuure.todoplannereducationedition.first_layer.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.minizuure.todoplannereducationedition.R
import com.minizuure.todoplannereducationedition.databinding.FragmentDetailBinding
import com.minizuure.todoplannereducationedition.first_layer.TaskManagementActivity

private const val ARG_DETAIL_ID = "com.minizuure.todoplannereducationedition.first_layer.task.detail"
private const val ARG_DETAIL_TITLE = "com.minizuure.todoplannereducationedition.first_layer.task.title"
class DetailFragment : Fragment() {
    val args : DetailFragmentArgs by navArgs()
    private var detailId: Long? = null
    private var detailTitle: String? = null

    private val binding by lazy {
        FragmentDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            detailId = it.getLong(ARG_DETAIL_ID)
            detailTitle = it.getString(ARG_DETAIL_TITLE)
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
        (activity as TaskManagementActivity).setToolbarTitle(this, args.title)

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param detailId : Long (default = -1L)
         * @param detailTitle : String (name of the task)
         * @return A new instance of fragment DetailFragment.
         */
        @JvmStatic
        fun newInstance(detailId: Long = -1L, detailTitle: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DETAIL_ID, detailId)
                    putString(ARG_DETAIL_TITLE, detailTitle)
                }
            }
    }


}