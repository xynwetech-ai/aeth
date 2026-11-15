# Aether Launcher

Refined. Adaptive. Reliable.

Package: `com.aethlauncher.kyn` | Min SDK 26 | Target SDK 36

## Status
- Milestone 0: Project Foundation
- Milestone 1: Safety Core
- Milestone 2: Home Screen & Visual Engine

## Build
Push ke branch `main` atau jalankan workflow "Build APK" dari tab Actions.
APK debug ada di Artifacts: `AetherLauncher-debug`.

## Final polish in this workspace
- Liquid Glass Dock dengan kapsul berlapis, highlight, border, dan fallback Safe Mode.
- Dock dapat digeser dengan tangan; tekan lama untuk editor posisi X/Y dan ukuran.
- Posisi, ukuran, opasitas, label, dan animasi Dock tersimpan di DataStore.
- Aplikasi Home dapat dipindahkan dengan drag setelah tekan lama.
- Gradle Wrapper disertakan untuk build reproducible.

Catatan: validasi APK lokal memerlukan Android SDK; build sandbox ini terhenti karena SDK tidak tersedia. Gunakan GitHub Actions workflow untuk validasi penuh dan artefak APK.

## Milestone 6 visual and interaction pass
- Style `Transparent` menjadi default ringan lintas API.
- Style `Native Glass` memakai Android window blur API 31+ bila tersedia, dengan fallback otomatis.
- Style tambahan `Soft Light`, preview kartu tema, dan migrasi setting `LIQUID_GLASS` lama ke `Transparent`.
- Clock widget dapat digeser dengan jari dan posisi tersimpan.
- Dock dan aplikasi Home tetap mendukung gesture drag.
- Research teknis tersimpan di `docs/LIQUID_GLASS_RESEARCH.md`.
