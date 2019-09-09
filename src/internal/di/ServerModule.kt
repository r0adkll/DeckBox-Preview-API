package com.deckboxtcg.internal.di

import com.deckboxtcg.data.DataSource
import com.deckboxtcg.data.JsonDataSource
import org.koin.dsl.module

val appModule = module {

    single<DataSource> { JsonDataSource("sm12.json") }
}