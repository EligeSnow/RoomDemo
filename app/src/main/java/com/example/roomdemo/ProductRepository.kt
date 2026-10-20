package com.example.roomdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Класс репозитория для абстрагирования источника данных (базы данных Room) от ViewModel.
 * Управляет выполнением асинхронных операций с БД через Kotlin Coroutines.
 *
 * @property productDao Интерфейс доступа к данным DAO.
 */
class ProductRepository(private val productDao: ProductDao) {

    /** LiveData со списком всех товаров в БД, автоматически обновляемый через Room. */
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    /** LiveData для хранения результатов выполнения поиска по названию. */
    val searchResults = MutableLiveData<List<Product>>()

    /** Область видимости корутин, привязанная к главному потоку UI. */
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    /**
     * Асинхронная вставка товара в базу данных в фоновом потоке IO.
     * @param newproduct Новый товар для добавления.
     */
    fun insertProduct(newproduct: Product) {
        coroutineScope.launch(Dispatchers.IO) {
            productDao.insertProduct(newproduct)
        }
    }

    /**
     * Асинхронное удаление товара по его названию в фоновом потоке IO.
     * @param name Название товара для удаления.
     */
    fun deleteProduct(name: String) {
        coroutineScope.launch(Dispatchers.IO) {
            productDao.deleteProduct(name)
        }
    }

    /**
     * Запуск асинхронного поиска товара с сохранением результатов в [searchResults].
     * @param name Название товара для поиска.
     */
    fun findProduct(name: String) {
        coroutineScope.launch(Dispatchers.Main) {
            searchResults.value = asyncFind(name).await()
        }
    }

    /**
     * Вспомогательный метод для выполнения SQL-запроса поиска в потоке IO.
     * @param name Название искомого товара.
     * @return Deferred с результатом поиска списка продуктов.
     */
    private fun asyncFind(name: String): Deferred<List<Product>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async productDao.findProduct(name)
        }
}
