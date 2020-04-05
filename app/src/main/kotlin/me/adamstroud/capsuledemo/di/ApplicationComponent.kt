package me.adamstroud.capsuledemo.di

import dagger.Component
import me.adamstroud.capsuledemo.articlelist.ArticleListFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(target: ArticleListFragment.ViewModel)
}