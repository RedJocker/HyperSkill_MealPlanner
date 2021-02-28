package mealdb.data

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String

interface IngredientsDaoQueries : Transacter {
  fun <T : Any> selectAll(mapper: (
    ingredient_id: Long,
    ingredient: String,
    meal_id: Long
  ) -> T): Query<T>

  fun selectAll(): Query<IngredientsDao>

  fun <T : Any> allIngredientsWithMealId(meal_id: Long, mapper: (
    ingredient_id: Long,
    ingredient: String,
    meal_id: Long
  ) -> T): Query<T>

  fun allIngredientsWithMealId(meal_id: Long): Query<IngredientsDao>

  fun insert(ingredient: String, meal_id: Long)
}
