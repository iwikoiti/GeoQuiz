package com.example.geoquiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var questionTextView: TextView
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans,true),
        Question(R.string.question_mideast,false),
        Question(R.string.question_africa,false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia,true))
    private var currentIndex = 0

    //lateinit - ввод ненулевого значения

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)


        trueButton.setOnClickListener{
           checkAnswer(true)
        }

        falseButton.setOnClickListener{
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            checkIndexDirection()
            updateQuestion()
        }

        prevButton.setOnClickListener{
            currentIndex = abs(currentIndex - 1) % questionBank.size
            checkIndexDirection()
            updateQuestion()
        }

        updateQuestion()

    }
    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionBank[currentIndex].answer
        val messageResId =
            if (userAnswer == correctAnswer) {
                R.string.correct_toast
            } else {
                R.string.incorrect_toast
            }
        val t = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.BOTTOM,0,500)
        t.show()
    }

    private fun checkIndexDirection(){
        if (currentIndex == 0){
            prevButton.visibility = View.INVISIBLE
        } else {
            prevButton.visibility = View.VISIBLE
        }

        if (currentIndex == questionBank.size - 1){
            nextButton.visibility = View.INVISIBLE
        } else{
            nextButton.visibility = View.VISIBLE
        }
    }

}