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

### อัปเดต v1.2.0 (2026-09-08 17:0x-17:2x) — GUI ยืนยันอัพเกรด + Dropper + คงระดับติดไอเทม
สามฟีเจอร์ใหญ่รวดเดียว ตามที่ผู้ใช้ขอเพิ่มทีละส่วนในบทสนทนา:

1. **GUI ยืนยัน/ยกเลิกแทนอัพเกรดทันที** — Shift+คลิกขวาตอนนี้แค่ *เปิดกล่อง* (27 ช่อง) แสดงระดับปัจจุบัน→ใหม่ + ค่าประสิทธิภาพจริง (`describeEffect`) + ราคา แล้วมีปุ่ม §aยืนยันอัพเกรด / §cยกเลิกการอัพเกรด ให้กดเอง การหักเงิน/ไอเทม+เซ็ตระดับจริงเกิดตอนกด "ยืนยัน" เท่านั้น (เช็คสิทธิ์/ราคา/ระดับซ้ำอีกรอบตอนกดยืนยัน กันกรณีเงื่อนไขเปลี่ยนไปตอนกล่องเปิดค้าง)
   - ไฟล์ใหม่: `gui/UpgradeConfirmHolder.java` (marker + เก็บ block/type), `gui/UpgradeConfirmGui.java` (สร้างกล่อง, slot คงที่ CONFIRM_SLOT=11/INFO_SLOT=13/CANCEL_SLOT=15), `listener/UpgradeConfirmGuiListener.java` (ดัก click+drag ในกล่อง, cancel ทุก click กันหยิบของ, ทำ transaction จริงตอนกด confirm — โค้ด logic เดียวกับที่เคยอยู่ใน `UpgradeToolListener.upgrade()` เดิม ย้ายมาที่นี่)
   - `UpgradeToolListener.java` ตอนนี้แค่เช็คสิทธิ์+max-level แล้ว `player.openInventory(UpgradeConfirmGui.build(...))` ไม่มี logic หักเงิน/setLevel ในไฟล์นี้แล้ว

2. **เพิ่ม Dropper เป็นเครื่องที่ 5** — `MachineType.DROPPER`, config section `dropper` (เหมือน dispenser ทุกอย่าง) ยุบ `DispenserUpgradeListener` ให้ใช้ interface กลาง `org.bukkit.block.Container` (แทน `org.bukkit.block.Dispenser` เจาะจง) เลยรองรับทั้ง Dispenser และ Dropper ในไฟล์เดียว — ตรวจสอบแล้วว่า `Dispenser`/`Dropper` ทั้งคู่ extends `Container` จริงในจากตัว jar (`javap`) ก่อนเขียนโค้ด
   - `UpgradeManager.getDispenserExtraItems` เปลี่ยนเป็น `getExtraItems(MachineType, level)` ใช้ร่วมกันทั้ง dispenser/dropper (อ่าน `<configKey>.extra-items`)

3. **คงระดับติดไอเทมเมื่อทุบ/วาง** — ไฟล์ใหม่ `listener/UpgradeItemPersistenceListener.java`:
   - `BlockBreakEvent` (เฉพาะกรณี `event.isDropItems()==true` เช่นไม่ใช่ creative): ถ้าบล็อก level>0 → `setDropItems(false)` แล้วดรอปไอเทม custom เอง ตั้งชื่อ+lore ด้วย `manager.describeEffect(type, level)` (เช่น "ดรอปเปอร์ → Lv.3" / "ดรอปทีละ: 4 ชิ้น") พร้อมฝัง level ลง PDC ของ **ItemMeta** โดยใช้ `keys.level` ตัวเดียวกับที่ใช้บนบล็อก (คนละ holder แต่ NamespacedKey เดียวกันได้ ไม่มีปัญหา)
   - `BlockPlaceEvent`: อ่าน PDC level จาก `event.getItemInHand()` ถ้ามี → `manager.setLevel(...)` ใส่บล็อกที่เพิ่งวางทันที (setLevel เดิม clamp ตาม max-level ปัจจุบันอยู่แล้ว)
   - **ข้อจำกัดที่บันทึกไว้ใน README**: ครอบคลุมแค่ BlockBreakEvent ปกติ ไม่ครอบคลุมระเบิด/วิธีอื่น

- เพิ่ม `MachineType.getDisplayName()` (ชื่อไทยของแต่ละเครื่อง) ใช้แทน `type.name()` (ENUM ตัวพิมพ์ใหญ่ภาษาอังกฤษ) ในข้อความ/GUI/lore ทั้งหมด, `/upgrade info` เพิ่มบรรทัดโชว์ `describeEffect` ด้วยถ้า level>0
- อัปเดต `config.yml` (section `dropper`), `plugin.yml` (description), `README.md` (หัวข้อใหม่ "ทุบ/วางบล็อกที่อัพเกรดแล้ว" + คำอธิบาย GUI)
- **bump เวอร์ชันเป็น 1.2.0**
- **เหตุการณ์ระวังไว้**: ระหว่างทำงานลืมปิดเซิร์ฟทดสอบค้างไว้ทันทีหลัง verify v1.1.1 (เพราะ user ส่งข้อความใหม่มาแทรกกลางที) ทำให้ jar ใหม่ copy ทับไม่ได้ตอนแรก (`Device or resource busy`) ต้องเช็ค `Get-Process -Name java` แล้ว `taskkill` ตัวเก่าก่อนเสมอ **ก่อน deploy jar ใหม่ทุกครั้ง** ให้เช็คว่าไม่มี java process ค้างอยู่ก่อน
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.2.0 สำเร็จไม่มี error, เซิร์ฟหยุดเรียบร้อยหลังทดสอบ
- Push + สร้าง Release `v1.2.0` แล้ว: https://github.com/xGRAFEW/UpgradeMachines/releases/tag/v1.2.0
- **ผู้ใช้เข้าเกมทดสอบเองแล้ว** ส่งภาพหน้าจอกล่อง GUI จริงมายืนยันว่าเปิดกล่องได้ปกติ (เตาเผา, ปุ่มเขียว/แดง, ไอเทมกลาง) — ฟีเจอร์ GUI ทำงานได้จริงในเกม

### อัปเดต v1.2.1 → v1.2.2 (2026-09-08 17:2x-17:3x) — ราคา+ผลอัพเกรดโชว์ที่ชื่อกล่อง (title bar)
ผู้ใช้ส่งภาพหน้าจอกล่อง GUI จริง วงสีแดงที่แถบหัวกล่อง (ตอนนั้นมีแค่ "อัพเกรด เตาเผา" เฉย ๆ) ขอให้ราคาไปโผล่ตรงนั้นด้วย จากนั้นขอเพิ่ม "คุณสมบัติหลังอัพเกรด" ไปโชว์ตรงนั้นด้วยอีกอย่าง
- แก้ `gui/UpgradeConfirmGui.java`: ย้ายการคำนวณราคา (moneyPrice/itemMaterial/itemAmount/chargeMoney/chargeItem) ขึ้นมาก่อนสร้าง `Bukkit.createInventory(...)` แล้วประกอบ title ใหม่เป็น
  `"§8" + type.getDisplayName() + " §f" + manager.describeEffect(type, target) + " §7- " + priceSummary(...)`
  เช่น `เตาเผา เผาทีละ: 16 ชิ้น - 500`
- เพิ่ม helper `priceSummary(...)` (private static) คืนข้อความราคาแบบสั้น ใช้ทั้งใน title และแยกจาก lore เดิมที่ยังละเอียดกว่า (ยังคง lore ไว้เหมือนเดิม ไม่ได้ลบ)
- title bar ของ Minecraft แสดงได้บรรทัดเดียว ความกว้างจำกัดตามฟอนต์/ไคลเอนต์ ไม่มี hard limit ที่เช็คได้ฝั่ง server แค่ปล่อยให้ยาวเท่าที่ประกอบมา ถ้ายาวเกินไปไคลเอนต์จะตัด/scroll เอง (ยังไม่ได้ตรวจสอบด้วยตาว่ายาวเกินจริงไหมในเกม)
- bump เวอร์ชัน 2 รอบติด (1.2.1 → 1.2.2 คนละ commit ไม่ได้แยก เพราะเป็นการแก้ไขต่อเนื่องในบทสนทนาเดียวกัน สุดท้าย commit เดียวจบที่ 1.2.2)
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test ทั้ง 2 รอบ (1.2.1 และ 1.2.2) — โหลด/enable สำเร็จไม่มี error ทั้งคู่, ปิดเซิร์ฟเรียบร้อยหลังทดสอบทุกครั้ง
- **ยังไม่ได้เข้าเกมดูจริงว่า title ยาวเกินจอไหม/ตัวหนังสือ Thai ใน title bar อ่านง่ายไหม** — ควรลองเปิดกล่องจริงอีกรอบดูความยาว title ถ้ามีโอกาส

### อัปเดต v1.3.0 (2026-09-09) — ชื่อไอเทมแก้ได้ผ่าน config.yml
ผู้ใช้ถามว่าทำไมแก้ชื่อ "เตาเผา/ฮอปเปอร์/ดิสเพนเซอร์/คราฟเตอร์" ใน config.yml ไม่ได้ — พบว่าชื่อเหล่านี้ hardcode อยู่ใน `MachineType.java` (enum constructor) ไม่เคยอ่านจาก config เลย
- เพิ่มฟิลด์ `display-name` ให้ทุก section ใน `config.yml` (furnace/hopper/dispenser/dropper/crafter) ตั้งค่าเริ่มต้นเป็นชื่อไทยเดิม
- เพิ่ม `UpgradeManager.getDisplayName(MachineType)` อ่านจาก `<configKey>.display-name` โดย fallback เป็น `MachineType.getDisplayName()` เดิมถ้าไม่ได้ตั้งค่า (กัน config เก่าที่ยังไม่มีฟิลด์นี้พัง)
- แก้ทุกจุดที่เคยเรียก `type.getDisplayName()` ตรง ๆ (GUI title/lore, `/upgrade info`, ข้อความอัพเกรดสำเร็จ, ชื่อไอเทมที่ดรอปตอนทุบบล็อกอัพเกรดแล้ว) ให้เรียก `manager.getDisplayName(type)` แทน — รวม 5 ไฟล์: `UpgradeCommand.java`, `UpgradeConfirmGui.java` (2 จุด), `UpgradeConfirmGuiListener.java`, `UpgradeItemPersistenceListener.java`
- อัปเดต README.md หัวข้อ "ตั้งค่า (config.yml)" ให้พูดถึง `display-name`
- **เหตุการณ์ระวังไว้**: `.tools/apache-maven-3.9.9` ที่เคยดาวน์โหลดไว้หายไปจากเครื่อง (โฟลเดอร์ `.tools` ไม่มีอยู่แล้วตอนเริ่ม session นี้) ต้องโหลดใหม่จาก `archive.apache.org` (dlcdn.apache.org คืน 404 ให้ ลิงก์รุ่นเก่าต้องใช้ archive) — ถ้า session หน้าเจอ `.tools` หายอีกให้ไปที่ archive.apache.org แทน dlcdn
- **เหตุการณ์ระวังไว้ที่ 2**: `run.bat` ของเซิร์ฟทดสอบ hardcode พาธ JDK เป็น `jdk-25.0.3.9-hotspot` ซึ่งไม่มีอยู่จริงในเครื่องแล้ว (มีแต่ `jdk-25.0.4.101-hotspot` กับ `jdk-21.0.12.101-hotspot`) ทำให้ server process ตายเงียบทันทีตอน start (ไม่มี error ใน log เพราะ java.exe หา path ไม่เจอเลยไม่ได้รันด้วยซ้ำ) แก้โดยแก้ path ใน `run.bat` ให้ตรงกับ JDK ที่มีจริง — ถ้าเจอเซิร์ฟทดสอบ start ไม่ติดอีก ให้เช็คพาธ JDK ใน `run.bat` ก่อนเป็นอันดับแรก
- bump เวอร์ชันเป็น 1.3.0 (`pom.xml` + `plugin.yml`) เพราะเป็นฟีเจอร์ใหม่ที่แอดมินใช้ได้
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.3.0 สำเร็จไม่มี error, ปิดเซิร์ฟเรียบร้อยหลังทดสอบ
- **ยังไม่ได้ทดสอบในเกมจริงว่าเปลี่ยน `display-name` ใน config แล้วชื่อไปโผล่ถูกที่จริงไหม** (GUI title, `/upgrade info`, ไอเทมที่ดรอป) — ที่ยืนยันคือแค่ enable ไม่มี error เท่านั้น

### อัปเดต v1.4.0 (2026-09-09) — หน้าตากล่องยืนยันอัพเกรด + ชื่อไอเทมที่ดรอป ตั้งค่าละเอียดได้ผ่าน config.yml
ผู้ใช้บอกว่า `display-name` (v1.3.0) ยังไม่พอ — อยากแก้ "หน้า GUI ตอนอัพเกรด" (ปุ่ม/lore/สี/ไอเทมเติมช่องว่าง/รูปแบบ title) กับ "ชื่อไอเทมแต่ละชนิดแบบละเอียด" ด้วย ถามกลับผ่าน AskUserQuestion 2 ข้อเพื่อกันเดาผิด: (1) อยากได้ชื่อแยกรายระดับ (Lv.1 ชื่อหนึ่ง Lv.2 อีกชื่อ) หรือแค่แก้รูปแบบข้อความ → ผู้ใช้ตอบว่า **แค่รูปแบบข้อความ** ไม่ใช่ต่อระดับ (2) อยากแก้ส่วนไหนของกล่อง → ผู้ใช้เลือก **ปุ่ม/lore, ไอเทม/สีของปุ่มและช่องว่าง, รูปแบบ title** ทั้ง 3 ข้อ
- เพิ่ม section `gui:` ใหม่ใน `config.yml` (อยู่บนสุด ก่อน `furnace:`) คุม `title-format`, `info-item.name-format`/`lore`, `confirm-button.material`/`name`/`lore`, `cancel-button.material`/`name`/`lore`, `filler.material`, `item-name-format`/`item-lore-format` (ใช้กับไอเทมที่ดรอปตอนทุบบล็อกที่อัพเกรดแล้ว) — ทุกช่องข้อความรองรับ placeholder `{name}`/`{level}`/`{current}`/`{max}`/`{effect}`/`{price}` และสีโค้ด `&`
- เพิ่มใน `UpgradeManager.java`: `formatText(...)`/`formatTextList(...)` (แปลงสี `&`→ChatColor + แทน placeholder ทั้ง 6 ตัว) และ getter อ่านค่าจาก `gui.*` ทั้งหมด พร้อม fallback เป็นค่าเดิมที่เคย hardcode ถ้า config เก่ายังไม่มี section นี้ (`getStringListOrDefault`/`getMaterialOrDefault`)
- แก้ `UpgradeConfirmGui.build()` ให้ประกอบ title/info-item/confirm-button/cancel-button/filler ทั้งหมดจาก `manager.formatText(...)` แทนสตริง hardcode เดิม (ลบ `import java.util.ArrayList` ที่ไม่ใช้แล้วออกด้วย)
- แก้ `UpgradeItemPersistenceListener.createUpgradedItem()` ให้ใช้ `manager.getItemNameFormat()`/`getItemLoreFormat()` แทนสตริง hardcode (ที่นี่ `{level}` = `{current}` = ระดับของไอเทมนั้น ๆ, `{price}` ว่างเปล่าเพราะไม่มีราคาเกี่ยวข้อง)
- อัปเดต README.md เพิ่มหัวข้อ "หน้าตากล่องยืนยันอัพเกรด + ชื่อไอเทมที่ดรอป (config.yml → gui:)" อธิบาย placeholder ทั้งหมด + ตัวอย่างเต็ม
- bump เวอร์ชันเป็น 1.4.0 (`pom.xml` + `plugin.yml`)
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.4.0 สำเร็จไม่มี error (`Done (44.454s)!`), ปิดเซิร์ฟเรียบร้อยหลังทดสอบ
- **ยังไม่ได้เข้าเกมจริงเปิดกล่องยืนยันอัพเกรดดูว่าปุ่ม/สี/title ที่ตั้งค่าใหม่ออกมาถูกต้องตามที่ตั้งไว้ไหม** — ที่ยืนยันคือแค่ enable ไม่มี error เท่านั้น ถ้า session หน้ามีโอกาสควรเข้าเกมลอง Shift+คลิกขวาที่เตาเผาดูกล่องจริง

### อัปเดต v1.5.0 (2026-09-10) — ชื่อแยกตามชนิดบล็อกจริง (`display-names`)
ผู้ใช้บอกว่า `display-name` (v1.3.0) ยังไม่พอ เพราะ Furnace/Blast Furnace/Smoker ทั้ง 3 ชนิดใช้ config section `furnace` เดียวกัน เลยได้ชื่อเดียวกันหมด ("เตาเผา" ทั้งหมด) อยากตั้งชื่อแยกแต่ละชนิดบล็อกจริง (ยังคงอัตราเผา/max-level/ราคาเหมือนเดิมที่ใช้ร่วมกันได้ ไม่ได้ขอแยกส่วนนั้น)
- เพิ่ม `UpgradeManager.getDisplayName(MachineType type, Material material)` overload — เช็ค `<configKey>.display-names.<MATERIAL_NAME>` ก่อน ถ้าไม่ตั้งไว้ fallback ไปที่ `<configKey>.display-name` เดิม (method เดิมแบบไม่รับ material ยังอยู่ ใช้เป็น fallback ภายใน)
- `formatText(...)`/`formatTextList(...)` ใน `UpgradeManager.java` เพิ่มพารามิเตอร์ `Material` แทรกเข้าไปเพื่อ resolve `{name}` แบบ per-material — ทุกจุดที่เรียกอยู่แล้ว (GUI, ไอเทมที่ดรอป) มี `Block`/`Material` อยู่ในมือแล้วทั้งหมดเลยส่งผ่านได้โดยไม่ต้องเพิ่ม state ใหม่: `UpgradeConfirmGui.java` (ใช้ `block.getType()`), `UpgradeItemPersistenceListener.java` (ใช้ `material` ที่มีอยู่แล้ว), `UpgradeCommand.java` และ `UpgradeConfirmGuiListener.java` (เปลี่ยนจาก `getDisplayName(type)` เป็น `getDisplayName(type, block.getType())`)
- เพิ่ม `display-names:` ใน `config.yml` ส่วน `furnace` เป็นตัวอย่าง (`FURNACE: "เตาเผา"`, `BLAST_FURNACE: "เตาถลุงแร่"`, `SMOKER: "เตาอบรมควัน"`) — ไม่บังคับต้องตั้งครบทุกชนิด ชนิดที่ไม่ตั้งจะ fallback อัตโนมัติ ไม่กระทบ config เก่าที่ยังไม่มี section นี้
- อัปเดต README.md เพิ่มหัวข้อ "ชื่อแยกตามชนิดบล็อกจริง (display-names)" + comment ใน config.yml อธิบาย `{name}` placeholder
- **ตอบคำถามผู้ใช้เรื่องการเพิ่มเครื่องจักรชนิดใหม่ในอนาคต** (ยังไม่ได้ทำ แค่ตอบแนวทาง ไม่มีโค้ดเปลี่ยน): ต้องมี Bukkit event ที่ hook "รอบการทำงาน" ของบล็อกนั้นได้ (เช่น FurnaceSmeltEvent/InventoryMoveItemEvent/BlockDispenseEvent/CrafterCraftEvent ที่ใช้อยู่) และบล็อกต้องเป็น TileState (มี PersistentDataContainer เก็บระดับได้) ถ้าเข้าเงื่อนไขนี้ ขั้นตอนคือ: (1) เพิ่ม enum constant ใน `MachineType.java` + map ใน `fromBlock()`, (2) เพิ่ม listener ใหม่ hook event ที่เกี่ยวข้อง อ่านอัตราจาก `UpgradeManager` แล้ว apply, (3) เพิ่ม accessor อัตรา + case ใน `describeEffect()` ที่ `UpgradeManager.java`, (4) เพิ่ม section ใหม่ใน `config.yml`, (5) ลงทะเบียน listener ใหม่ใน `UpgradeMachinesPlugin.java` — ส่วน GUI ยืนยัน/ระบบราคา/คงระดับติดไอเทมตอนทุบ-วาง ทำงานแบบ generic ผ่าน `MachineType.fromBlock()` อยู่แล้ว ไม่ต้องแก้เพิ่ม ถ้าผู้ใช้เจาะจงบล็อกที่อยากเพิ่มในอนาคต ค่อยเช็ค Bukkit API ว่ามี event รอบการทำงานให้ hook ไหมก่อนเริ่ม
- bump เวอร์ชันเป็น 1.5.0 (`pom.xml` + `plugin.yml`)
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.5.0 สำเร็จไม่มี error (`Done (39.664s)!`), ปิดเซิร์ฟเรียบร้อยหลังทดสอบ
- **ยังไม่ได้เข้าเกมจริงดูว่าเตาเผา/เตาถลุงแร่/เตาอบรมควันแสดงชื่อแยกกันถูกต้องไหม** (title กล่อง, ไอเทมที่ดรอป) — ที่ยืนยันคือแค่ enable ไม่มี error เท่านั้น

### อัปเดต v1.6.0 (2026-09-10) — ข้อความเอฟเฟค (`effect-format`) ตั้งค่าได้แล้ว
ผู้ใช้ถามว่าแก้ข้อความเอฟเฟคแต่ละชนิด (เช่น "เผาทีละ: 16 ชิ้น") ได้ไหม — พบว่า `describeEffect()` ยัง hardcode ข้อความเป็น switch statement ในโค้ด ไม่เคยอ่านจาก config เหมือน `display-name`/`gui:` ที่ทำไปก่อนหน้า
- แก้ `UpgradeManager.describeEffect()`: แยกตัวเลข (`rate`) ออกจากข้อความ — คำนวณ `rate` จาก accessor เดิม (`getFurnaceBatchSize`/`getHopperExtraTransfers`/`getExtraItems`/`getCrafterExtraCrafts`) เหมือนเดิมทุกอย่าง แต่ข้อความรอบตัวเลขอ่านจาก `<configKey>.effect-format` (placeholder `{rate}` ตัวเดียว) แทน string literal เดิม พร้อม `ChatColor.translateAlternateColorCodes('&', ...)` ให้ใส่โค้ดสีในข้อความเอฟเฟคได้ด้วย (ก่อนหน้านี้สีต้องมาจาก template รอบนอกอย่างเดียว)
- เพิ่ม `defaultEffectFormat(MachineType)` เป็น fallback ตรงกับข้อความเดิมทุกตัวอักษร ถ้า config เก่ายังไม่มี `effect-format` จะได้ผลลัพธ์เหมือน v1.5.0 เป๊ะ ๆ ไม่กระทบของเดิม
- เพิ่ม `effect-format: "..."` ให้ทุก section ใน `config.yml` (furnace/hopper/dispenser/dropper/crafter) พร้อม comment อธิบาย `{rate}` ที่มาจากไหน
- อัปเดต README.md หัวข้อ "ตั้งค่า (config.yml)" อธิบาย `effect-format` + placeholder `{rate}`
- bump เวอร์ชันเป็น 1.6.0 (`pom.xml` + `plugin.yml`)
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.6.0 สำเร็จไม่มี error (`Done (43.727s)!`), ปิดเซิร์ฟเรียบร้อยหลังทดสอบ (เซิร์ฟยังใช้ `config.yml` เก่าที่ deploy ไว้จาก v1.5.0 ที่ไม่มี `effect-format` เลย ก็ยัง enable ผ่านปกติเพราะมี fallback — ยืนยัน backward-compat จริง)
- **ยังไม่ได้เข้าเกมจริงดูว่าแก้ `effect-format` แล้วข้อความเปลี่ยนถูกจุดจริงไหม** — ที่ยืนยันคือแค่ enable ไม่มี error เท่านั้น

### อัปเดต v1.7.0 (2026-09-10) — ชื่อแยกตามระดับ (level-list) + เลขระดับแบบโรมัน
สอง feature รวดเดียวจากบทสนทนาต่อเนื่อง: ผู้ใช้บอกกลางทางที่กำลังทำ effect-format ว่า "ทำไมไม่ฟังให้จบก่อนเริ่มทำ" แล้วเผยว่าที่จริงต้องการ **ตั้งชื่อแยกได้ทุกระดับของทุกชนิดเครื่อง** (ไม่ใช่แค่แก้ format ข้อความเฉย ๆ แบบที่ตอบ AskUserQuestion ไว้ก่อนหน้าใน v1.4.0) จากนั้นถามต่อขอเปลี่ยนเลข Lv. จากอารบิก (1,2,3) เป็นโรมัน (I,II,III)

**1) ชื่อแยกตามระดับ**: `UpgradeManager.getDisplayName(type, material)` (v1.5.0) เปลี่ยนเป็น `getDisplayName(type, material, level)` — ทั้ง `display-name` และแต่ละค่าใน `display-names.<MATERIAL>` ตอนนี้ตั้งเป็น**ข้อความเดียว** (ใช้ทุกระดับเหมือนเดิม) **หรือ list แยกตามระดับ** ก็ได้ (เช็คด้วย `plugin.getConfig().isList(path)` แล้วอ่านตามนั้น index 0 = ยังไม่อัพเกรด เหมือนหลักการ list อัตรา/ราคาทั้งไฟล์) ผ่าน method ใหม่ `resolveConfiguredName(path, level)` — ลำดับ fallback: per-material level-list/string → shared display-name level-list/string → ชื่อ default ในโค้ด
- `formatText(...)`/`formatTextList(...)` เปลี่ยนไปเรียก `getDisplayName(type, material, target)` (ใช้ระดับเป้าหมายสำหรับ GUI/ไอเทมที่ดรอป)
- จุดที่เรียก `getDisplayName` ตรง ๆ (ไม่ผ่าน formatText) ต้องส่ง level ให้ตรงบริบท: `UpgradeCommand.handleInfo` ใช้ `lvl` (ระดับที่ถืออยู่ปัจจุบัน), `UpgradeConfirmGuiListener` ข้อความสำเร็จใช้ `applied` (ระดับใหม่ที่เพิ่งได้)
- อัปเดต `config.yml` ใส่ list ชื่อจริงให้ทุกเครื่อง (ธีม Copper → Iron → Gold → Diamond → Netherite ตามธรรมเนียม Minecraft) เป็นตัวอย่างใช้งานได้จริงทันที ไม่ใช่แค่ placeholder เปล่า ๆ — แก้ชื่อในนั้นได้อิสระ

**2) เลขระดับแบบโรมัน**: เพิ่ม `UpgradeManager.toRoman(int)` (public static, algorithm มาตรฐาน 13 คู่ value/symbol, เลข ≤0 พิมพ์เป็นตัวเลขปกติเพราะโรมันไม่มีเลข 0/ติดลบ) เพิ่ม placeholder `{level-roman}`/`{current-roman}`/`{max-roman}` คู่กับ `{level}`/`{current}`/`{max}` เดิมใน `formatText(...)` — **เปลี่ยนค่า default ทุกจุดที่โชว์ "Lv."** ให้ใช้เลขโรมันแล้ว: `gui.info-item.name-format`/`gui.info-item.lore`/`gui.item-name-format` ใน `config.yml` และ default string ฝั่ง Java ที่ตรงกัน, รวมถึงข้อความที่ไม่ผ่านระบบ template (`/upgrade info`, ข้อความอัพเกรดสำเร็จ/เงินไม่พอ/วัตถุดิบไม่พอ/ถึงระดับสูงสุดแล้วใน `UpgradeCommand.java`/`UpgradeConfirmGuiListener.java`) เรียก `UpgradeManager.toRoman(...)` ตรง ๆ ด้วย
- อัปเดต README.md เพิ่มหัวข้อ "ชื่อแยกตามระดับ (level-list)" และ "เลขระดับแบบโรมัน" + อัปเดตตาราง placeholder เดิมให้มีคอลัมน์โรมัน
- bump เวอร์ชันเป็น 1.7.0 (`pom.xml` + `plugin.yml`)
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.7.0 สำเร็จไม่มี error (`Done (34.186s)!`) **เซิร์ฟทดสอบยังใช้ `config.yml` เก่ามาก** (ก่อน v1.3.0 เลย ไม่มี `dropper`/`display-name`/`gui:` section ใด ๆ เลย แถมมี item price material ที่ผู้ใช้ตั้งเองอยู่ — IRON_INGOT/REDSTONE/AMETHYST_SHARD) แต่ยัง enable ผ่านปกติเพราะ fallback ทำงานถูกต้อง เป็นการยืนยัน backward-compat ที่หนักที่สุดเท่าที่เคยทดสอบมา — **ไม่ได้แก้/ลบไฟล์นั้นเพราะมีค่าที่ผู้ใช้ตั้งเองอยู่** ให้ผู้ใช้ตัดสินใจเองว่าจะขอให้ merge field ใหม่เข้าไปหรือลบให้ regenerate ใหม่
- มี error ในนี้ log ด้วยแต่ไม่เกี่ยวกับปลั๊กอินนี้: MMOItems recipe warnings + MMOItemsAmethyst enable ล้มเหลว (`ClassNotFoundException: io.lumine.mythic.lib.util.sched.Sched`) เป็นปัญหาของปลั๊กอินอื่นที่มีอยู่ก่อนแล้ว ไม่เกี่ยวกับการเปลี่ยนแปลงรอบนี้
- **ยังไม่ได้เข้าเกมจริงดูว่าชื่อแยกตามระดับ/เลขโรมันแสดงถูกต้องจริงไหม** (เพราะเซิร์ฟทดสอบใช้ config เก่าที่ไม่มี section พวกนี้เลย เห็นแค่ fallback เดิม) ถ้า session หน้าจะ verify เพิ่ม ต้องอัปเดต live config หรือลบให้ regenerate ก่อน

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
