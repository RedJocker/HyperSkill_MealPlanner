package mealplanner

import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver
import mealdb.data.CreateTablesQueries
import mealdb.data.IngredientsDaoQueries
import mealdb.data.MealDaoQueries
import mealplanner.MealPlannertask.newInstance
import mealplanner.MealPlannertask.schema

interface Database : Transacter {
  val createTablesQueries: CreateTablesQueries

  val ingredientsDaoQueries: IngredientsDaoQueries

  val mealDaoQueries: MealDaoQueries

  companion object {
    val Schema: SqlDriver.Schema
      get() = Database::class.schema

    operator fun invoke(driver: SqlDriver): Database = Database::class.newInstance(driver)}
}
