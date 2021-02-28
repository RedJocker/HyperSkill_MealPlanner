package mealplanner

import mealplanner.planner.WeekDay
import mealplanner.planner.WeekMealPlan


fun main(args: Array<String>) {
    val dbName = getDatabaseName(args)
    UI.start(dbName)

}

fun getDatabaseName(args: Array<String>) : String {
    val defaultDb = "myDbTest.db"
    val argsDB = args.find { arg -> arg.endsWith(".db") }

    return  argsDB ?: defaultDb
}