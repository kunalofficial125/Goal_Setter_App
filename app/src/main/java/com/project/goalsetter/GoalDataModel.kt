package com.project.goalsetter

class GoalDataModel(val id: String, val title: String, val description: String, val startDate:Long,
                    val endDate: Long, var completed: Int ) {

    constructor() : this("","","",0L,0L,0) {
    }
}