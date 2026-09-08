# CLAUDE.md

คำแนะนำสำหรับ Claude Code (หรือใครก็ตาม) เมื่อกลับมาทำงานต่อกับโปรเจกต์นี้ในเซสชันใหม่

## โปรเจกต์นี้คืออะไร
`UpgradeMachines` — ปลั๊กอิน Paper/Purpur (Java 21, Maven) ที่เพิ่ม "อัตรา" การทำงานของ Furnace/BlastFurnace/Smoker, Hopper, Dispenser, Crafter ตามระดับอัพเกรดที่เก็บบนตัวบล็อกเอง (PersistentDataContainer) — ไม่มีไอเทม/สูตรใหม่ ไม่มีดาต้าเบสภายนอก ดูรายละเอียดกลไกทั้งหมดใน README.md

รายละเอียดความคืบหน้าล่าสุด สถานะการ build/deploy/push ให้ดูที่ **HANDOFF.md** เสมอก่อนเริ่มงานต่อ

## กฎการทำงานที่ต้องรู้
1. **ต้องทดสอบบนเซิร์ฟจริงเสมอ** ก่อนถือว่างานเสร็จ — ห้ามสรุปว่า "ใช้งานได้" จากแค่ compile ผ่าน
   เซิร์ฟทดสอบ (Purpur) อยู่ที่:
   `C:\Users\ACER\Desktop\Project\Survival SMP Purpur 26.2 test\Survival SMP Purpur 26.2 test`
   - jar ที่ build แล้วให้ก็อปไปวางที่ `plugins\UpgradeMachines.jar` ของโฟลเดอร์นี้
   - รันด้วย `run.bat` (มี plugin อื่น ๆ ติดตั้งอยู่เยอะมาก เช่น LuckPerms, Vault, WorldGuard ฯลฯ — ระวังอย่าไปยุ่งกับ config ของปลั๊กอินอื่นโดยไม่ตั้งใจ)
   - เช็ค `server_console.log` / `server_err.log` หลังสตาร์ทเพื่อดู error การโหลดปลั๊กอิน

2. **เวอร์ชันเซิร์ฟเวอร์ใช้ scheme ใหม่** ไม่ใช่ `1.21.x` แบบเดิมแล้ว — ตอนนี้ (ก.ย. 2026) Minecraft/Paper/Purpur ใช้เลขเวอร์ชันแบบ `26.x` (ปีที่ออก) เช่น `26.2`, `26.3`
   - เซิร์ฟทดสอบรัน **Purpur 26.2** (stable, build 2632)
   - `26.3` ตอนนี้มีแค่ paper-api build พรีรีลีส/อัลฟ่า (`26.3-pre-2.build.0-alpha`) — ยังไม่มี Purpur build ของ 26.3 ให้ทดสอบจริง ดูรายละเอียดการตัดสินใจเรื่องเวอร์ชันใน HANDOFF.md

3. **ไม่มี Maven ติดตั้งในระบบ (global PATH)** — ใช้ Maven แบบพกพาที่ดาวน์โหลดไว้ใน `.tools/apache-maven-*` (อยู่ใน `.gitignore` แล้ว ไม่ต้อง commit) และ**ต้อง build ด้วย JDK 25** (`C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot`) ไม่ใช่ JDK 21 — paper-api 26.2 คอมไพล์ด้วย Java 25 คำสั่ง build เต็ม ๆ ดูใน HANDOFF.md

4. **Git remote**: repo อยู่ที่ `https://github.com/xGRAFEW/UpgradeMachines` — login ผ่าน `gh` CLI (auth แล้วเป็น xGRAFEW) เวลา commit ให้ต่อท้ายข้อความด้วย Co-Authored-By ตามที่ระบบกำหนด (ดูใน session ปัจจุบันถ้ายังอยู่ใน context เดียวกัน ไม่งั้นใช้ format มาตรฐานของ Claude Code)

5. **โครงสร้างโค้ด** (`src/main/java/net/plugin/upgrademachines/`):
   - `UpgradeMachinesPlugin.java` — entrypoint, ผูก listener/command ทั้งหมด
   - `listener/FurnaceUpgradeListener.java` — batch smelting (FurnaceSmeltEvent)
   - `listener/HopperUpgradeListener.java` — extra transfers (InventoryMoveItemEvent)
   - `listener/DispenserUpgradeListener.java` — extra dispensed items (BlockDispenseEvent)
   - `listener/CrafterUpgradeListener.java` — extra crafts (CrafterCraftEvent, Paper-only API)
   - `listener/UpgradeToolListener.java` — ไม้อัพเกรด + คิดราคา (เงินผ่าน Vault + ไอเทม)
   - `util/UpgradeManager.java` — อ่าน/เขียนระดับ + resolve ค่า config (rate/price) ตามระดับ
   - `util/EconomyHook.java` — wrapper Vault (null-safe ถ้าไม่มี Vault)
   - `util/MachineType.java`, `util/UpgradeKeys.java` — enum/PDC key กลาง

## สิ่งที่ห้ามทำโดยไม่ถาม
- ห้าม force-push, ห้าม reset --hard บน repo ของผู้ใช้
- ห้ามลบ/แก้ config ของปลั๊กอินอื่นในเซิร์ฟทดสอบ (CMI, LuckPerms, WorldGuard ฯลฯ)
- การ deploy jar ไปเซิร์ฟทดสอบ = แค่ก็อปไฟล์เข้า `plugins/` ถือเป็นการทดสอบปกติ ทำได้เลยไม่ต้องถาม แต่การรันเซิร์ฟเวอร์จริง (เปิดพอร์ต ฯลฯ) ควรแจ้งผู้ใช้ก่อนถ้าจะรันแบบ background ยาว ๆ
