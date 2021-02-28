package mealplanner.MealPlannertask

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.internal.copyOnWriteList
import kotlin.Any
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.MutableList
import kotlin.jvm.JvmField
import kotlin.reflect.KClass
import mealdb.data.CreateTablesQueries
import mealdb.data.IngredientsDao
import mealdb.data.IngredientsDaoQueries
import mealdb.data.MealDao
import mealdb.data.MealDaoQueries
import mealplanner.Database

internal val KClass<Database>.schema: SqlDriver.Schema
  get() = DatabaseImpl.Schema

internal fun KClass<Database>.newInstance(driver: SqlDriver): Database = DatabaseImpl(driver)

private class DatabaseImpl(
  driver: SqlDriver
) : TransacterImpl(driver), Database {
  override val createTablesQueries: CreateTablesQueriesImpl = CreateTablesQueriesImpl(this, driver)

  override val ingredientsDaoQueries: IngredientsDaoQueriesImpl = IngredientsDaoQueriesImpl(this,
      driver)

  override val mealDaoQueries: MealDaoQueriesImpl = MealDaoQueriesImpl(this, driver)

  object Schema : SqlDriver.Schema {
    override val version: Int
      get() = 1

    override fun create(driver: SqlDriver) {
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS mealDao (
          |  meal_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |  category TEXT NOT NULL,
          |  meal TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS ingredientsDao (
          |  ingredient_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |  ingredient TEXT NOT NULL,
          |  meal_id INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ) {
    }
  }
}

private class IngredientsDaoQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), IngredientsDaoQueries {
  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  internal val allIngredientsWithMealId: MutableList<Query<*>> = copyOnWriteList()

  override fun <T : Any> selectAll(mapper: (
    ingredient_id: Long,
    ingredient: String,
    meal_id: Long
  ) -> T): Query<T> = Query(-971048060, selectAll, driver, "IngredientsDao.sq", "selectAll", """
  |SELECT *
  |FROM ingredientsDao
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!
    )
  }

  override fun selectAll(): Query<IngredientsDao> = selectAll { ingredient_id, ingredient,
      meal_id ->
    mealdb.data.IngredientsDao(
      ingredient_id,
      ingredient,
      meal_id
    )
  }

  override fun <T : Any> allIngredientsWithMealId(meal_id: Long, mapper: (
    ingredient_id: Long,
    ingredient: String,
    meal_id: Long
  ) -> T): Query<T> = AllIngredientsWithMealIdQuery(meal_id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getLong(2)!!
    )
  }

  override fun allIngredientsWithMealId(meal_id: Long): Query<IngredientsDao> =
      allIngredientsWithMealId(meal_id) { ingredient_id, ingredient, meal_id ->
    mealdb.data.IngredientsDao(
      ingredient_id,
      ingredient,
      meal_id
    )
  }

  override fun insert(ingredient: String, meal_id: Long) {
    driver.execute(1271302810, """
    |INSERT INTO ingredientsDao(ingredient, meal_id)
    |VALUES (?, ?)
    """.trimMargin(), 2) {
      bindString(1, ingredient)
      bindLong(2, meal_id)
    }
    notifyQueries(1271302810, {database.ingredientsDaoQueries.selectAll +
        database.ingredientsDaoQueries.allIngredientsWithMealId})
  }

  private inner class AllIngredientsWithMealIdQuery<out T : Any>(
    @JvmField
    val meal_id: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(allIngredientsWithMealId, mapper) {
    override fun execute(): SqlCursor = driver.executeQuery(1579977094, """
    |SELECT *
    |FROM ingredientsDao
    |WHERE meal_id = (?)
    """.trimMargin(), 1) {
      bindLong(1, meal_id)
    }

    override fun toString(): String = "IngredientsDao.sq:allIngredientsWithMealId"
  }
}

private class CreateTablesQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), CreateTablesQueries

private class MealDaoQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), MealDaoQueries {
  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  internal val findIdByName: MutableList<Query<*>> = copyOnWriteList()

  internal val findMealDao: MutableList<Query<*>> = copyOnWriteList()

  override fun <T : Any> selectAll(mapper: (
    meal_id: Long,
    category: String,
    meal: String
  ) -> T): Query<T> = Query(151761259, selectAll, driver, "MealDao.sq", "selectAll", """
  |SELECT *
  |FROM mealDao
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!
    )
  }

  override fun selectAll(): Query<MealDao> = selectAll { meal_id, category, meal ->
    mealdb.data.MealDao(
      meal_id,
      category,
      meal
    )
  }

  override fun findIdByName(meal: String): Query<Long> = FindIdByNameQuery(meal) { cursor ->
    cursor.getLong(0)!!
  }

  override fun <T : Any> findMealDao(
    meal: String,
    category: String,
    mapper: (
      meal_id: Long,
      category: String,
      meal: String
    ) -> T
  ): Query<T> = FindMealDaoQuery(meal, category) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!
    )
  }

  override fun findMealDao(meal: String, category: String): Query<MealDao> = findMealDao(meal,
      category) { meal_id, category, meal ->
    mealdb.data.MealDao(
      meal_id,
      category,
      meal
    )
  }

  override fun insert(category: String, meal: String) {
    driver.execute(-1024133613, """
    |INSERT INTO mealDao(category, meal)
    |VALUES (?, ?)
    """.trimMargin(), 2) {
      bindString(1, category)
      bindString(2, meal)
    }
    notifyQueries(-1024133613, {database.mealDaoQueries.selectAll +
        database.mealDaoQueries.findIdByName + database.mealDaoQueries.findMealDao})
  }

  private inner class FindIdByNameQuery<out T : Any>(
    @JvmField
    val meal: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(findIdByName, mapper) {
    override fun execute(): SqlCursor = driver.executeQuery(313837872, """
    |SELECT meal_id FROM mealDao
    |WHERE meal = (?)
    """.trimMargin(), 1) {
      bindString(1, meal)
    }

    override fun toString(): String = "MealDao.sq:findIdByName"
  }

  private inner class FindMealDaoQuery<out T : Any>(
    @JvmField
    val meal: String,
    @JvmField
    val category: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(findMealDao, mapper) {
    override fun execute(): SqlCursor = driver.executeQuery(1400242620, """
    |SELECT * FROM mealDao
    |WHERE meal = (?) AND category = (?)
    """.trimMargin(), 2) {
      bindString(1, meal)
      bindString(2, category)
    }

    override fun toString(): String = "MealDao.sq:findMealDao"
  }
}
