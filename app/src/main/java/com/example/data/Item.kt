package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SlotType(val displayName: String, val maxSlots: Int, val description: String) {
    HLAVA("Hlava", 1, "Slot pro čepici, helmu či korunu."),
    KRK("Krk", 1, "Slot pro náhrdelník nebo řetízek."),
    HRUDNIK("Hrudník", 3, "Sloty pro spodní, střední a vnější vrstvy (triko, mikina, bunda)."),
    PAS("Pás", 1, "Slot pro opasek."),
    NOHY("Nohy", 3, "Sloty pro spodní prádlo, termo prádlo a gatě."),
    CHODIDLA("Chodidla", 2, "Sloty pro ponožky a boty."),
    PRST("Prsty", 4, "Až 4 sloty pro prsteny."),
    BATOH("Batoh/Taška", 1, "Slot pro batoh, kabelku nebo tašku zvyšující kapacitu."),
    OBECNY("Jiný předmět", 0, "Obecný předmět, který nelze nosit, slouží jako materiál či spotřební zboží.")
}

enum class ItemRarity(val displayName: String, val color: Long) {
    COMMON("Obyčejný (bílá)", 0xFFFFFFFF),
    UNCOMMON("Neobyčejný (zelená)", 0xFF4CAF50),
    RARE("Vzácný (modrá)", 0xFF2196F3),
    UNIQUE("Unikátní (fialová)", 0xFF9C27B0),
    LEGENDARY("Legendární (oranžová)", 0xFFFF9800)
}

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val slotType: String, // SlotType.name
    val rarity: String, // ItemRarity.name
    val description: String = "",
    val stats: String = "", // e.g. "Odolnost vůči zimě:Dobrá,Ochrana:+2"
    val weight: Double = 0.5,
    val isConsumable: Boolean = false,
    val charges: Int = 0, // current uses
    val maxCharges: Int = 0, // max uses
    val hasPockets: Boolean = false,
    val pocketSize: Int = 0, // inventory slot bonus when equipped
    val pixelArtData: String? = null, // Base64 PNG image or Built-in Icon name
    val locationId: Long? = null, // Null if on character (equipped or in backpack), otherwise REFERENCES Location.id
    val equippedSlotIndex: Int? = null // If not null, equipped on active character in specified slot type's index (0 to maxSlots-1)
)
