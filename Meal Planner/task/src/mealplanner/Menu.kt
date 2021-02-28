package mealplanner


object Menu {

    private val meals: MutableList<Meal> = mutableListOf()

    fun addMeal(meal: Meal) {
        meals.add(meal)
    }

    fun getMeals(): List<Meal> {
        return meals
    }

    fun getMeals(category: Meal.Category): List<Meal> {
        return meals.filter { meal -> meal.category == category }

    }

    fun getMealSortedByCategory(): List<Meal> {
        return meals.sortedBy { meal -> meal.category }
    }

    fun contains(category: Meal.Category , mealName: String): Boolean {
        return meals.filter { meal -> meal.category == category }
                .any { meal -> mealName == meal.name }
    }

    fun getMeal(category: Meal.Category , mealName: String): Meal? {
        return meals.filter { meal -> meal.category == category }
                .firstOrNull() { meal -> mealName == meal.name }
    }
}