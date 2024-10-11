package com.example.geoquiz

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel

//ViewModel хранит все данные,
//связанные с потребностями экрана, форматирует их и облегчает доступ

private const val TAG = "QuizViewModel"
class QuizViewModel: ViewModel() {
    var currentIndex = 0
    var isCheater = false  // Добавляем флаг для отслеживания, подсмотрел ли пользователь

    companion object {
        var cheatCount: Int = 0 // Счетчик использованных подсказок
    }
    //var cheatCount = 0 // Счетчик использованных подсказок
    private val maxCheatCount = 3  // Максимальное количество подсказок

    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans,true),
        Question(R.string.question_mideast,false),
        Question(R.string.question_africa,false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia,true))

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
        isCheater = false  // Сбрасываем статус после переключения на новый вопрос
    }

    fun moveToPrev() {
        currentIndex = (currentIndex - 1) % questionBank.size
        isCheater = false  // Сбрасываем статус после переключения на новый вопрос
    }

    fun canUseCheat(): Boolean {
        return cheatCount < maxCheatCount
    }

    fun useCheat() {
        if (canUseCheat()) {
            cheatCount+=1
        }
    }



}