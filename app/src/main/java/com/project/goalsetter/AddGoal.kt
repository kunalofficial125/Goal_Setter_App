package com.project.goalsetter

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddGoal.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddGoal : Fragment() {
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



    private val firestoreDB:FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_add_goal, container, false)

        val titleEditText = v.findViewById<EditText>(R.id.titleEditText)
        val descEditText = v.findViewById<EditText>(R.id.descEditText)
        val targetTextView = v.findViewById<TextView>(R.id.targetDateTextView)
        val setGoalBtn = v.findViewById<Button>(R.id.setGoalBtn)
        val calendarView = v.findViewById<CalendarView>(R.id.calendarView)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        calendarView.visibility = View.GONE
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)

        val currentMillis: Long = System.currentTimeMillis()
        var targetDateMillis : Long = 0

        // Calculate tomorrow's date in milliseconds
        val calendar = Calendar.getInstance()

        // Move to tomorrow
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val minDateMillis = calendar.timeInMillis

        calendarView.minDate = minDateMillis


        calendarView.setOnDateChangeListener{ _ ,year,month,day ->

            targetDateMillis = getDateAt10AMMillis(year,month,day)
            val chosenDate: String = ""+day+"/"+(month+1)+"/"+year
            targetTextView.text = chosenDate
            Toast.makeText(context, targetDateMillis.toString(), Toast.LENGTH_SHORT).show()
            calendarView.visibility = View.GONE
        }

        targetTextView.setOnClickListener{
            calendarView.visibility = View.VISIBLE
            Toast.makeText(context, "Calendar opened", Toast.LENGTH_SHORT).show()
        }

        setGoalBtn.setOnClickListener{

            val title: String = titleEditText.text.toString()
            val description: String = descEditText.text.toString()


            val randNum = Random.nextInt(123,1000000)

            val model = GoalDataModel("Goal$randNum",title, description, currentMillis, targetDateMillis, 0)

            if(title!="" && description!="" && targetDateMillis!=0L){
                firestoreDB.collection("User").document().set(model).addOnCompleteListener{ task ->

                    if(task.isSuccessful){
                        val operationTime = targetDateMillis - (4*60*60*1000)
                        val deadlineTime = targetDateMillis - (1*60*60*1000)
                        intent.putExtra("title",titleEditText.text.toString())
                        val pendingIntent = PendingIntent.getBroadcast(context,100, intent,PendingIntent.FLAG_IMMUTABLE)
                        alarmManager.set(AlarmManager.RTC_WAKEUP, operationTime, pendingIntent)
                        alarmManager.set(AlarmManager.RTC_WAKEUP, deadlineTime, pendingIntent)
                        goalCreatedNotification()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    else{
                        Toast.makeText(context,"Network Problem",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(context,"Please fill all details",Toast.LENGTH_SHORT).show();
            }

        }

        return v
    }


    private fun getDateAt10AMMillis(year: Int, month: Int, dayOfMonth: Int): Long {

        val calendar = Calendar.getInstance();

        calendar.set(year, month, dayOfMonth)

        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)


        return calendar.timeInMillis
    }

    private fun goalCreatedNotification(){

        val CHANNEL_ID = "GOAL_CREATED"

        val notificationManager: NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val notification: Notification;

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            notification = Notification.Builder(context,CHANNEL_ID)
                .setContentText("New Goal Created")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setSubText("Your new goal just created.")
                .setChannelId(CHANNEL_ID)
                .build()
            notificationManager.createNotificationChannels(
                listOf(
                    NotificationChannel(CHANNEL_ID,"AlertUser",
                        NotificationManager.IMPORTANCE_DEFAULT)
                ))

        }
        else{
            notification = Notification.Builder(context)
                .setContentTitle("New Goal Created")
                .setSubText("Your new goal just created.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()
        }

        notificationManager.notify(1,notification)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddGoal.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddGoal().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}