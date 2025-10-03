package com.example.basicscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorScreen()
        }
    }
}

@Composable
fun CalculatorScreen() {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    var output by remember { mutableStateOf("") }
    var basicMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C))
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                textStyle = TextStyle(
                    fontSize = 32.sp,
                    color = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Text(
                text = output,
                fontSize = 40.sp,
                color = Color.Green,
                modifier = Modifier.padding(4.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalculatorButton("C", Color(0xFFFF6B6B)) {
                    if (input.text.isNotEmpty() && input.selection.start > 0) {
                        val pos = input.selection.start
                        val newT = input.text.removeRange(pos - 1, pos)
                        input = TextFieldValue(newT, TextRange(pos - 1))
                    }
                }
                CalculatorButton("( )", Color.Gray) {
                    val pos = input.selection.start
                    val newT = input.text.substring(0, pos) + "()" + input.text.substring(pos)
                    input = TextFieldValue(newT, TextRange(pos + 1))
                }
                CalculatorButton(if (basicMode) "Func" else "123", Color.Gray) {
                    basicMode = !basicMode
                }
                CalculatorButton("/", Color(0xFFFF9800)) {
                    insertAtCursor("/", input) { input = it }
                }
            }

            if (basicMode) {
                val basicButtons = listOf(
                    listOf("7", "8", "9", "*"),
                    listOf("4", "5", "6", "+"),
                    listOf("1", "2", "3", "-"),
                    listOf("AC", "0", ".", "=")
                )
                for (row in basicButtons) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (btn in row) {
                            CalculatorButton(btn) {
                                when (btn) {
                                    "AC" -> {
                                        input = TextFieldValue("")
                                        output = ""
                                    }
                                    "=" -> {
                                        try {
                                            output = calculateExpression(input.text).toString()
                                        } catch (e: Exception) {
                                            output = "Error"
                                        }
                                    }
                                    else -> {
                                        insertAtCursor(btn, input) { input = it }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val sciButtons = listOf(
                    listOf("1/x", "x!", "x^y", "√"),
                    listOf("sin", "cos", "tan", ""),
                    listOf("asin", "acos", "atan", ""),
                    listOf("AC", "log", "ln", "=")
                )
                for (row in sciButtons) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (btn in row) {
                            if (btn.isNotEmpty()) {
                                CalculatorButton(btn) {
                                    when (btn) {
                                        "AC" -> {
                                            input = TextFieldValue("")
                                            output = ""
                                        }
                                        "=" -> {
                                            try {
                                                output = calculateExpression(input.text).toString()
                                            } catch (e: Exception) {
                                                output = "Error"
                                            }
                                        }
                                        else -> {
                                            val addText = when (btn) {
                                                "√" -> "sqrt()"
                                                "x^y" -> "^"
                                                "1/x" -> "1/()"
                                                "x!" -> "!"
                                                "ln" -> "ln()"
                                                "log" -> "log()"
                                                "sin" -> "sin()"
                                                "cos" -> "cos()"
                                                "tan" -> "tan()"
                                                "asin" -> "asin()"
                                                "acos" -> "acos()"
                                                "atan" -> "atan()"
                                                else -> btn
                                            }
                                            insertAtCursor(addText, input) { newInput ->
                                                if (addText.endsWith("()")) {
                                                    input = newInput.copy(
                                                        selection = TextRange(newInput.selection.start - 1)
                                                    )
                                                } else {
                                                    input = newInput
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.size(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
fun calculateExpression(expr: String): Double {
    var e = expr.replace(" ", "")

    if (e.contains("!")) {
        val num = e.replace("!", "").toInt()
        var res = 1
        for (i in 1..num) res *= i
        return res.toDouble()
    }
    if (e.startsWith("sqrt(")) {
        val inside = e.removePrefix("sqrt(").removeSuffix(")")
        return sqrt(calculateExpression(inside))
    }
    if (e.contains("^")) {
        val parts = e.split("^")
        return calculateExpression(parts[0]).pow(calculateExpression(parts[1]))
    }
    if (e.startsWith("1/(")) {
        val inside = e.removePrefix("1/(").removeSuffix(")")
        return 1.0 / calculateExpression(inside)
    }
    if (e.contains("+")) {
        val p = e.split("+")
        return calculateExpression(p[0]) + calculateExpression(p[1])
    }
    if (e.contains("-")) {
        val p = e.split("-")
        if (p[0].isEmpty()) return -calculateExpression(p[1])
        return calculateExpression(p[0]) - calculateExpression(p[1])
    }
    if (e.contains("*")) {
        val p = e.split("*")
        return calculateExpression(p[0]) * calculateExpression(p[1])
    }
    if (e.contains("/")) {
        val p = e.split("/")
        return calculateExpression(p[0]) / calculateExpression(p[1])
    }
    if (e.startsWith("log(")) {
        val inside = e.removePrefix("log(").removeSuffix(")")
        return ln(calculateExpression(inside)) / ln(10.0)
    }
    if (e.startsWith("ln(")) {
        val inside = e.removePrefix("ln(").removeSuffix(")")
        return ln(calculateExpression(inside))
    }
    if (e.startsWith("sin(")) {
        val inside = e.removePrefix("sin(").removeSuffix(")")
        return sin(calculateExpression(inside))
    }
    if (e.startsWith("cos(")) {
        val inside = e.removePrefix("cos(").removeSuffix(")")
        return cos(calculateExpression(inside))
    }
    if (e.startsWith("tan(")) {
        val inside = e.removePrefix("tan(").removeSuffix(")")
        return tan(calculateExpression(inside))
    }
    if (e.startsWith("asin(")) {
        val inside = e.removePrefix("asin(").removeSuffix(")")
        return asin(calculateExpression(inside))
    }
    if (e.startsWith("acos(")) {
        val inside = e.removePrefix("acos(").removeSuffix(")")
        return acos(calculateExpression(inside))
    }
    if (e.startsWith("atan(")) {
        val inside = e.removePrefix("atan(").removeSuffix(")")
        return atan(calculateExpression(inside))
    }

    return e.toDoubleOrNull() ?: 0.0
}

@Composable
fun CalculatorButton(symbol: String, color: Color? = null, onClick: () -> Unit) {
    val buttonColor = color ?: when (symbol) {
        "C", "AC" -> Color(0xFFFF6B6B)
        "/", "*", "+", "-", "=" -> Color(0xFFFF9800)
        "( )", "Func", "123" -> Color.Gray
        else -> Color(0xFF00897B)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .background(buttonColor, shape = CircleShape)
            .clickable { onClick() }
    ) {
        Text(
            text = symbol,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

fun insertAtCursor(txt: String, input: TextFieldValue, update: (TextFieldValue) -> Unit) {
    val pos = input.selection.start
    val newT = input.text.substring(0, pos) + txt + input.text.substring(pos)
    update(input.copy(text = newT, selection = TextRange(pos + txt.length)))
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CalculatorPreview() {
    CalculatorScreen()
}
