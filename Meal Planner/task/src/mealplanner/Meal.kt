package mealplanner

class Meal(val category: Category, val name: String, val ingredients: List<String>) {


    override fun toString(): String {

        return "Category: $category\n" +
                "Name: $name\n" +
                ingredients.joinToString(
                        "\n", "Ingredients: \n"
                )
    }



    enum class Category(val type: String) {
        BREAKFAST("breakfast"), LUNCH("lunch"), DINNER("dinner");


        override fun toString(): String {
            return type
        }

        companion object {
            val categoriesString: String = Meal.Category.values()
                    .map(Meal.Category::type)
                    .joinToString(", ")

            fun fromName(name: String): Category? {
                return values().firstOrNull { it.type == name }
            }
        }
    }

    object Validator {
        fun validateWord(word: String): Boolean {
            return word.all { char -> char.isLetter() || char == ' ' }
        }
    }
}