package org.example.lesson07.hw

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf

enum class Color { RED, BLUE, GREEN, YELLOW, BLACK, WHITE }

data class Shape(
    val sideLength: Float,
    val sides: Int,
    val color: Color
)

/** 1) Проверка количества углов (cornerNumber) */
class CornerNumberMatcher(private val expectedCorners: Int) : TypeSafeDiagnosingMatcher<Shape>() {
    override fun describeTo(description: Description) {
        description.appendText("shape with corners=$expectedCorners (derived from sides)")
    }

    override fun matchesSafely(item: Shape, mismatchDescription: Description): Boolean {
        if (item.sides < 0) {
            mismatchDescription.appendText("sides was negative (${item.sides})")
            return false
        }

        val actualCorners = if (item.sides <= 2) 0 else item.sides
        if (actualCorners != expectedCorners) {
            mismatchDescription.appendText("corners was $actualCorners (sides=${item.sides})")
            return false
        }

        return true
    }
}

/** 2) Проверка: отрицательное количество сторон недопустимо */
class NegativeSidesMatcher : TypeSafeDiagnosingMatcher<Shape>() {
    override fun describeTo(description: Description) {
        description.appendText("shape with non-negative sides count")
    }

    override fun matchesSafely(item: Shape, mismatchDescription: Description): Boolean {
        if (item.sides < 0) {
            mismatchDescription.appendText("sides was ${item.sides}")
            return false
        }
        return true
    }
}

/** 3) Проверка: отрицательная длина стороны недопустима */
class NegativeSideLengthMatcher : TypeSafeDiagnosingMatcher<Shape>() {
    override fun describeTo(description: Description) {
        description.appendText("shape with non-negative sideLength")
    }

    override fun matchesSafely(item: Shape, mismatchDescription: Description): Boolean {
        val v = item.sideLength
        if (v.isNaN()) {
            mismatchDescription.appendText("sideLength was NaN")
            return false
        }
        if (v < 0f) {
            mismatchDescription.appendText("sideLength was $v")
            return false
        }
        return true
    }
}

///** 4) Проверка: длина стороны в диапазоне [min..max] */
class SideLengthMatcher(private val min: Float, private val max: Float) : TypeSafeDiagnosingMatcher<Shape>() {
    override fun describeTo(description: Description) {
        description.appendText("shape with sideLength in range [$min..$max]")
    }

    override fun matchesSafely(item: Shape, mismatchDescription: Description): Boolean {
        val v = item.sideLength
        if (v.isNaN()) {
            mismatchDescription.appendText("sideLength was NaN")
            return false
        }
        if (v < min || v > max) {
            mismatchDescription.appendText("sideLength was $v")
            return false
        }
        return true
    }
}

/** 5) Проверка: чётное количество сторон (отрицательное — сразу фейл) */
class EvenSides : TypeSafeDiagnosingMatcher<Shape>() {
    override fun describeTo(description: Description) {
        description.appendText("shape with even sides count")
    }

    override fun matchesSafely(item: Shape, mismatchDescription: Description): Boolean {
        if (item.sides < 0) {
            mismatchDescription.appendText("sides was negative (${item.sides})")
            return false
        }
        if (item.sides % 2 != 0) {
            mismatchDescription.appendText("sides was ${item.sides}")
            return false
        }
        return true
    }
}

/** 6) Проверка цвета */
class ColorMatcher(private val expectedColor: Color) : TypeSafeDiagnosingMatcher<Shape>() {
    override fun describeTo(description: Description) {
        description.appendText("shape with color=$expectedColor")
    }

    override fun matchesSafely(item: Shape, mismatchDescription: Description): Boolean {
        if (item.color != expectedColor) {
            mismatchDescription.appendText("color was ${item.color}")
            return false
        }
        return true
    }
}

/** Функции-обёртки */
fun hasCorners(cornerNumber: Int) = CornerNumberMatcher(cornerNumber)
fun hasValidSides() = NegativeSidesMatcher()
fun hasValidSideLength() = NegativeSideLengthMatcher()
fun hasSideLengthInRange(min: Float, max: Float) = SideLengthMatcher(min, max)
fun hasEvenSides() = EvenSides()
fun hasColor(expectedColor: Color) = ColorMatcher(expectedColor)

/** данные */
val shapes = listOf(
    Shape(10f, 3, Color.RED), Shape(5f, 4, Color.BLUE), Shape(7f, 2, Color.GREEN),
    Shape(0.5f, 1, Color.YELLOW), Shape(-3f, 5, Color.BLACK), Shape(8f, -2, Color.WHITE),
    Shape(12f, 6, Color.RED), Shape(15f, 8, Color.BLUE), Shape(20f, 4, Color.GREEN),
    Shape(9f, 5, Color.YELLOW), Shape(2f, 3, Color.BLACK), Shape(11f, 7, Color.WHITE),
    Shape(6f, 10, Color.RED), Shape(3f, 2, Color.BLUE), Shape(4f, 1, Color.GREEN),
    Shape(25f, 12, Color.YELLOW), Shape(30f, 14, Color.BLACK), Shape(35f, 16, Color.WHITE),
    Shape(40f, 18, Color.RED), Shape(50f, 20, Color.BLUE)
)

// Фильтрация с использованием allOf
val filteredShapes = shapes.filter { shape ->
    allOf(
        hasSideLengthInRange(1f, 20f),
        hasEvenSides(),
        hasValidSideLength(),
        hasValidSides()
    ).matches(shape)
}

// ассерты
fun testMatchers() {
    val shape = Shape(10f, 4, Color.RED)
    assertThat(shape, hasSideLengthInRange(1f, 20f))
    assertThat(shape, hasColor(Color.RED))
    assertThat(shape, hasValidSideLength())
    assertThat(shape, hasValidSides())

    // пример для углов:
    assertThat(Shape(0.5f, 1, Color.YELLOW), hasCorners(0)) // линия -> 0 углов
    assertThat(Shape(10f, 4, Color.RED), hasCorners(4))     // 4 стороны -> 4 угла
}

fun main() {
    testMatchers()
    println("Фигуры, прошедшие фильтрацию: $filteredShapes")
}