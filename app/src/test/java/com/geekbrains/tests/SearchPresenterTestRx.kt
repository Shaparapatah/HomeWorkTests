package com.geekbrains.tests

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.search.ScheduleProviderStub
import com.geekbrains.tests.presenter.search.SearchPresenter
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.view.search.ViewSearchContract
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class SearchPresenterTestRx {

    private lateinit var presenter: SearchPresenter

    @Mock
    private lateinit var repository: GitHubRepository

    @Mock
    private lateinit var viewContract: ViewSearchContract


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = SearchPresenter(repository, ScheduleProviderStub())
        presenter.onAttach(viewContract)
    }

    @Test
    fun searchGitHub_Test() {
        Mockito.`when`(repository.searchGithub(SOME_QUERY_TEXT)).thenReturn(
            Observable.just(
                SearchResponse(
                    1,
                    listOf()
                )
            )
        )
        presenter.searchGitHub(SOME_QUERY_TEXT)
        Mockito.verify(repository, Mockito.times(1))
            .searchGithub(SOME_QUERY_TEXT)
    }

    @Test
    fun handleRequestError_Test() {
        Mockito.`when`(repository.searchGithub(SOME_QUERY_TEXT)).thenReturn(
            Observable.error(Throwable(ERROR_TEXT))
        )
        presenter.searchGitHub(SOME_QUERY_TEXT)
        Mockito.verify(viewContract, Mockito.times(1))
            .displayError(ERROR_TEXT)
    }

    @Test
    fun handleResponseError_TotalCountIsNull() {
        Mockito.`when`(repository.searchGithub(SOME_QUERY_TEXT)).thenReturn(
            Observable.just(
                SearchResponse(
                    null,
                    listOf()
                )
            )
        )
        presenter.searchGitHub(SOME_QUERY_TEXT)
        Mockito.verify(viewContract, Mockito.times(1))
            .displayError(NULL_RESULT_TEXT)
    }

    @Test
    fun handleResponseError_TotalCountIsNull_ViewContractMethodOrder() {
        Mockito.`when`(repository.searchGithub(SOME_QUERY_TEXT)).thenReturn(
            Observable.just(
                SearchResponse(
                    null,
                    listOf()
                )
            )
        )
        presenter.searchGitHub(SOME_QUERY_TEXT)
        val inOrder = Mockito.inOrder(viewContract)
        inOrder.verify(viewContract).displayLoading(true)
        inOrder.verify(viewContract).displayError(NULL_RESULT_TEXT)
        inOrder.verify(viewContract).displayLoading(false)
    }

    @Test
    fun handleResponseSuccess() {
        Mockito.`when`(repository.searchGithub(SOME_QUERY_TEXT)).thenReturn(
            Observable.just(
                SearchResponse(
                    42,
                    listOf()
                )
            )
        )
        presenter.searchGitHub(SOME_QUERY_TEXT)
        Mockito.verify(viewContract, Mockito.times(1))
            .displaySearchResults(listOf(), 42)
    }
}
