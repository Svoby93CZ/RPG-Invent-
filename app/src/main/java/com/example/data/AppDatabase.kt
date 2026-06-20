package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [GameCharacter::class, Location::class, Item::class, EquipmentSet::class, Skill::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rpg_character_organizer_db"
                )
                .addCallback(AppDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.appDao()
                    
                    // seed character
                    dao.insertCharacter(GameCharacter(id = 1, name = "Hrdina Reality"))
                    
                    // seed default locations
                    val domovId = dao.insertLocation(Location(name = "Domov", maxCapacity = 15))
                    val praceId = dao.insertLocation(Location(name = "Práce", maxCapacity = 8))
                    
                    // seed starting equipped items
                    dao.insertItem(Item(
                        name = "Ošumělá čepice",
                        slotType = "HLAVA",
                        rarity = "COMMON",
                        description = "Trochu kouše, ale drží hlavu v teple.",
                        stats = "Odolnost vůči zimě:Nízká,Pohodlí:+1",
                        weight = 0.2,
                        equippedSlotIndex = 0
                    ))

                    dao.insertItem(Item(
                        name = "Kostkovaná košile",
                        slotType = "HRUDNIK",
                        rarity = "COMMON",
                        description = "Teplý flanelový kousek, ideální na podzim.",
                        stats = "Odolnost vůči zimě:Dobrá",
                        weight = 0.4,
                        hasPockets = true,
                        pocketSize = 2, // offers 2 inventory slots because it has shirt pockets!
                        equippedSlotIndex = 0
                    ))

                    dao.insertItem(Item(
                        name = "Kožený batoh poutníka",
                        slotType = "BATOH",
                        rarity = "RARE",
                        description = "Prostorný kožený turistický batoh.",
                        stats = "Kapacita:+12",
                        weight = 1.2,
                        hasPockets = true,
                        pocketSize = 12, // adds 12 inventory slots
                        equippedSlotIndex = 0
                    ))

                    dao.insertItem(Item(
                        name = "Prsten štěstí",
                        slotType = "PRST",
                        rarity = "LEGENDARY",
                        description = "Nápis uvnitř říká: 'Všechno dobře dopadne.'",
                        stats = "Štěstí:+10,Soustředění:+5",
                        weight = 0.02,
                        equippedSlotIndex = 0
                    ))

                    // seed some stored items in "Domov" stash
                    dao.insertItem(Item(
                        name = "Starší zimní bunda",
                        slotType = "HRUDNIK",
                        rarity = "UNCOMMON",
                        description = "Velká, těžká bunda s hlubokými kapsami.",
                        stats = "Odolnost vůči zimě:Skvělá,Izolace:+5",
                        weight = 1.8,
                        hasPockets = true,
                        pocketSize = 6, // adds 6 slots when worn
                        locationId = domovId
                    ))

                    dao.insertItem(Item(
                        name = "Kancelářský čaj",
                        slotType = "OBECNY",
                        rarity = "COMMON",
                        description = "Voňavý heřmánkový čaj z firemní kuchyňky.",
                        stats = "Energie:+2 body",
                        weight = 0.05,
                        isConsumable = true,
                        charges = 5,
                        maxCharges = 5,
                        locationId = praceId
                    ))

                    // seed a consumable in the traveler's active storage (backpack representation)
                    dao.insertItem(Item(
                        name = "Elixír soustředění",
                        slotType = "OBECNY",
                        rarity = "UNIQUE",
                        description = "Dodá mysli nebývalou jasnost.",
                        stats = "Koncentrace:+5 bodů,Trvání:1 hodina",
                        weight = 0.1,
                        isConsumable = true,
                        charges = 3,
                        maxCharges = 3,
                        locationId = null,
                        equippedSlotIndex = null
                    ))
                }
            }
        }
    }
}
