package com.example.roomdemo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Абстрактный класс базы данных Room.
 * Объявляет сущности (entities = [Product::class]) и версию схемы базы данных.
 */
@Database(entities = [(Product::class)], version = 1, exportSchema = false)
abstract class ProductRoomDatabase : RoomDatabase() {

    /**
     * Абстрактный метод для получения экземпляра ProductDao.
     */
    abstract fun productDao(): ProductDao

    companion object {
        /** Ссылка на единственный экземпляр базы данных (Singleton). */
        private var INSTANCE: ProductRoomDatabase? = null

        /**
         * Метод получения единого экземпляра базы данных Room.
         * Использует шаблон Singleton с синхронизацией потоков.
         *
         * @param context Контекст приложения.
         * @return Экземпляр ProductRoomDatabase.
         */
        fun getInstance(context: Context): ProductRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    // Создание базы данных с именем "product_database"
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ProductRoomDatabase::class.java,
                        "product_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
