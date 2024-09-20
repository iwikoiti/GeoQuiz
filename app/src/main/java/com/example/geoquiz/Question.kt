package com.example.geoquiz

import androidx.annotation.StringRes //аннотация, помогающая встроенному инспектору кода
// проверить правильность строкового идентификатора ресурса

data class Question (@StringRes val textResId: Int, val answer: Boolean)