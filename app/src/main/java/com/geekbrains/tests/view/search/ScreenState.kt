package com.geekbrains.tests.view.search

import com.geekbrains.tests.model.SearchResponse


sealed class ScreenState {
    object Loading : ScreenState()
    data class Working(val searchResponse: SearchResponse) : ScreenState()
    data class Error(val error: Throwable) : ScreenState()
}