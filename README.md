# Gothic RPG Organizátor

Android aplikace, která z tvého skutečného majetku dělá inventář RPG postavy.
Věci si vytvoříš sám, dáš jim vzácnost, staty, váhu a pixelartovou ikonu, a pak je
oblékáš na postavu, nosíš v batohu nebo skladuješ v truhlách (doma, v práci, na chatě).

Vizuálně vychází z tmavé gotické palety a z tooltipů Baldur's Gate III: jméno v barvě
vzácnosti, blok statů, vlastnosti a kurzívou psaný popis nad lištou se slotem a váhou.

## Co aplikace umí

* **Hrdina** – postava se sloty na hlavu, krk, tři vrstvy hrudníku, pás, tři vrstvy nohou,
  dvě vrstvy chodidel, čtyři prsteny a batoh. Staty ze všech oblečených věcí se sčítají.
* **Batoh** – aktivní inventář. Kapacitu tvoří základ postavy plus kapsy a popruhy
  oblečených věcí, takže bunda s kapsami nebo batoh reálně zvětší, co uneseš.
* **Kovárna** – tvorba předmětů: jméno, slot, vzácnost, popis, staty, váha, počet použití
  u spotřebních věcí a ikona (vlastní obrázek z galerie se převede na pixelart, nebo
  jedna z připravených šablon).
* **Místa** – truhly s kapacitou, do kterých věci odkládáš a zase si je bereš.
* **Sety výbavy** – uložíš si celý outfit včetně toho, ve které vrstvě co bylo, a jedním
  klepnutím se do něj zase oblékneš.

Všechno běží offline v lokální Room databázi; aplikace nemá oprávnění k internetu.

##技 Technologie

Kotlin, Jetpack Compose (Material 3), Room, ViewModel + StateFlow. Pixelart postavy
i siluety prázdných slotů se kreslí přímo na Canvas z textových matic, takže v projektu
nejsou žádné bitmapové assety.

## Sestavení a spuštění

**Potřebuješ:** [Android Studio](https://developer.android.com/studio) a Android SDK 36.

```bash
./gradlew assembleDebug        # sestavení APK
./gradlew testDebugUnitTest    # unit testy
./gradlew installDebug         # nasazení na připojené zařízení nebo emulátor
```

Nebo projekt prostě otevři v Android Studiu (**Open** → adresář projektu) a spusť ho.

Projekt používá Gradle wrapper (`gradlew`), takže není potřeba mít Gradle nainstalovaný.
Wrapper je připnutý na Gradle 9.7.1 — pokud by si ho AGP nevzalo, změň verzi
v `gradle/wrapper/gradle-wrapper.properties`.

### Podepisování

Debug sestavení používá `debug.keystore` v kořeni projektu (soubor je v `.gitignore`).
Pokud ho nemáš, buď si ho vygeneruj, nebo z `app/build.gradle.kts` smaž řádek
`signingConfig = signingConfigs.getByName("debugConfig")` a nech Android Studio použít
výchozí debug klíč.

Release sestavení čte keystore z proměnných prostředí `KEYSTORE_PATH`, `STORE_PASSWORD`
a `KEY_PASSWORD`.
