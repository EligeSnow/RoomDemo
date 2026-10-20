package com.example.roomdemo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel для управления состоянием UI и связывания пользовательского интерфейса с репозиторием данных.
 *
 * @param application Экземпляр приложения для доступа к контексту при создании базы данных.
 */
class MainViewModel(application: Application) : ViewModel() {

    /** LiveData со списком всех товаров из базы данных. */
    val allProducts: LiveData<List<Product>>

    /** Ссылка на репозиторий для выполнения бизнес-логики и работы с БД. */
    private val repository: ProductRepository

    /** LiveData с результатами последнего поиска товаров. */
    val searchResults: MutableLiveData<List<Product>>

    init {
        // Инициализация базы данных и DAO
        val productDb = ProductRoomDatabase.getInstance(application)
        val productDao = productDb.productDao()

        // Создание репозитория и привязка источников данных
        repository = ProductRepository(productDao)

        allProducts = repository.allProducts
        searchResults = repository.searchResults
    }

    /** Передача запроса на вставку нового товара в репозиторий. */
    fun insertProduct(product: Product) {
        repository.insertProduct(product)
    }

    /** Передача запроса на поиск товара по названию в репозиторий. */
    fun findProduct(name: String) {
        repository.findProduct(name)
    }

    /** Передача запроса на удаление товара по названию в репозиторий. */
    fun deleteProduct(name: String) {
        repository.deleteProduct(name)
    }
}
