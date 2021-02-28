package mealplanner.planner

enum class WeekDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    override fun toString(): String {
        return this.name.toLowerCase().capitalize()
    }


}