package com.example.geoquiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button
    private lateinit var resetButton: Button
    private lateinit var finalScoreText: TextView

    private var isCheater = false
    private var correctAnswersCount = 0

    //lateinit - ввод ненулевого значения

    private val quizViewModel: QuizViewModel by
    lazy {
        ViewModelProviders.of(this)[QuizViewModel::class.java]
    }

    // Лаунчер для запуска CheatActivity и получения результата
    private val cheatActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Проверка, подсмотрел ли пользователь ответ
            isCheater = result.data?.let { CheatActivity.wasAnswerShown(it) } ?: false
            quizViewModel.isCheater = isCheater  // Сохраняем статус в ViewModel
        }
    }

    @SuppressLint("ShowToast")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)
        resetButton = findViewById(R.id.reset_button)
        finalScoreText = findViewById(R.id.final_score_text)


        trueButton.setOnClickListener{
           checkAnswer(true)
        }

        falseButton.setOnClickListener{
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            checkIndexDirection()
            updateQuestion()
        }

        prevButton.setOnClickListener{
            quizViewModel.moveToPrev()
            checkIndexDirection()
            updateQuestion()
        }

        cheatButton.setOnClickListener {
            if (quizViewModel.canUseCheat()) {
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this, answerIsTrue)
                cheatActivityResultLauncher.launch(intent)

                quizViewModel.useCheat() // Увеличиваем счетчик использованных подсказок
            } else{
                Toast.makeText(this, "Вы исчерпали лимит подсказок", Toast.LENGTH_SHORT).show()
            }
        }

        resetButton.setOnClickListener {
            resetQuiz()
        }

        checkIndexDirection()
        updateQuestion()

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart() called")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume() called")
    }
    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause() called")
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        Log.i(TAG,"onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)

        //При обновлении вопроса кнопки снова видимы
        trueButton.visibility = View.VISIBLE
        falseButton.visibility = View.VISIBLE

        if (quizViewModel.currentIndex == quizViewModel.questionBank.size - 1) {
            resetButton.visibility = View.VISIBLE  // Показываем кнопку reset
        }

        //Если подсказки все использованы, то скрываем кнопку
        if (!quizViewModel.canUseCheat()) {
            cheatButton.visibility = View.INVISIBLE
        } else {
            cheatButton.visibility = View.VISIBLE
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        // Изменяем сообщение в зависимости от того, подсмотрел ли пользователь ответ
        val messageResId = when {
            //quizViewModel.isCheater -> R.string.judgment_toast  // Осуждающее сообщение
            userAnswer == correctAnswer -> {
                correctAnswersCount += 1 // Счётчик правильных ответов
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }

        val t = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.BOTTOM,0,500)
        t.show()

        //Кнопки невидимы после ответа на вопрос
        trueButton.visibility = View.INVISIBLE
        falseButton.visibility = View.INVISIBLE

        // На последний вопрос показываем кол-во правильных ответов
        if (quizViewModel.currentIndex == quizViewModel.questionBank.size - 1) {
            showFinalResult()
        }
    }

    //Результат квиза
    private fun showFinalResult() {
        val totalQuestions = quizViewModel.questionBank.size
        val resultMessage = getString(R.string.final_score_text, correctAnswersCount, totalQuestions)

        // Отображаем результат в TextView
        finalScoreText.text = resultMessage
        finalScoreText.visibility = View.VISIBLE
    }

    private fun checkIndexDirection(){
        if (quizViewModel.currentIndex == 0){
            prevButton.isEnabled = false
            prevButton.visibility = View.INVISIBLE
        } else {
            prevButton.isEnabled = true
            prevButton.visibility = View.VISIBLE
        }

        if (quizViewModel.currentIndex == quizViewModel.questionBank.size - 1){
            nextButton.isEnabled = false
            nextButton.visibility = View.INVISIBLE
        } else {
            nextButton.isEnabled = true
            nextButton.visibility = View.VISIBLE
        }
    }

    // Сброс квиза
    private fun resetQuiz() {
        quizViewModel.currentIndex = 0  // Сброс индекса
        correctAnswersCount = 0         // Сброс счётчика правильных ответов
        quizViewModel.isCheater = false  // Сброс состояния "cheat"
        QuizViewModel.cheatCount = 0 // Сброс кол-ва использованных подсказок

        trueButton.visibility = View.VISIBLE
        falseButton.visibility = View.VISIBLE
        resetButton.visibility = View.INVISIBLE
        finalScoreText.visibility = View.INVISIBLE

        checkIndexDirection() // Обновление кнопок prev и next
        updateQuestion()  // Обновление вопроса
    }

}