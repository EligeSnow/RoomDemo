package com.example.roomdemo

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Интерфейс DAO (Data Access Object) для доступа к данным таблицы "products".
 * Определяет методы взаимодействия с базой данных SQLite через аннотации Room.
 */
@Dao
interface ProductDao {

    /**
     * Вставка новой записи продукта в таблицу "products".
     * @param product Объект сущности Product для вставки.
     */
    @Insert
    fun insertProduct(product: Product)

    /**
     * Поиск продуктов по их точному названию.
     * @param name Название искомого товара.
     * @return Список найденных продуктов.
     */
    @Query("SELECT * FROM products WHERE productName = :name")
    fun findProduct(name: String): List<Product>

    /**
     * Удаление всех продуктов с указанным названием из базы данных.
     * @param name Название товара для удаления.
     */
    @Query("DELETE FROM products WHERE productName = :name")
    fun deleteProduct(name: String)

    /**
     * Получение всех записей из таблицы "products".
     * Возвращает LiveData для автоматического уведомления подписчиков при любых изменениях в таблице.
     */
    @Query("SELECT * FROM products")
    fun getAllProducts(): LiveData<List<Product>>
}
