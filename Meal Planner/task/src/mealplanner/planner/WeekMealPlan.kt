package mealplanner.planner


import mealplanner.Meal


class WeekMealPlan( val weekPlanMap: Map<WeekDay, Map<Meal.Category, Meal>>) {

    override fun toString(): String {
        return weekPlanMap.mapTo(mutableListOf<String>(), { (weekday, daysMeals) ->
            val daysMealsString = daysMeals.mapTo(mutableListOf<String>(), { (category, meal) ->
                "${category.type.capitalize()}: ${meal.name.toLowerCase()}"
            }).joinToString("\n",  )

            "${weekday.toString().toLowerCase().capitalize()}\n$daysMealsString\n"

        }).joinToString("\n" ,)

    }

    fun getIngredientsList() : List<String> {
        return weekPlanMap.flatMap { (weekday, mapMeals) ->
            mapMeals.flatMap { (category, meal)  ->
                meal.ingredients
            }
        }.groupBy { ingredient :String -> ingredient
        }.map { (ingredient, listRepeatedIngredient) ->
            val countIngredient = listRepeatedIngredient.size
            val countIngredientString = if(countIngredient > 1)
                                            " x$countIngredient"
                                            else ""
            "$ingredient$countIngredientString"
        }
    }
}