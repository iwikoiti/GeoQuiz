package com.example.geoquiz

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.geoquiz.ui.theme.GeoQuizTheme

private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"
private const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_shown"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var  versionTextView: TextView

    private var answerIsTrue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cheat)

        versionTextView = findViewById(R.id.version_text_view)

        // Получаем версию Android
        val androidVersion = "Android Version: ${Build.VERSION.RELEASE} (API Level: ${Build.VERSION.SDK_INT})"

        // Отображаем версию в TextView
        versionTextView.text = androidVersion


        // Получаем ответ из интента
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener {
            // Показываем ответ пользователю
            val answerText = when {
                answerIsTrue ->
                    R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)

            // Устанавливаем результат, что ответ был показан
            setAnswerShownResult(true)
        }
    }

         fun setAnswerShownResult(isAnswerShown: Boolean) {
            val data = Intent().apply {
                putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
            }
             // Устанавливаем результат для передачи обратно в MainActivity
            setResult(RESULT_OK, data)
        }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }

        // Метод для проверки, был ли показан ответ
        fun wasAnswerShown(result: Intent?): Boolean {
            return result?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
}
