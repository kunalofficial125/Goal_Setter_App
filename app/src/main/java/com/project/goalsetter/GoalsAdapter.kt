package com.project.goalsetter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GoalsAdapter(private val context: Context, private val dataList: ArrayList<GoalDataModel>) : RecyclerView.Adapter<GoalsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val descTextView : TextView = itemView.findViewById(R.id.desc)
        val startDate: TextView = itemView.findViewById(R.id.startDate)
        val endDate: TextView = itemView.findViewById(R.id.endDate)
        //val aboutImageView = itemView.findViewById<ImageView>(R.id.aboutImgView)
        val goalProgress: ProgressBar = itemView.findViewById(R.id.goalProgress)
        val markCompleteText: TextView = itemView.findViewById(R.id.markCompletedTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.goal_recycler_layout,parent,false);
        return ViewHolder(view);
    }

    override fun onBindViewHolder(holder: GoalsAdapter.ViewHolder, position: Int) {

        var data: GoalDataModel = dataList[position]
        holder.titleTextView.text = data.title
        holder.descTextView.text = data.description

        val dateEnd = Date(data.endDate)
        val dateStart = Date(data.startDate)
        val locale = Locale.ENGLISH
        val format = SimpleDateFormat("dd-MMM-yy",locale)
        val endDateFormatted = format.format(dateEnd)
        val startDateFormatted = format.format(dateStart)
        val msg = "Completed"

        holder.startDate.text = startDateFormatted
        holder.endDate.text = endDateFormatted

        // Get current time from System
        val currentTimeMillis: Long = System.currentTimeMillis()

        // Calculate the total duration ->  end time - start time
        val totalDuration: Long = data.endDate - data.startDate

        // Calculate elapsed time ->  current time - start time
        val elapsedTime: Long = currentTimeMillis - data.startDate

        // Calculate the progress as a percentage
        val progressPercentage: Int = ((elapsedTime.toDouble() / totalDuration) * 100).toInt()

        Log.d("ProgressPerc","${progressPercentage.toString() +" "+data.title} ")


        Log.d("completeStatus",data.completed.toString())

        // Updating Progress Bar
        if(data.completed==1 || elapsedTime>totalDuration){
            //Set Progress to 100
            holder.goalProgress.progress = holder.goalProgress.max
            holder.markCompleteText.text = msg
            holder.markCompleteText.isEnabled = false
        }
        else{
            holder.goalProgress.progress = progressPercentage
            holder.markCompleteText.setOnClickListener{
                data.completed = 1  // Update the goal's completion status in the local data list
                notifyItemChanged(position)
                updateUser(data.id)

            }
        }



    }

    private fun updateUser(id: String){
        val firestoreDB = FirebaseFirestore.getInstance()
        firestoreDB.collection("User")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firestoreDB.collection("User").document(document.id).update("completed",1).addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            Toast.makeText(context,"Congratulations!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context,"FireStore Error!", Toast.LENGTH_SHORT).show();
            }
    }


    override fun getItemCount(): Int {
        return dataList.size;
    }
}