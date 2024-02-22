package com.example.calculatorpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val zeroButton = findViewById<Button>(R.id.zero_Button)
        val oneButton = findViewById<Button>(R.id.one_Button)
        val twoButton = findViewById<Button>(R.id.two_Button)
        val threeButton = findViewById<Button>(R.id.three_Button)
        val fourButton = findViewById<Button>(R.id.four_Button)
        val fiveButton = findViewById<Button>(R.id.five_Button)
        val sixButton = findViewById<Button>(R.id.six_Button)
        val sevenButton = findViewById<Button>(R.id.seven_Button)
        val eightButton = findViewById<Button>(R.id.eight_Button)
        val nineButton = findViewById<Button>(R.id.nine_Button)


        val equalButton = findViewById<Button>(R.id.equal_Button)
        val pointButton = findViewById<Button>(R.id.point_Button)
        val plusButton = findViewById<Button>(R.id.plus_Button)
        val minusButton = findViewById<Button>(R.id.minus_Button)
        val multipleButton = findViewById<Button>(R.id.multiple_Button)

        val resultTextView = findViewById<TextView>(R.id.textView)

        val numberStringBuilder = StringBuilder()

        oneButton.setOnClickListener {
            numberStringBuilder.append(1)
            resultTextView.text = numberStringBuilder

        }
        plusButton.setOnClickListener {
            numberStringBuilder.append("+")
            resultTextView.text = numberStringBuilder

        }
        equalButton.setOnClickListener {
            val result = evaluate(numberStringBuilder.toString())
            resultTextView.text = result.toInt().toString()
            numberStringBuilder.clear()

        }



    }

    fun evaluate(str: String): Double {
        data class Data(val rest: List<Char>, val value: Double)

        return object : Any() {
            fun parse(chars: List<Char>): Double {

                return getExpression(chars.filter { it != ' ' })
                    .also {
                        if (it.rest.isNotEmpty())
                            throw RuntimeException("Unexpected character: ${it.rest.first()}")
                    }
                    .value
            }

            private fun getExpression(chars: List<Char>): Data {
                var (rest, carry) = getTerm(chars)
                while (true) {
                    when {
                        rest.firstOrNull() == '+' -> rest =
                            getTerm(rest.drop(1)).also { carry += it.value }.rest

                        rest.firstOrNull() == '-' -> rest =
                            getTerm(rest.drop(1)).also { carry -= it.value }.rest

                        else -> return Data(rest, carry)
                    }
                }
            }

            private fun getTerm(chars: List<Char>): Data {
                var (rest, carry) = getFactor(chars)
                while (true) {
                    when {
                        rest.firstOrNull() == '*' -> rest =
                            getTerm(rest.drop(1)).also { carry *= it.value }.rest

                        rest.firstOrNull() == '/' -> rest =
                            getTerm(rest.drop(1)).also { carry /= it.value }.rest

                        else -> return Data(rest, carry)
                    }
                }
            }

            private fun getFactor(chars: List<Char>): Data {
                return when (val char = chars.firstOrNull()) {
                    '+' -> getFactor(chars.drop(1)).let { Data(it.rest, +it.value) }
                    '-' -> getFactor(chars.drop(1)).let { Data(it.rest, -it.value) }
                    '(' -> getParenthesizedExpression(chars.drop(1))
                    in '0'..'9', ',' -> getNumber(chars)
                    else -> throw RuntimeException("Unexpected character: $char")
                }
            }

            private fun getParenthesizedExpression(chars: List<Char>): Data {
                return getExpression(chars)
                    .also { if (it.rest.firstOrNull() != ')') throw RuntimeException("Missing closing parenthesis") }
                    .let { Data(it.rest.drop(1), it.value) }
            }

            private fun getNumber(chars: List<Char>): Data {
                val s = chars.takeWhile { it.isDigit() || it == '.' }.joinToString("")
                return Data(chars.drop(s.length), s.toDouble())
            }
        }.parse(str.toList())
    }



}