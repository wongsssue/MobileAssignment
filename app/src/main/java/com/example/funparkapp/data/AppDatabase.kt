package com.example.funparkapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [UserType::class, Ticket::class, TicketType::class, CartItem::class, PurchaseHistory::class, PurchasedItem::class, PaymentMethod::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val ticketDao: TicketDao
    abstract val cartDao: CartItemDao
    abstract val ticketPurchasedDao: PurchasedTicketDao
    abstract val paymentMethodDao: PaymentMethodDao
    abstract val userDao: UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration() //REMOVE TO ALLOW DATA TO BE SAVED
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

