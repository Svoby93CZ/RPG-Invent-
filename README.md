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

## Technologie

Kotlin, Jetpack Compose (Material 3), Room, ViewModel + StateFlow. Pixelart postavy
i siluety prázdných slotů se kreslí přímo na Canvas z textových matic, takže v projektu
nejsou žádné bitmapové assety.

### Písma

V `app/src/main/res/font/` jsou přibalené [Cinzel](https://fonts.google.com/specimen/Cinzel)
(nadpisy) a [EB Garamond](https://fonts.google.com/specimen/EB+Garamond) (karty předmětů).
Oba jsou pod SIL Open Font License 1.1, texty licencí jsou v `app/src/main/assets/licenses/`.
Jde o statické řezy vytažené z variabilních originálů a ořezané na latinku + Latin Extended-A
(celá česká diakritika), dohromady ~240 kB. Seznamy, počítadla a tlačítka zůstaly monospace,
aby si aplikace udržela pixelový charakter.

### Databáze a migrace

Room, verze schématu 4. Schéma se exportuje do `app/schemas/` (vznikne při prvním buildu)
a **každá změna entit musí mít migraci** — vzorem je `MIGRATION_3_4` v `AppDatabase.kt`.
Destruktivní fallback, který dřív při každé změně schématu smazal všechna data, je teď
omezený jen na verze 1 a 2 z doby, kdy se schéma neexportovalo. Když od verze 3 zvýšíš
`version` a zapomeneš migraci, aplikace spadne při startu — místo aby ti tiše vymazala
inventář.

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

### Nasazení na telefon

Telefon potřebuje Android 7.0 nebo novější (`minSdk 24`).

1. V telefonu zapni vývojářská nastavení: **Nastavení → O telefonu** a sedmkrát klepni na
   **Číslo sestavení**. Pak v **Možnosti pro vývojáře** zapni **Ladění přes USB**.
2. Připoj telefon kabelem, u režimu USB zvol **Přenos souborů** a na výzvu potvrď
   otisk počítače.
3. `adb devices` musí telefon vypsat jako `device`. Pokud píše `unauthorized`, nepotvrdil
   jsi dialog v telefonu; pokud ho nevypíše vůbec, chybí udev pravidla — na Ubuntu/Pop!_OS
   je doplní `sudo apt install android-sdk-platform-tools-common`.
4. `./gradlew installDebug`

Bez kabelu (Android 11+): v možnostech pro vývojáře zapni **Bezdrátové ladění**, pak
`adb pair <ip>:<port>` s kódem z telefonu a `adb connect <ip>:<port>`.

Případně APK z `app/build/outputs/apk/debug/app-debug.apk` prostě přenes do telefonu
a nainstaluj ručně (telefon si řekne o povolení instalace z neznámých zdrojů).

### Podepisování

Debug sestavení podepisuje Android Gradle Plugin sdíleným klíčem `~/.android/debug.keystore`,
který si sám vytvoří — není potřeba nic nastavovat.

Release sestavení čte keystore z proměnných prostředí `KEYSTORE_PATH`, `STORE_PASSWORD`
a `KEY_PASSWORD`.
