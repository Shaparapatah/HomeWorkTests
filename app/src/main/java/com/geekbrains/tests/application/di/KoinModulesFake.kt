package com.geekbrains.tests.application.di

import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.repository.FakeGitHubRepository
import org.koin.dsl.module

val application = module {
    single<RepositoryContract> {
        FakeGitHubRepository()
    }
}