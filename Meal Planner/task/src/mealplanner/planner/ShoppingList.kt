package mealplanner.planner

object ShoppingList {

    var weekMealPlan:WeekMealPlan? = null;


    fun hasWeekMealPlan():Boolean {
        return weekMealPlan != null
    }

    override fun toString(): String {
        return weekMealPlan?.getIngredientsList()?.joinToString("\n")
        ?: "no plan"
    }
}