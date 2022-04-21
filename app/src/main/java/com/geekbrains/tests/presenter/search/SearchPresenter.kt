package com.geekbrains.tests.presenter.search

import com.geekbrains.tests.model.Constants.Companion.NULL_RESULT_TEXT
import com.geekbrains.tests.model.Constants.Companion.UNSUCCESSFUL_RESULT_TEXT
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.presenter.SchedulerProvider
import com.geekbrains.tests.repository.RepositoryCallback
import com.geekbrains.tests.view.ViewContract
import com.geekbrains.tests.view.search.ViewSearchContract
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import retrofit2.Response

/**
 * В архитектуре MVP все запросы на получение данных адресуются в Репозиторий.
 * Запросы могут проходить через Interactor или UseCase, использовать источники
 * данных (DataSource), но суть от этого не меняется.
 * Непосредственно Презентер отвечает за управление потоками запросов и ответов,
 * выступая в роли регулировщика движения на перекрестке.
 */

internal class SearchPresenter internal constructor(
    private val repository: RepositoryContract,
    private val appSchedulerProvider: SchedulerProvider = SearchSchedulerProvider()
) : PresenterSearchContract, RepositoryCallback {
    private var viewContract: ViewSearchContract? = null

    override fun onAttach(viewContract: ViewContract) {
        this.viewContract = viewContract as ViewSearchContract
    }

    override fun onDetach() {
        viewContract = null
    }

    override fun searchGitHub(searchQuery: String) {
        viewContract?.let { viewContract ->
            val compositeDisposable = CompositeDisposable()
            compositeDisposable.add(
                repository.searchGithub(searchQuery)
                    .subscribeOn(appSchedulerProvider.io())
                    .observeOn(appSchedulerProvider.ui())
                    .doOnSubscribe { viewContract.displayLoading(true) }
                    .doOnTerminate { viewContract.displayLoading(false) }
                    .subscribeWith(object : DisposableObserver<SearchResponse>() {
                        override fun onNext(searchResponse: SearchResponse) {
                            val searchResults = searchResponse.searchResults
                            val totalCount = searchResponse.totalCount
                            if (searchResults != null && totalCount != null) {
                                viewContract.displaySearchResults(
                                    searchResults,
                                    totalCount
                                )
                            } else {
                                viewContract
                                    .displayError(NULL_RESULT_TEXT)
                            }
                        }

                        override fun onError(e: Throwable) {
                            viewContract
                                .displayError(e.message ?: UNSUCCESSFUL_RESULT_TEXT)
                        }

                        override fun onComplete() {}
                    }
                    )
            )
        }
    }

    override fun handleGitHubResponse(response: Response<SearchResponse?>?) {
        viewContract?.let { viewContract ->
            viewContract.displayLoading(false)
            if (response != null && response.isSuccessful) {
                val searchResponse = response.body()
                val searchResults = searchResponse?.searchResults
                val totalCount = searchResponse?.totalCount
                if (searchResults != null && totalCount != null) {
                    viewContract.displaySearchResults(
                        searchResults,
                        totalCount
                    )
                } else {
                    viewContract.displayError(NULL_RESULT_TEXT)
                }
            } else {
                viewContract.displayError(UNSUCCESSFUL_RESULT_TEXT)
            }
        }
    }

    override fun handleGitHubError() {
        viewContract?.let { viewContract ->
            viewContract.displayLoading(false)
            viewContract.displayError()
        }
    }
}
