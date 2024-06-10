package com.app.pills.model

import android.util.Log
import com.app.pills.R

enum class UnitType(val text: String, val drawableResId: Int) {
    AMPULES("ампулы", R.drawable.ampule),
    UNITS("единицы", R.drawable.insulune),
    CAPSULES("капсулы", R.drawable.capsule),
    SACHETS("саше", R.drawable.sashe),
    TABLETS("таблетки", R.drawable.tablet),
    PIECES("штуки", R.drawable.backet_default),
    GRAMS("граммы", R.drawable.add_pill),
    MILLIGRAMS("миллиграммы", R.drawable.backet_default),
    MILLILITERS("миллилитры", R.drawable.ml);

    companion object {
        fun fromText(text: String): UnitType? {
            return values().find { it.text.equals(text, ignoreCase = true) }
        }

        val list = listOf(
                "ампулы",
                "единицы",
                "капсулы",
                "саше",
                "таблетки",
                "штуки",
                "граммы",
                "миллиграммы",
                "миллилитры"
            )

        private val ruleTable = listOf(
            listOf("ампула", "ампул", "ампулы"),
            listOf("единица", "eдиниц", "единицы"),
            listOf("капсула", "капсул", "капсулы"),
            listOf("саше", "саше", "саше"),
            listOf("таблетка", "таблеток", "таблетки"),
            listOf("штука", "штук", "штуки"),
            listOf("г", "г", "г"),
            listOf("мг", "мг", "мг"),
            listOf("мл", "мл", "мл"),
            listOf("лекарство", "лекарств", "лекарства")
        )

        fun rule(name: String, amount: Int): String {
            var line = list.indexOf(name)
            Log.d("RULE:","$name $line")
            if (line == -1) line = ruleTable.lastIndex
            Log.d("Rule", line.toString())
            return when (amount % 10) {
                1 -> ruleTable[line][0]
                2, 3, 4 -> ruleTable[line][2]
                else -> ruleTable[line][1]
            }
        }
    }
}