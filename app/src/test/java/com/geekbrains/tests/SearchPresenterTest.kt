package com.geekbrains.tests

import com.geekbrains.tests.presenter.search.SearchPresenter
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.view.search.ViewSearchContract
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SearchPresenterTests {
    private lateinit var searchPresenter: SearchPresenter

    @Mock
    private lateinit var repository: GitHubRepository

    @Mock
    private lateinit var viewContract: ViewSearchContract


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        searchPresenter = SearchPresenter(repository)
        searchPresenter.onAttach(viewContract)
    }

    @Test
    fun searchGitHub_Test() {
        val searchQuery = SOME_QUERY_TEXT
        searchPresenter.searchGitHub(SOME_QUERY_TEXT)
        com.nhaarman.mockito_kotlin.verify(repository, com.nhaarman.mockito_kotlin.times(1))
            .searchGithub(searchQuery, searchPresenter)
    }

    @Test
    fun handleGitHubError_Test() {

        searchPresenter.handleGitHubError()
        verify(viewContract, times(ONE_INT_VALUE)).displayError()
    }

    @Test
    fun onDetach_Test() {
        searchPresenter.onDetach()
        searchPresenter.handleGitHubError()
        verify(viewContract, times(ZERO_INT_VALUE)).displayError()
    }

    @Test
    fun onAttach_Test() {
        searchPresenter.onDetach()
        searchPresenter.onAttach(viewContract)
        searchPresenter.handleGitHubError()
        verify(viewContract, times(ONE_INT_VALUE)).displayError()
    }
}
