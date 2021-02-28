package mealdb.data

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String

interface MealDaoQueries : Transacter {
  fun <T : Any> selectAll(mapper: (
    meal_id: Long,
    category: String,
    meal: String
  ) -> T): Query<T>

  fun selectAll(): Query<MealDao>

  fun findIdByName(meal: String): Query<Long>

  fun <T : Any> findMealDao(
    meal: String,
    category: String,
    mapper: (
      meal_id: Long,
      category: String,
      meal: String
    ) -> T
  ): Query<T>

  fun findMealDao(meal: String, category: String): Query<MealDao>

  fun insert(category: String, meal: String)
}
