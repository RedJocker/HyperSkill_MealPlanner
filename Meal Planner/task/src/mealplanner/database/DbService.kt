package mealplanner.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import mealdb.data.IngredientsDaoQueries
import mealdb.data.MealDao
import mealdb.data.MealDaoQueries
import mealplanner.Database
import mealplanner.Meal
import mealplanner.Menu


class DbService private constructor(private val databaseName: String) {

    private val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$databaseName")

    init {
        Database.Schema.create(driver)
    }

    private val database = Database(driver)
    private val mealDaoQueries: MealDaoQueries = database.mealDaoQueries
    private val ingredientsDaoQueries: IngredientsDaoQueries = database.ingredientsDaoQueries


    fun insertIngredient(ingredient: String, meal: Meal) {

        val mealDao = mealDaoQueries.findMealDao(meal.name, meal.category.type)
                .executeAsOneOrNull()
        mealDao?.also { dao ->
            ingredientsDaoQueries.insert(ingredient, dao.meal_id)
        }
    }

    fun insertIngredient(ingredient: String, mealDao: MealDao) {
        ingredientsDaoQueries.insert(ingredient, mealDao.meal_id)
    }

    fun insertMeal(meal: Meal) {
        val foundMealDao = mealDaoQueries.findMealDao(meal.name, meal.category.type)
                .executeAsOneOrNull()

        if (foundMealDao == null) {
            mealDaoQueries.insert(meal.category.type, meal.name)
            val createdMealDao = mealDaoQueries.findMealDao(meal.name, meal.category.type)
                    .executeAsOne()
            meal.ingredients.forEach { ingred ->
                insertIngredient(ingred, createdMealDao)
            }
        } else {
            meal.ingredients.forEach { ingred ->
                insertIngredient(ingred, foundMealDao)
            }
        }
    }

    fun initMenu() {
        val listMealDao = mealDaoQueries.selectAll().executeAsList()
        listMealDao.forEach { mealDao ->
            val ingredientsList =
                    ingredientsDaoQueries.allIngredientsWithMealId(mealDao.meal_id)
                            .executeAsList()
                            .map { ingredientsDao -> ingredientsDao.ingredient }

            val category = Meal.Category.fromName(mealDao.category)
                    ?: throw error("initMenu error, invalid meal category stored on database")

            val meal = Meal(category, mealDao.meal, ingredientsList)

            Menu.addMeal(meal);
        }

    }

    fun close() {
        driver.close()
    }

    companion object {
        var singletonInstance:  DbService? = null;


        fun getDbServiceInstance(dbName: String): DbService {
            if (singletonInstance == null) {
                singletonInstance  =  DbService(dbName)
                return singletonInstance as DbService
            }

            return singletonInstance as DbService
        }
    }
}