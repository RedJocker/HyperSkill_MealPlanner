package mealdb.data

import kotlin.Long
import kotlin.String

data class MealDao(
  val meal_id: Long,
  val category: String,
  val meal: String
) {
  override fun toString(): String = """
  |MealDao [
  |  meal_id: $meal_id
  |  category: $category
  |  meal: $meal
  |]
  """.trimMargin()
}
