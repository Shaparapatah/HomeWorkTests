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


class SearchPresenterTest {

    private lateinit var presenter: SearchPresenter

    @Mock
    private lateinit var repository: GitHubRepository

    @Mock
    private lateinit var viewContract: ViewSearchContract

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        presenter = SearchPresenter(repository)
        presenter.onAttach(viewContract)
    }

    @Test
    fun searchGitHub_Test() {
        val searchQuery = SOME_QUERY_TEXT
        presenter.searchGitHub(SOME_QUERY_TEXT)
        com.nhaarman.mockito_kotlin.verify(repository, com.nhaarman.mockito_kotlin.times(1))
            .searchGithub(searchQuery, presenter)
    }

    @Test
    fun handleGitHubError_Test() {

        presenter.handleGitHubError()
        verify(viewContract, times(ONE_INT_VALUE)).displayError()
    }

    @Test
    fun onDetach_Test() {
        presenter.onDetach()
        presenter.handleGitHubError()
        verify(viewContract, times(ZERO_INT_VALUE)).displayError()
    }

    @Test
    fun onAttach_Test() {
        presenter.onDetach()
        presenter.onAttach(viewContract)
        presenter.handleGitHubError()
        verify(viewContract, times(ONE_INT_VALUE)).displayError()
    }
}