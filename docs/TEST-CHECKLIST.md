# Manual Test Checklist — Thrift UMKM Store

Run `mvn clean javafx:run` and walk through each case. Tick after verifying.

## Startup

- [ ] App opens with title "Thrift UMKM Store", size 960×620.
- [ ] `data/` folder auto-created on first run.
- [ ] `data/users.json`, `data/barang.json`, `data/transaksi.json` auto-created with seed.
- [ ] BGM auto-plays if `audio/bgm-lofi.mp3` exists. Otherwise app runs silently.

## Login

- [ ] Correct admin (`admin`/`admin123`) → dashboard with all 4 sidebar buttons.
- [ ] Correct kasir (`kasir`/`kasir123`) → dashboard with only Kasir + Settings.
- [ ] Wrong username → "Username tidak ditemukan" + SFX error.
- [ ] Wrong password → "Password salah" + SFX error.
- [ ] Empty username → "Username tidak boleh kosong".
- [ ] Empty password → "Password tidak boleh kosong".
- [ ] Pressing Enter on password field triggers login (default button).

## Dashboard / Shell

- [ ] Topbar shows user name + role.
- [ ] Volume slider changes audibly affect BGM and future SFX.
- [ ] BGM toggle button pause/resume works.
- [ ] Logout confirm → returns to login.
- [ ] Login after logout clears previous user's state.

## Inventory (Admin)

- [ ] 15 seed barang visible in TableView.
- [ ] Search by keyword filters nama/brand/kode live as typed.
- [ ] Kategori filter narrows list (blank/null = all).
- [ ] `+ Tambah Barang` → modal opens; kode auto-incremented.
- [ ] Submit with empty nama → "Nama wajib diisi".
- [ ] Submit with harga jual 0 → "Harga jual harus lebih dari 0".
- [ ] Submit with alphabetic harga → "Harga jual harus angka".
- [ ] Successful add → modal closes, new row in TableView, persisted to `data/barang.json`.
- [ ] FileChooser selects image → preview updates; file copied to `images/products/<uuid>.ext`.
- [ ] Edit existing row → modal pre-filled; save updates row and JSON.
- [ ] Hapus → confirm dialog → deletes row and JSON entry.

## Kasir (Admin or Kasir)

- [ ] Product grid shows all (or filtered) barang with thumbnail + nama + brand + ukuran + harga + stok.
- [ ] Cards with stok=0 show `+ Keranjang` disabled.
- [ ] Klik `+ Keranjang` → SFX add-cart, item muncul di cart, subtotal/total update.
- [ ] Tambah barang yang sudah di cart → qty naik, bukan duplikat baris.
- [ ] Tombol `-` di cart kurangi qty; `-` pada qty=1 hapus baris.
- [ ] Tombol `+` di cart nambahi qty (dibatasi stok).
- [ ] Tombol `✕` hapus baris cart.
- [ ] Diskon 10% mode % → total = subtotal × 0.9.
- [ ] Diskon 20000 mode Rp → total = subtotal − 20000 (min 0).
- [ ] Metode TUNAI + bayar < total → checkout error "Uang bayar kurang dari total".
- [ ] Metode TRANSFER atau QRIS → bayar tidak wajib ≥ total (tapi kembalian 0).
- [ ] Checkout sukses → SFX success, struk dialog muncul, cart kosong, produk refresh (stok berkurang), `data/transaksi.json` bertambah.
- [ ] Checkout dengan keranjang kosong → "Keranjang kosong".

## Laporan (Admin only)

- [ ] Default range: 30 hari lalu → hari ini. Data loaded automatically.
- [ ] Summary cards: Total Sales, Transaksi, Profit, Best Seller akurat terhadap data terfilter.
- [ ] Range invalid (from > to) → error.
- [ ] Double-click row → detail dialog dengan items, subtotal, diskon (jika ada), total, profit.
- [ ] Range ke masa depan → empty state (0 di semua card).

## Settings

- [ ] Volume slider sync dengan topbar slider (nilai sama).
- [ ] BGM toggle di settings menggantikan icon di topbar (dan sebaliknya via reload).
- [ ] Angka persen di samping slider update realtime.

## Exception resilience

- [ ] Hapus file `data/barang.json` manual saat app mati → restart → seed ter-recreate.
- [ ] Korup `data/transaksi.json` (isi `not json`) → app show error saat buka Laporan, tidak crash.
- [ ] Hapus semua file audio → restart → console warning, app tetap running tanpa SFX/BGM.
- [ ] Upload file bukan image via BarangFormController → filter `.png/.jpg/.jpeg` block itu; kalau somehow lolos, exception ditangkap.

## Role separation

- [ ] Login kasir: Inventory & Laporan disembunyikan di sidebar.
- [ ] Mengetik URL langsung ke inventory/laporan (mis. via debug) — tidak applicable karena tidak ada deep-link, OK.

## Shutdown

- [ ] Close window → AudioManager.dispose() dipanggil (BGM berhenti).
- [ ] `data/*.json` tetap utuh setelah close.
