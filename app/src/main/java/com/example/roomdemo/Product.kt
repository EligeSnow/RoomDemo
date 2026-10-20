package com.example.roomdemo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Класс сущности (Entity), описывающий структуру таблицы "products" в базе данных SQLite.
 * Включает столбцы: productId (первичный ключ), productName (название товара) и quantity (количество).
 */
@Entity(tableName = "products")
class Product {

    /**
     * Идентификатор продукта. Выступает в роли автогенерируемого первичного ключа (PrimaryKey).
     * В базе данных столбцу задано имя "productId".
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "productId")
    var id: Int = 0

    /**
     * Название товара. В базе данных столбцу задано имя "productName".
     */
    @ColumnInfo(name = "productName")
    var productName: String = ""

    /**
     * Количество товара. Поскольку аннотация @ColumnInfo не указана,
     * имя столбца совпадает с именем переменной "quantity".
     */
    var quantity: Int = 0

    /** Конструктор по умолчанию, необходимый для работы библиотеки Room. */
    constructor()

    /**
     * Вспомогательный конструктор для создания экземпляра товара без первичного ключа
     * (ключ генерируется автоматически при вставке в БД).
     */
    constructor(productname: String, quantity: Int) {
        this.productName = productname
        this.quantity = quantity
    }
}
