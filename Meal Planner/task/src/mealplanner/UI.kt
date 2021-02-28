package mealplanner

import mealplanner.database.DbService
import mealplanner.file.FileService
import mealplanner.planner.ShoppingList
import mealplanner.planner.WeekDay
import mealplanner.planner.WeekMealPlan


object UI {

    private val commandMenuString = "What would you like to do (${Command.commandsString})?"
    private val addMenuString = "Which meal do you want to add (${Meal.Category.categoriesString})?"
    private val printMenuString = "Which category do you want to print? (${Meal.Category.categoriesString})?"
    private val invalidCategory = "Wrong meal category! Choose from: ${Meal.Category.categoriesString}."
    private const val invalidMeal = "This meal doesn’t exist. Choose a meal from the list above."
    private const val invalidFormat = "Wrong format. Use letters only!"
    private const val invalidSaveShoppingList = "Save isn't allowed. Plan your meals first."
    private const val invalidFileName = "File name invalid shopping list was not saved"
    private const val queryFileName = "Write a filename:"
    private const val queryMealsName = "Meal's name:"
    private const val queryIngredients = "Ingredients:"
    private const val addSucceeded = "Meal added!"
    private const val noMealsFound = "No meals found."
    private const val invalidCommand = "Invalid command, try again"
    private const val saved  = "Saved correctly!"
    private const val bye = "Bye!"
    private const val invalidStringDefault = "1"

    private var dbService: DbService? = null


    fun start(dbName: String) {
        dbService = DbService.getDbServiceInstance(dbName)
        dbService?.initMenu() ?:  throw error("could not init database, dbService was null")
        generateSequence { Command.NEXT }.forEach(Command::execute)

    }

    private fun addMealFromInput(){
        println(addMenuString)
        val category = queryMealCategory()

        println(queryMealsName)
        val name = queryMealName()

        println(queryIngredients)
        val ingredients = queryMealIngredients()

        val createdMeal = Meal(category, name, ingredients)
        Menu.addMeal(createdMeal)
        dbService?.insertMeal(createdMeal) ?: throw error("failed to add meal from input, dbService was null")
        println(addSucceeded)


    }

    private fun queryMealCategory(): Meal.Category {
        //println(addMenuString)
        val category = Meal.Category.fromName(
                readLine() ?: invalidStringDefault
        )

        if(category == null) {
            println(invalidCategory)
            return queryMealCategory()
        }
        return category
    }

    private fun queryMealName(): String {
        //println(queryMealsName)
        val name = readLine() ?: invalidStringDefault
        if(!Meal.Validator.validateWord(name)) {
            println(invalidFormat)
            return queryMealName()
        }
        return name
    }

    private fun queryMealIngredients(): List<String> {
        //println(queryIngredients)
        val ingredients:List<String> = (readLine() ?: invalidStringDefault).split(", ")
                .map(String::trim)
                .toList()
        if(ingredients.any { ingredient -> !Meal.Validator.validateWord(ingredient) }) {
            println(invalidFormat)
            return queryMealIngredients()

        }
        return ingredients
    }

    private fun printMenu() {
        println(printMenuString)
        val category = queryMealCategory()
        val meals  = Menu.getMeals(category)

        meals.apply{
            if(isEmpty())
                println(noMealsFound)
                else meals.forEach(::println)
        }
    }

    private fun nextCommand() {
        println(commandMenuString)
        val commandStr = readLine() ?: invalidStringDefault
        Command.fromName(commandStr).execute()

    }

    private fun tryAgain() {
        println(invalidCommand)

    }

    private fun getPlanFromInput() {
        val plan = WeekDay.values().fold( mutableMapOf<WeekDay, Map<Meal.Category, Meal>>(),
                { map, weekDay ->
                    val weekDayPlan = getWeekDayPlan(weekDay)
                    map[weekDay] = weekDayPlan
                    map
                },
        )

        ShoppingList.weekMealPlan = WeekMealPlan(plan)
        println(ShoppingList.weekMealPlan)

    }

    private fun getWeekDayPlan(weekDay: WeekDay): Map<Meal.Category, Meal> {
        println(weekDay)
        return Meal.Category.values().fold(mutableMapOf<Meal.Category, Meal>(),
                { map, category ->
                    printMeals(category)
                    println("Choose $category for $weekDay from the list above:")
                    val meal = getValidMeal(category)
                    println("Yeah! We planned the meals for $weekDay.")

                    map[category] = meal
                    map
                }

        )
    }

    private fun getValidMeal(category: Meal.Category): Meal{

        val inputMealName = readLine() ?: invalidStringDefault
        if(!Menu.contains(category, inputMealName)) {
            println(invalidMeal)
            return getValidMeal(category)
        }

        return Menu.getMeal(category, inputMealName)!!


    }

    private fun printMeals(category: Meal.Category) {
        Menu.getMeals(category).forEach { meal -> println(meal.name) }
    }

    private fun save() {
        println(queryFileName)
        val fileName = getFileName()

        if(!ShoppingList.hasWeekMealPlan()) {
            println(invalidSaveShoppingList)
        } else {
            if (fileName != null) {
                FileService.writeShoppingList(fileName, ShoppingList.toString())
                println(saved)
            } else {
                println(invalidFileName)
            }
        }
    }

    private fun getFileName(): String? {
        return readLine()
    }

    enum class Command(private val runnable: Runnable) {
        ADD(UI::addMealFromInput), SHOW(UI::printMenu),
        NEXT(UI::nextCommand), TRY_AGAIN(UI::tryAgain),
        PLAN(UI::getPlanFromInput), SAVE(UI::save),
        EXIT( {  ->
            dbService?.close() ?: throw error("unable to close dbService, dbService was null")
            println(bye)
            kotlin.system.exitProcess(0) }
        );


        fun execute()   {
            runnable.run()
        }

        companion object {

            val commandsString = values()
                    .filter { it !in listOf(TRY_AGAIN, NEXT)}
                    .joinToString(", ") { it.name.toLowerCase() }

            fun fromName(name: String): Command {
                return Command.values().firstOrNull(){ it.name.equals(name, ignoreCase = true)} ?: TRY_AGAIN
            }
        }
    }
}