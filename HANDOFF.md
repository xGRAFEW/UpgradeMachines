# HANDOFF.md

บันทึกความคืบหน้าล่าสุดของโปรเจกต์ `UpgradeMachines` — อ่านไฟล์นี้ก่อนเริ่ม session ใหม่เสมอ
(อัปเดตล่าสุด: 2026-09-08)

## สถานะปัจจุบัน
- [x] สำรวจโค้ดทั้งหมด (5 listener, 1 command, 3 util class) — โครงสร้างมั่นคง ไม่มี API เฉพาะเวอร์ชันที่เปราะบาง
- [x] ปรับ `pom.xml` → `paper-api` เป็น `26.2.build.121-stable` (ดูเหตุผลด้านล่าง)
- [x] ปรับ `pom.xml` → `maven.compiler.source/target` เป็น `25` (paper-api 26.2 class files คือ Java 25 / class version 69 — คอมไพล์ด้วย JDK 21 ไม่ได้ ต้องใช้ JDK 25)
- [x] ปรับ `plugin.yml` → `api-version: '26.2'`
- [x] แก้ `CrafterUpgradeListener.java` → import `CrafterCraftEvent` จาก `org.bukkit.event.block` (ย้ายมาจาก `io.papermc.paper.event.block` แล้วในสาย 26.x — กลายเป็น Bukkit event มาตรฐาน ไม่ใช่ Paper-only อีกต่อไป)
- [x] `git init` + `.gitignore`
- [x] build สำเร็จด้วย Maven พกพา (`.tools/apache-maven-3.9.9`) + JDK 25 → ได้ `target/upgrademachines-1.0.0.jar`
- [x] deploy jar ไปเซิร์ฟทดสอบจริง + รันเซิร์ฟจริงยืนยันแล้ว: **`UpgradeMachines` โหลด/enable สำเร็จ ไม่มี error** (log: `Loading server plugin UpgradeMachines v1.0.0` → `Enabling...` → `UpgradeMachines เปิดใช้งานแล้ว!`) เซิร์ฟหยุดเรียบร้อยหลังทดสอบ
- [x] commit แรกเข้า git (`4eb62a2`)
- [x] สร้าง GitHub repo `xGRAFEW/UpgradeMachines` (public) + push → https://github.com/xGRAFEW/UpgradeMachines
- [x] สร้าง GitHub Release `v1.0.0` พร้อมแนบ `upgrademachines-1.0.0.jar` → https://github.com/xGRAFEW/UpgradeMachines/releases/tag/v1.0.0

**งานหลักที่ขอไว้เสร็จครบแล้ว** งานที่เหลือ (ถ้ามี) คือรอ Purpur 26.3 ออก stable build จริงแล้วค่อยอัปเกรด+รีเทสต์ตามหัวข้อด้านล่าง

### อัปเดต v1.1.0 (2026-09-08 16:5x) — เปลี่ยนวิธีอัพเกรด
ผู้ใช้ขอเปลี่ยนกลไก: **เอาไม้อัพเกรดออกทั้งหมด** เปลี่ยนเป็น **Shift (กดย่อ) + คลิกขวา** ที่บล็อกโดยตรงเพื่ออัพเกรด (ไม่ต้องถือไอเทมพิเศษ) และ**เอาฟีเจอร์ลดระดับ (downgrade) ออกทั้งหมด** — คลิกขวาแบบไม่กด Shift ยังคงเปิด/ใช้บล็อกตามปกติแบบวานิลลาเหมือนเดิม
- แก้ `UpgradeToolListener.java`: ลบการเช็คไม้ (PDC `wandLevel` บนไอเทม) ออก, trigger ใหม่คือ `action==RIGHT_CLICK_BLOCK && player.isSneaking() && MachineType.fromBlock(...) != null` แล้วค่อย `event.setCancelled(true)` (เดิม cancel event ทุกกรณีที่ถือไม้ ทำให้ non-sneak click ไม่ถูกแตะต้องอีกต่อไป) ลบ `downgrade()` method ทิ้ง
- ลบ `wandLevel` ออกจาก `UpgradeKeys.java`, ลบ `/upgrade wand` + `createWand()` ออกจาก `UpgradeCommand.java`, ลบ `getKeys()` accessor ที่ไม่มีใครเรียกใช้แล้วออกจากทั้ง `UpgradeManager.java` และ `UpgradeMachinesPlugin.java`
- อัปเดต `plugin.yml` (description/permission/usage) และ `README.md` ให้ตรงกับกลไกใหม่
- **bump เวอร์ชันเป็น 1.1.0** (`pom.xml` + `plugin.yml`) เพราะเป็น behavior change ที่ผู้เล่น/แอดมินเห็นชัดเจน
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว 2 รอบ (ครั้งแรกตอนแก้โค้ด, ครั้งที่สองหลัง bump เวอร์ชัน) — โหลด/enable สำเร็จไม่มี error ทั้งคู่
- Push + สร้าง Release `v1.1.0` แล้ว: https://github.com/xGRAFEW/UpgradeMachines/releases/tag/v1.1.0
- **ยังไม่ได้ทดสอบจริงในเกม** (เข้าเซิร์ฟแล้วกดย่อคลิกขวาดูว่าอัพเกรดสำเร็จจริงไหม) — ที่ทดสอบไปคือแค่ "โหลด/enable ปลั๊กอินไม่มี error" เท่านั้น ถ้า session หน้าจะ verify เพิ่ม ให้เข้าเกมจริงแล้วลอง sneak+คลิกขวาที่เตาเผา/ฮอปเปอร์/ดิสเพนเซอร์/คราฟเตอร์

### หมายเหตุการทดสอบรอบนี้ (2026-09-08 16:0x-16:15)
- ตอนเริ่มงาน มีเซิร์ฟทดสอบตัวเดิม (PID เดิม) ค้างรันอยู่แล้วตั้งแต่ 13:13 (ไม่มีผู้เล่นออนไลน์เลยตลอด — เช็คจาก log ไม่มี "joined the game") จึงสั่ง `taskkill /PID <pid>` (ไม่ใช้ `/F`) เพื่อหยุดก่อนรันใหม่พร้อม jar ตัวใหม่
- **ข้อสังเกต**: `taskkill` (ไม่ /F) บนเครื่องนี้ไม่ทำให้ log ขึ้นข้อความ "Stopping the server"/"Saving worlds" เลยทั้ง 2 รอบที่ทดสอบ — เป็นไปได้ว่า shutdown hook ของ Paper ไม่ได้ถูกเรียกแบบ graceful เต็มรูปแบบบน Windows ผ่านวิธีนี้ (ไม่มี error/corruption ให้เห็นหลังสตาร์ทใหม่ก็จริง แต่ควรระวัง) — ถ้าจะให้ปลอดภัยกว่านี้ในอนาคต ควรเปิด RCON (`enable-rcon=true` ใน `server.properties`, ตอนนี้ปิดอยู่) แล้วสั่ง `stop` ผ่าน RCON แทน
- log หลักที่ใช้ดูสถานะเรียลไทม์คือ **`logs/latest.log`** (มาตรฐานของ Paper) ไม่ใช่ `server_console.log` ที่ root — ไฟล์หลังนี้ถูกเขียนโดย wrapper ของ Phoenix Plugins ตอนรันผ่าน `run.bat`/panel เท่านั้น ถ้าเรา spawn โปรเซส java ตรง ๆ (ไม่ผ่าน wrapper นั้น) มันจะไม่ถูกอัปเดต

## เรื่องเวอร์ชัน Purpur 26.2 → 26.3 (สำคัญ อ่านก่อนแก้ต่อ)
Minecraft/Paper/Purpur เปลี่ยนมาใช้เลขเวอร์ชันแบบปี (`26.x`) แล้ว ไม่ใช่ `1.21.x` แบบเดิม จากการเช็ค repo จริง:

- **paper-api ที่มีให้ใช้ตอนนี้**: เวอร์ชันล่าสุดในสาย `26.2` คือ `26.2.build.121-stable` (เสถียร, ผ่าน alpha/beta ครบแล้ว) ส่วน `26.3` มีแค่ **`26.3-pre-2.build.0-alpha`** — เป็น pre-release/alpha รอบแรกสุดของสายถัดไป ยังไม่เสถียร
- **เซิร์ฟทดสอบที่มี** รัน Purpur **26.2-2632-stable** (Minecraft 26.2) เท่านั้น — ยังไม่มี Purpur build ของ 26.3 ให้โหลดทดสอบจริงเลย (ไม่มีใน `versions/` ของโฟลเดอร์เซิร์ฟด้วย)

**การตัดสินใจ**: compile ปลั๊กอินด้วย `paper-api:26.2.build.121-stable` (ตรงกับเวอร์ชันที่รันบนเซิร์ฟทดสอบจริงเป๊ะ ๆ) และตั้ง `api-version: '26.2'` ใน `plugin.yml` เพราะ:
1. Bukkit/Paper ปฏิเสธไม่โหลดปลั๊กอินถ้า `api-version` **สูงกว่า**เวอร์ชันที่เซิร์ฟรองรับ — ถ้าตั้งเป็น `26.3` ปลั๊กอินจะ**โหลดไม่ขึ้นเลยบนเซิร์ฟทดสอบ 26.2 ที่มี** ซึ่งขัดกับกฎ "ต้องทดสอบบนเซิร์ฟจริงเสมอ"
2. โค้ดทั้งหมดใช้แต่ API มาตรฐานที่นิ่งมานาน (`FurnaceSmeltEvent`, `BlockDispenseEvent`, `InventoryMoveItemEvent`, `CrafterCraftEvent`, `PersistentDataContainer`) ไม่มีตัวไหนถูกเปลี่ยน/เอาออกระหว่าง 26.2 → 26.3-alpha เท่าที่ตรวจสอบได้จาก metadata ของ repo — ปลั๊กอินนี้จึง**เข้ากันได้ล่วงหน้ากับ 26.3 อยู่แล้ว**ในทางเทคนิค แม้ compile เป็นเลข 26.2 ก็ตาม

**เมื่อ Purpur 26.3 ออก stable build จริง** (มี jar ให้โหลดเทสได้): ทำตามนี้
1. อัปเดตเซิร์ฟทดสอบ (หรือสร้างโฟลเดอร์ทดสอบใหม่) เป็น Purpur 26.3
2. เช็คเวอร์ชัน paper-api ล่าสุดของสาย 26.3 (`https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/maven-metadata.xml`)
3. แก้ `pom.xml` → เวอร์ชัน paper-api ใหม่, แก้ `plugin.yml` → `api-version: '26.3'`
4. `mvn clean package` ใหม่ แล้วทดสอบบนเซิร์ฟ 26.3 จริงก่อนถือว่าเสร็จ

## Build โดยไม่มี Maven ติดตั้งในระบบ
เครื่องนี้ไม่มี `mvn` ใน PATH เลย (เช็คแล้วทั้ง global + `where /R`) และไม่มี `choco`/`scoop` ก็เลยดาวน์โหลด Apache Maven 3.9.9 แบบพกพามาไว้ที่ `.tools/` (อยู่ใน `.gitignore` แล้ว ไม่ commit — ถ้า session ใหม่ไม่เจอโฟลเดอร์นี้ ให้ดาวน์โหลดซ้ำจาก `https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip`)

**สำคัญ**: ต้อง build ด้วย **JDK 25** ไม่ใช่ JDK 21 — paper-api 26.2 ถูก compile ด้วย Java 25 (class file version 69) ส่วน JDK 21 อ่านได้แค่ version 65 ทำให้ error `bad class file ... wrong version 69.0, should be 65.0` เครื่องนี้มี JDK 25 อยู่แล้วที่ `C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot`

```
cd "C:\Users\ACER\Desktop\Project\Upgrade\UpgradeMachines"
export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot"   # (bash) หรือ $env:JAVA_HOME=... ใน PowerShell
"C:\Users\ACER\Desktop\Project\Upgrade\.tools\apache-maven-3.9.9\bin\mvn.cmd" clean package
```
jar ที่ได้จะอยู่ที่ `target\upgrademachines-1.0.0.jar` — **build+test ผ่านแล้วจริง** ในรอบทำงานนี้ (ดูหัวข้อสถานะปัจจุบัน)

## เซิร์ฟทดสอบ (ต้องใช้เสมอตามที่ผู้ใช้ระบุ)
```
C:\Users\ACER\Desktop\Project\Survival SMP Purpur 26.2 test\Survival SMP Purpur 26.2 test
```
- มีปลั๊กอินอื่นติดตั้งอยู่เยอะ (LuckPerms, Vault, WorldGuard, MMOItems ฯลฯ) — **ปลั๊กอินนี้ใช้ Vault แบบ softdepend ผ่าน `EconomyHook`** (ถ้ามี Vault + ปลั๊กอินระบบเงินจะเก็บราคาเป็นเงินได้ ถ้าไม่มีจะข้ามอัตโนมัติ) เซิร์ฟทดสอบนี้มี Vault ติดตั้งอยู่แล้ว แต่ต้องเช็คว่ามีปลั๊กอินระบบเงิน (เช่น EssentialsX/CMI) ลงทะเบียน economy provider ไว้จริงหรือไม่ — ถ้าไม่มี ราคาเงินจะถูกข้ามเฉย ๆ (ไม่ error)
- deploy: copy `target\upgrademachines-1.0.0.jar` → `plugins\` ของโฟลเดอร์เซิร์ฟ, restart ด้วย `run.bat`
- เช็ค log: `server_console.log`, `server_err.log` หลังสตาร์ท ต้องไม่มี error เกี่ยวกับ `UpgradeMachines`

## GitHub
- Repo ปลายทาง: `https://github.com/xGRAFEW/UpgradeMachines` (ยังไม่มีอยู่จริง ณ ตอนสำรวจ — ต้องสร้างใหม่ด้วย `gh repo create`)
- `gh` CLI login อยู่แล้วเป็น account `xGRAFEW` (repo scope พร้อม)
- แผน: `gh repo create xGRAFEW/UpgradeMachines --public --source=. --remote=origin` → push → `gh release create` แนบ jar ที่ build ได้

## จุดที่ควรระวัง/จำกัดของโค้ด (มีอยู่แล้วจาก README เดิม อย่าลืม)
- Dispenser: ไอเทมโบนัสดรอปตรง ๆ ไม่ได้เลียนแบบพฤติกรรมพิเศษ (ถังน้ำ/ไข่มอนสเตอร์/ลูกศร) ของไอเทมนั้น
- Crafter: ใช้วิธี "คราฟซ้ำ" (best-effort) เพราะ Paper ไม่มี API ลด cooldown ภายในตรง ๆ
- ราคาเงินต้องมี Vault + ปลั๊กอินระบบเงินจริง ไม่งั้นข้ามอัตโนมัติ
