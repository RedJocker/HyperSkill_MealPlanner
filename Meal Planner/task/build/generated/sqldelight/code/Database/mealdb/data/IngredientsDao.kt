package mealdb.data

import kotlin.Long
import kotlin.String

data class IngredientsDao(
  val ingredient_id: Long,
  val ingredient: String,
  val meal_id: Long
) {
  override fun toString(): String = """
  |IngredientsDao [
  |  ingredient_id: $ingredient_id
  |  ingredient: $ingredient
  |  meal_id: $meal_id
  |]
  """.trimMargin()
}
