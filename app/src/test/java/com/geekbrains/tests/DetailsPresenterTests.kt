package com.geekbrains.tests

import com.geekbrains.tests.presenter.details.DetailsPresenter
import com.geekbrains.tests.view.details.ViewDetailsContract
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DetailsPresenterTests {
    private lateinit var detailsPresenter: DetailsPresenter

    @Mock
    private lateinit var viewContract: ViewDetailsContract

    @Before
    fun setUp() {

        MockitoAnnotations.openMocks(this)
        detailsPresenter = DetailsPresenter(ZERO_INT_VALUE)
        detailsPresenter.onAttach(viewContract)
    }

    @Test
    fun onDetach() {
        detailsPresenter.onDetach()
        detailsPresenter.onIncrement()
        verify(viewContract, times(ZERO_INT_VALUE)).setCount(ONE_INT_VALUE)
    }

    @Test
    fun onAttach() {
        detailsPresenter.onDetach()
        detailsPresenter.onAttach(viewContract)
        detailsPresenter.onIncrement()
        verify(viewContract, times(ONE_INT_VALUE)).setCount(ONE_INT_VALUE)
    }

    @Test
    fun onIncrement_Test() {
        detailsPresenter.onIncrement()
        verify(viewContract, times(ONE_INT_VALUE)).setCount(ONE_INT_VALUE)
    }

    @Test
    fun onDecrement_Test() {
        detailsPresenter.onDecrement()
        verify(viewContract, times(ONE_INT_VALUE)).setCount(MINUS_ONE_INT_VALUE)
    }

    @Test
    fun setCounter_Test() {
        detailsPresenter.setCounter(ONE_INT_VALUE)
        detailsPresenter.onIncrement()
        verify(viewContract, times(ONE_INT_VALUE)).setCount(TWO_INT_VALUE)
    }
}