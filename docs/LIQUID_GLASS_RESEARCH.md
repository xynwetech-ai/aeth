# Aether Launcher — Liquid Glass Research

## Kesimpulan implementasi

Android tidak memiliki API publik bernama "Liquid Glass". Efek yang paling dekat secara native adalah **cross-window blur**: `Window#setBackgroundBlurRadius()` untuk background blur dan blur-behind untuk memburamkan konten di belakang sebuah window. API ini tersedia mulai Android 12/API 31, hanya aktif pada perangkat/kondisi tertentu, dan bisa dinonaktifkan sistem saat battery saver atau kondisi GPU tertentu. Karena itu Aether harus selalu memiliki fallback opaque/translucent ketika blur tidak aktif.

`Modifier.blur()` pada Jetpack Compose bukan backdrop blur. Ia memburamkan isi composable itu sendiri, termasuk teks, ikon, dan child composables. Jika ditempel langsung ke Dock, hasilnya bukan kaca yang memburamkan wallpaper di belakang Dock. Native Glass Aether akan menggunakan window blur hanya pada API 31+ dan tetap memakai lapisan tint, highlight, border, serta fallback yang bisa dibaca.

Android 12 memperkenalkan Dynamic Color berbasis wallpaper. Android 13 menambah varian palette seperti Neutral, Vibrant Tonal, dan Expressive. Dynamic Color mengubah token warna, tetapi bukan efek blur atau material kaca.

## Keputusan produk

- **Transparent**: style default lintas API. Wallpaper tetap terlihat, tanpa klaim blur.
- **Native Glass**: style eksperimental API 31+ dengan window blur saat tersedia; fallback menjadi tinted translucent surface ketika blur tidak tersedia.
- **Ultra Minimal**: permukaan seminimal mungkin.
- **AMOLED Dark**: hitam murni dan kontras tinggi.
- Semua efek mahal dikontrol RenderPolicy dan Safe Mode.
- Tidak ada klaim bahwa Native Glass pasti tersedia pada setiap perangkat.

## Sumber

1. https://source.android.com/docs/core/display/window-blurs
2. https://developer.android.com/develop/ui/compose/graphics/blur/overview
3. https://developer.android.com/develop/ui/views/theming/dynamic-colors
4. https://developer.android.com/reference/android/graphics/RenderEffect
