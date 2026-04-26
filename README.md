# UMKM Thrift — UTS PBO 2

Aplikasi JavaFX untuk UMKM thrift baju (toko baju bekas). Project UTS mata kuliah Pemrograman Berorientasi Objek 2.

## Fitur

- **Login 2 role**: Admin & Kasir dengan akses berbeda
- **Inventory management**: CRUD barang lengkap (10 field) — kode, nama, kategori, ukuran, kondisi, harga beli, harga jual, stok, deskripsi, foto
- **Kasir**: Transaksi dengan multi-item cart, hitung profit margin, pilih metode bayar (Cash/Transfer/QRIS)
- **Laporan**: Filter transaksi by date range, total omzet & profit
- **Pencarian**: Search barang real-time by nama/kode
- **Calendar**: DatePicker untuk filter laporan
- **Audio**: BGM lofi looping + SFX (click, success, error, add-cart) dengan volume control
- **Persistence**: JSON file via Gson (data/barang.json, data/users.json, data/transaksi.json)

## Stack

- Java 11
- JavaFX 13 (controls + fxml + media)
- Maven
- Gson 2.10.1

## Build & Run

```bash
mvn clean javafx:run
```

## Login Default

| Role  | Username | Password    |
|-------|----------|-------------|
| Admin | `admin`  | `admin123`  |
| Kasir | `kasir`  | `kasir123`  |

## Struktur Project

```
src/main/java/com/example/
├── App.java                 # Entry point + scene management
├── module-info.java
├── adapter/                 # Gson TypeAdapter (polymorphic User, LocalDateTime)
├── controller/              # 7 FXML controller
├── exception/               # Custom exception
├── model/                   # User, Admin, Kasir, Barang, Transaksi, enum
├── repository/              # Generic JsonRepository<T>
├── service/                 # AuthService, InventoryService, KasirService, AudioManager
└── util/                    # RupiahFormatter, KodeGenerator, SessionManager, AlertHelper

src/main/resources/com/example/
├── view/                    # 7 FXML
├── css/app.css
├── images/products/         # Foto barang (upload via FileChooser)
└── audio/                   # BGM + SFX (drop file mp3/wav di sini)
```

## Catatan

Audio file (bgm-lofi.mp3, click.wav, success.wav, error.wav, add-cart.wav) harus ditaruh di `src/main/resources/com/example/audio/`. Source bisa dari Pixabay (royalty-free).

Test checklist manual ada di `docs/TEST-CHECKLIST.md`.
