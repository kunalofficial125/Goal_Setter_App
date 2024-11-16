package com.project.goalsetter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * A simple [Fragment] subclass.
 * Use the [AllGoals.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllGoals : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private val firebaseDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_all_goals, container, false)
        val parent = activity
        val noGoalsText = v.findViewById<TextView>(R.id.noGoalsText)
        val goalRecView : RecyclerView = v.findViewById(R.id.goalRecView)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val parentContext: Context = parent?.applicationContext?: requireContext()
        val dataList = ArrayList<GoalDataModel>();
        val goalAdapter = GoalsAdapter(parentContext, dataList)



        //Get Data From Database
        firebaseDB.collection("User").addSnapshotListener{ result, error ->
            if(error==null){
                if (result != null) {
                    dataList.clear()
                    dataList.addAll(result.toObjects(GoalDataModel::class.java) as ArrayList<GoalDataModel>) //result.toObjects(GoalDataModel::class.java) as ArrayList<GoalDataModel>

                    if(dataList.isEmpty()){
                        noGoalsText.visibility = View.VISIBLE
                    }

                    goalAdapter.notifyDataSetChanged()
                }
            }
            else{
                Toast.makeText(context,"Network Problem !",Toast.LENGTH_SHORT).show();
            }
        }

        //Applying Data to Adapter
        goalRecView.layoutManager = layoutManager
        goalRecView.adapter = goalAdapter

        return v;
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllGoals.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllGoals().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}