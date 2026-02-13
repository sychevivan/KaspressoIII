package org.example.lesson04

import kotlin.random.Random

// 1. Доступ к элементу по индексу ([ ]) и проверка наличия через in
class Inventory(initialItems: List<String> = emptyList()) {

    val items = initialItems.toMutableList()

    operator fun plus(item: String) {
        items.add(item)
    }

    operator fun get(i: Int): String {
        return items[i]
    }

    operator fun contains(item: String): Boolean {
        return items.contains(item)
    }
}

// 2. Инверсия состояния (!)
class Toggle(val enabled: Boolean) {

    operator fun not(): Toggle {
        return Toggle(!enabled)
    }
}

// 3. Умножение значения (*)
class Price(val amount: Int) {
    operator fun times(other: Int): Int {
        return (amount * other)
    }
}

// 4. Диапазон значений (..)
class Step(val number: Int) {
    operator fun rangeTo(step: Step): IntRange {
        return this.number.rangeTo(step.number)
    }

//    operator fun contains(other: IntRange): Boolean {
//        return this.number in other
//    }
}

operator fun IntRange.contains(other: Step): Boolean {
    return this.contains(other.number)
}

// 5. Последовательное объединение (+)
class Log(val entries: List<String>) {
    operator fun plus(other: Log): Log {
        return Log(entries + other.entries)
    }
}

// 6. Генератор фраз.
class Person(private val name: String) {

    private val phrases = mutableListOf<String>()

    fun print() {
        println(phrases.joinToString(" "))
    }

    private fun selectPhrase(first: String, second: String): String {
        val random = Random.nextInt(0, 2)
        return if (random == 0) first else second
    }

    infix fun says(other: String): Person {
        this.phrases += other
        return this
    }

    infix fun and(other: String): Person {
        if (phrases.isEmpty()) {
            throw IllegalStateException("and должен идти после says")
        }
        this.phrases += other
        return this
    }

    infix fun or(other: String): Person {
        if (phrases.isEmpty()) {
            throw IllegalStateException("or должен идти после says")
        }
        val lastPhrase = this.phrases.last()
        val randomPhrase = selectPhrase(other, lastPhrase)
        this.phrases[this.phrases.lastIndex] = randomPhrase
        return this
    }
}

fun main() {
    val list = listOf("one", "two", "three", "four")

    // Доступ к элементу по индексу ([ ]) и проверка наличия через in
    val inventory = Inventory(list)
    inventory + "five"
    println(inventory.items)

    println(inventory[2])

    println("three" in inventory)

    // 2. Инверсия состояния (!)
    println(!Toggle(true).enabled)

    // 3. Умножение значения (*)
    val price = Price(10)
    println(price * 3)

    // 4. Диапазон значений (..)
    val step1 = Step(1)
    val step2 = Step(5)
    println(step1..step2)

    val step3 = Step(3)
    println(step3 in step1..step2)

    // 5. Последовательное объединение (+)
    println((Log(list) + Log(list)).entries)

    // 6. Генератор фраз.
    val andrew = Person("Andrew")
    andrew says "Hello" and "brothers." or "sisters." and "I believe" and "you" and "can do it" or "can't"
    andrew.print()
}