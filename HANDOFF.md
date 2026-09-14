# HANDOFF.md

บันทึกความคืบหน้าล่าสุดของโปรเจกต์ `UpgradeMachines` — อ่านไฟล์นี้ก่อนเริ่ม session ใหม่เสมอ
(อัปเดตล่าสุด: 2026-09-14)

### อัปเดต v1.13.0 (2026-09-14) — ปุ่มกล่อง Dialog เรียงแนวตั้ง + สรุปข้อมูลบนปุ่มยืนยัน
ผู้ใช้ตั้งกฎใหม่กลางบทสนทนา: **ต่อไปนี้ให้สรุปแผนก่อนแล้วขออนุญาตก่อนลงมือทำทุกครั้ง** (สำคัญมาก จำไว้ใช้ทุก session ต่อจากนี้) จากนั้นขอให้ปรับกล่อง Dialog: อยากให้มีแค่ "ต้องการอัพเกรดหรือไม่" เอาปุ่มยืนยันไปไว้กลางจอ ปุ่มยืนยันต้องมีรายละเอียดอัพเกรดโชว์ด้วย ส่วนปุ่มยกเลิกอยู่ใต้ปุ่มยืนยัน
- เช็ค API แล้วพบว่า `DialogType.confirmation(...)` ที่ใช้อยู่เดิมเป็น layout ตายตัวของ Minecraft เอง (ปุ่มเรียงข้างกัน) **ไม่มีทางปรับตำแหน่งผ่าน config ได้เลย** ต้องแก้โค้ดเปลี่ยนเป็น `DialogType.multiAction(...)` พร้อม `columns(1)` ถึงจะเรียงปุ่มแนวตั้งได้ — ผู้ใช้เคยขอให้แก้แบบ config-only มาก่อนหน้านี้ (รอบ spacing) เลยถามให้ชัดเจนก่อนว่ารอบนี้จำเป็นต้องอัปเดตปลั๊กอินจริง ๆ (ไม่มีทาง config-only) ผู้ใช้ถามย้ำแล้วยืนยัน "โอเคดำเนินการได้เลย"
- ถามคำถามเพิ่มด้วย AskUserQuestion ว่า text body เดิม (ระดับ/ผล/ราคา) จะเอาไปแยกไว้เฉย ๆ หรือเก็บไว้ + เพิ่มสรุปบนปุ่มด้วย — ผู้ใช้ตอบว่า **เก็บทั้ง body เดิม และเพิ่มสรุปสั้น ๆ บนปุ่มด้วย**
- แก้ `UpgradeConfirmDialog.java`: เปลี่ยน `DialogType.confirmation(confirmButton, cancelButton)` → `DialogType.multiAction(List.of(confirmButton, cancelButton)).columns(1).build()` (1 คอลัมน์ = เรียงแนวตั้ง ปุ่มแรกอยู่บน) เพิ่ม `.width(300)` ให้ทั้ง 2 ปุ่ม (กว้างขึ้นเผื่อข้อความ 2 บรรทัด)
- แก้ default `gui.dialog.confirm-label` เป็น 2 บรรทัด (`\n`): `"&a&lยืนยันอัพเกรด\n&7Lv.{level-roman} - {price}"` — โชว์ระดับเป้าหมาย+ราคาบนปุ่มโดยตรง (ปุ่มยกเลิกยังคงข้อความเดิม ไม่มีรายละเอียดเพิ่ม)
- อัปเดต config.yml ตัวอย่าง + comment ให้ตรงกับ default ใหม่
- bump เวอร์ชันเป็น 1.13.0
- **ไม่ได้ทดสอบบนเซิร์ฟทดสอบท้องถิ่นรอบนี้เช่นกัน**: เช็คแรมก่อนแล้วเหลือแค่ **1.28GB** จาก 15.36GB (แย่กว่ารอบ v1.12.1 ที่เหลือ 2.55GB ตอนนั้นแล้วโดน OS ฆ่า process) เลยข้ามการทดสอบท้องถิ่นไปเลยรอบนี้ ใช้แค่ build สำเร็จ (compile-verified) + Dialog API เดิมที่ผู้ใช้ยืนยันแล้วว่าใช้งานได้จริงในเกม (จากภาพ v1.12.0/v1.12.1) เป็นหลักฐานว่า mechanism พื้นฐานใช้ได้ — **ถ้า session หน้าเจอแรมเครื่องต่ำต่อเนื่องแบบนี้ ควรถามผู้ใช้ว่ามีโปรแกรมอื่นกินแรมเยอะอยู่ไหม (เช่น Minecraft client ของผู้ใช้เองที่รันบนเครื่องเดียวกัน) ก่อนพยายามรันเซิร์ฟทดสอบซ้ำ**
- Push + สร้าง Release `v1.13.0` แล้ว: https://github.com/xGRAFEW/UpgradeMachines/releases/tag/v1.13.0
- อัปเดตไฟล์ config ของผู้ใช้ที่ `C:\Users\ACER\Desktop\UpgradeMachines\config.yml` ให้ตรงกับ default ใหม่ด้วยแล้ว (ต้องบอกผู้ใช้ให้เอาไปวางเซิร์ฟจริง + อัปเดต jar เป็น 1.13.0 ด้วย เพราะรอบนี้ไม่ใช่ config-only)
- **ยังไม่ได้ยืนยันจากผู้ใช้ว่า layout ใหม่ตรงกับที่ต้องการจริงไหม** — รอผู้ใช้อัปเดต jar+config แล้วลองในเกม

**บันทึกกฎการทำงานสำคัญจาก session นี้ (ใช้ต่อทุก session ในโปรเจกต์นี้)**: ผู้ใช้ขอให้ **สรุปแผนงานก่อนแล้วขออนุญาตก่อนลงมือทำทุกครั้ง** ไม่ใช่ลงมือทำเลยเหมือนที่เคยทำมา (แต่ก่อนหน้านี้ทำแบบ autonomous ทำแล้วค่อยรายงานผล) — ต้องเปลี่ยนวิธีทำงานตรงนี้ตั้งแต่ตอนนี้เป็นต้นไป

### อัปเดต v1.12.1 (2026-09-14) — ระยะห่างข้อความในกล่อง Dialog แน่นขึ้น + เพิ่มสี
ผู้ใช้เข้าเกมทดสอบโหมด `dialog` จริงแล้ว (v1.12.0) ส่งภาพหน้าจอมายืนยันว่า**ใช้งานได้** (title "อัพเกรด Dropper", body "ระดับ: 0 → I/VI"/"ผล: ดรอปครั้งละ 8 ชิ้น"/"ราคา: 1,500,000.00€", ปุ่ม "ยืนยันอัพเกรด"/"ยกเลิก" ครบ) แต่ขอให้ "ตัดระเบียบเรื่องตัวอักษรให้ดูเรียบร้อยกว่านี้" — จากภาพเห็นชัดว่าแต่ละบรรทัดห่างกันมากผิดปกติ
- **สาเหตุ**: `UpgradeConfirmDialog.show()` เดิมสร้าง `DialogBody.plainMessage(...)` แยกกัน 1 อันต่อ 1 บรรทัด (list 3 บรรทัด = 3 widget) ซึ่งไคลเอนต์เว้นระยะห่างระหว่าง widget ให้เองมาก (คล้ายๆ spacing ระหว่าง element ใน UI list)
- แก้โดยรวมทุกบรรทัดเป็น `PlainMessageDialogBody` เดียว คั่นด้วย `\n` (เหมือนข้อความหลายบรรทัดบนป้าย/หนังสือ) แทน — เหลือ widget เดียว ระยะห่างระหว่างบรรทัดเลยเป็นแบบ paragraph ปกติ แน่นกว่าเดิมมาก
- เพิ่มสีให้ default body lines ด้วย (เดิมเป็นสีขาวล้วนไม่มีสีเลย) ใช้ธีมเดียวกับที่อื่นในปลั๊กอิน (label สีเทา, ค่าสีขาว/เขียว) — `&7ระดับ: &f{current-roman} &7→ &a{level-roman}&7/{max-roman}` ฯลฯ
- อัปเดต config.yml ให้ตรงกับ default ใหม่ + comment อธิบายเรื่องบรรทัดรวมเป็นข้อความเดียว, อัปเดตไฟล์ config ของผู้ใช้ที่ `C:\Users\ACER\Desktop\UpgradeMachines\config.yml` ให้ด้วย (ไฟล์นี้เป็นสำเนาที่ผู้ใช้ดึงมาจากเซิร์ฟจริงให้ช่วยแก้ ไม่ใช่เซิร์ฟทดสอบ - ผู้ใช้ต้องเอากลับไปวางที่เซิร์ฟจริงเองทุกครั้งที่แก้ให้)
- bump เวอร์ชันเป็น 1.12.1
- **ไม่ได้ทดสอบบนเซิร์ฟทดสอบท้องถิ่นรอบนี้**: ตอน deploy+รันเซิร์ฟทดสอบ ระบบ (OS) ฆ่า process เพราะ**เครื่องแรมเหลือน้อยมาก** (เช็คด้วย PowerShell แล้วเหลือ Free ~2.55GB จาก 15.36GB ตอนนั้น) ตัดสินใจข้ามการทดสอบท้องถิ่นรอบนี้เพราะเป็นการแก้เล็กน้อยที่ build ผ่านแล้ว (compile-verified) ไม่อยากไปแย่งแรมเครื่องที่กำลังตึงอยู่ซ้ำ — **ถ้า session หน้าเจอเซิร์ฟทดสอบ start ไม่ติด/โดนฆ่าเอง ให้เช็คแรมเครื่องก่อนเป็นอันดับแรก** (`Get-CimInstance Win32_OperatingSystem`) ก่อนสงสัยเรื่องโค้ด/config
- Push + สร้าง Release `v1.12.1` แล้ว: https://github.com/xGRAFEW/UpgradeMachines/releases/tag/v1.12.1
- **ยังไม่ได้ยืนยันจากผู้ใช้ว่าหน้าตาใหม่โอเคหรือยัง** — รอผู้ใช้อัปเดต jar+config บนเซิร์ฟจริงแล้วลองดู

### อัปเดต v1.12.0 (2026-09-13) — เพิ่มโหมด Dialog (กล่องวานิลลาจริง ไม่ต้องมี resource pack)
ต่อจาก v1.11.0 (โหมด actionbar) ผู้ใช้ทดสอบแล้วบอกว่า action bar ธรรมดา "ไม่เหมือนกันเลย" กับภาพเซิร์ฟอื่นที่มีกรอบสวยงาม — วิเคราะห์ภาพอย่างละเอียดแล้วสรุปว่ากล่องมีกรอบนั้นน่าจะมาจาก resource pack ของเซิร์ฟนั้น (ไม่ใช่สิ่งที่ปลั๊กอินทำเองได้ตรง ๆ) ถามผู้ใช้ว่าเซิร์ฟตัวเองมี resource pack บังคับโหลดอยู่ไหม (ตอบว่าไม่มี/ไม่แน่ใจ) แล้วถามว่าอยากไปทางไหนต่อ (ตอบไม่มีข้อเลือก 2 รอบ) — ระหว่างนั้นผู้ใช้ให้หยุดรอก่อน ("เดี๋ยวก่อน"/"อย่าพึ่งทำอะไร") แล้วกลับมาถามว่า **"แก้ให้เป็น Dialog GUI ได้ไหมครับ"**
- เช็คด้วย `jar tf`/`javap` บน paper-api 26.2 แล้วพบว่า **Paper มีฟีเจอร์ "Dialog" แบบวานิลลาเต็มรูปแบบอยู่แล้ว** (`io.papermc.paper.dialog.Dialog`, `ConfirmationType`, `ActionButton`, `DialogBody` ฯลฯ) — เป็นฟีเจอร์เกมจริง (Minecraft's Dialogs) ไม่ใช่ inventory hack เรียกผ่าน `Player#showDialog(DialogLike)` (มาจาก Adventure `Audience`, เช็คแล้วว่า `adventure-api` เวอร์ชันที่ paper-api 26.2 ใช้จริงคือ **5.2.0** ซึ่งมี `showDialog`/`closeDialog`) — นี่คือคำตอบที่ตรงกับสิ่งที่ผู้ใช้ต้องการเป๊ะ: กล่องมีกรอบสวยงามให้เองโดยไม่ต้องมี resource pack เลย แถมมีปุ่มยืนยัน/ยกเลิกจริง (ไม่ใช่ item ใน inventory)
- เพิ่มไฟล์ใหม่ **`gui/UpgradeConfirmDialog.java`**: สร้าง `DialogBase` (title + body หลายบรรทัด) + `DialogType.confirmation(yesButton, noButton)` แล้วเรียก `player.showDialog(...)` — ปุ่มแต่ละอันใช้ `DialogAction.customClick(callback, ClickCallback.Options)` (single-use, `uses(1)`) ปุ่มยืนยันเรียก `UpgradeExecutor.upgrade(...)` ตรง ๆ (ใช้ตัวเดียวกับ GUI/action-bar) ปุ่มยกเลิกแค่ส่งข้อความ — ข้อความทั้งหมด (title/body/ปุ่ม) ยังคงผ่านระบบ `formatText`/placeholder เดิมทุกตัว (`{name}`/`{level-roman}`/`{effect}`/`{price}` ฯลฯ) แค่แปลงจาก legacy `&`/`§` string เป็น Adventure `Component` ด้วย `LegacyComponentSerializer.legacySection()` ก่อนส่ง (เช็คแล้วว่า `adventure-text-serializer-legacy:5.2.0` มีอยู่ใน dependency tree แล้วเป็น `provided` ไม่ต้องเพิ่มอะไรใน pom.xml)
- Refactor `UpgradeManager.isActionBarStyle()` (boolean) → `ConfirmStyle` enum (`INVENTORY`/`ACTIONBAR`/`DIALOG`) ผ่าน `getConfirmStyle()` ใหม่ ให้รองรับ 3 โหมดแทน 2
- Refactor `UpgradeToolListener.onInteract()`: รวม logic เช็คสิทธิ์ + max-level (เดิมซ้ำกันระหว่าง inventory/actionbar) เป็นจุดเดียว คำนวณ current/target/max ครั้งเดียวแล้ว switch ตาม style แทน (ลดโค้ดซ้ำ)
- เพิ่ม `gui.dialog.title-format` / `gui.dialog.body` (list) / `gui.dialog.confirm-label` / `gui.dialog.cancel-label` ใน config.yml + comment อธิบาย 3 โหมดทั้งหมด, อัปเดต README.md ส่วน "โหมดยืนยันอัพเกรด" ให้ครอบคลุมทั้ง 3 แบบ
- bump เวอร์ชันเป็น 1.12.0
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.12.0 สำเร็จไม่มี error (`Done (59.439s)!` รอบนี้ช้ากว่าปกติเล็กน้อย แต่ไม่มี error ใด ๆ เกี่ยวกับปลั๊กอิน) — Monitor tool ที่ใช้เช็ค log timeout ไปเองไม่ตรงจังหวะ (grep pattern อาจมีปัญหากับเครื่องหมาย `?` ใน log) แต่เช็คไฟล์ log ตรง ๆ ด้วย grep ปกติแล้วยืนยันว่า enable สำเร็จจริง
- **ยังไม่ได้เข้าเกมจริงทดสอบโหมด `dialog`** (ต้องมีผู้เล่นจริง ตั้ง `gui.style: "dialog"` แล้วลอง sneak+คลิกขวา) — สิ่งที่ควรเช็คเป็นพิเศษ: ไคลเอนต์เวอร์ชันของผู้เล่นต้องรองรับฟีเจอร์ Dialogs (ผู้เล่นบางคนอาจต่อผ่าน ViaVersion จากเวอร์ชันเก่ากว่าซึ่งอาจไม่รองรับ Dialogs เลย ต้องดูว่า fallback เป็นยังไงถ้าไคลเอนต์ไม่รองรับ)

### อัปเดต v1.11.0 (2026-09-13) — เพิ่มโหมด Action Bar สำหรับมือถือ
ผู้ใช้ถามกลางบทสนทนา (จากภาพหน้าจอเซิร์ฟอื่น) ว่าหน้าต่างอัพเกรดแบบ action-bar/HUD prompt เรียกว่าอะไร สะดวกกับมือถือไหม ตอบไปว่าน่าจะสะดวกกว่าจริงเพราะไม่ต้องเล็งแตะช่องเล็ก ๆ ในกริด แล้วผู้ใช้ขอให้ทำให้ปลั๊กอินนี้มีโหมดแบบนั้นด้วย
- เพิ่ม config ใหม่ `gui.style`: `"inventory"` (default, พฤติกรรมเดิมทุกอย่างไม่เปลี่ยน) หรือ `"actionbar"` (โหมดใหม่)
- โหมด `actionbar`: sneak+คลิกขวาครั้งแรกที่บล็อก **ไม่เปิดกล่อง 27 ช่องแล้ว** ขึ้นข้อความ prompt ที่แถบล่างจอแทน (`gui.actionbar.prompt-format` ใช้ placeholder ชุดเดียวกับที่อื่นหมด) แล้ว sneak+คลิกขวาซ้ำที่**บล็อกเดิม**ภายใน `gui.actionbar.timeout-seconds` วินาที (default 6) ถือเป็นการยืนยันทันที ไม่มี GUI เลย
- เก็บ pending confirmation ต่อผู้เล่นใน `Map<UUID, PendingConfirm>` (record เก็บ block + เวลาหมดอายุ) ภายใน `UpgradeToolListener` เอง คลิกที่บล็อกอื่น หรือคลิกช้าเกินกำหนด = เริ่ม prompt ใหม่ (ถือเป็นการยกเลิกโดยปริยาย ไม่มีปุ่มยกเลิกชัดเจนในโหมดนี้)
- **Refactor ระหว่างทาง** (ลดโค้ดซ้ำ ไม่ใช่แค่เพิ่มฟีเจอร์เฉย ๆ): ย้าย logic การทำธุรกรรมจริง (เช็คสิทธิ์/ราคา/max-level, หักเงิน/ไอเทม, setLevel, ข้อความแชท) ที่เดิมอยู่ใน `UpgradeConfirmGuiListener.confirmUpgrade()` ออกมาเป็นคลาสใหม่ **`util/UpgradeExecutor.java`** (static method `upgrade(...)`) ให้ทั้งปุ่มยืนยันใน GUI และการคลิกซ้ำแบบ action-bar เรียกใช้ร่วมกัน ไม่ต้องมี logic ซ้ำ 2 ที่ — เช็คแล้วว่าข้อความ/เงื่อนไขเหมือนเดิมทุกตัวอักษร ไม่มีอะไรเปลี่ยนพฤติกรรมโหมด `inventory` เดิมเลย
- ย้าย `UpgradeConfirmGui.priceSummary()` (private) ออกมาเป็น `UpgradeManager.formatPrice()` (public) ให้ action-bar prompt เรียกสร้าง `{price}` แบบเดียวกับกล่อง GUI ได้โดยไม่ต้องเขียนซ้ำ
- เพิ่ม `gui.style`/`gui.actionbar.*` ใน config.yml พร้อม comment, อัปเดต README.md เพิ่มหัวข้อ "โหมด Action Bar (เหมาะกับมือถือ)"
- bump เวอร์ชันเป็น 1.11.0
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.11.0 สำเร็จไม่มี error (`Done (34.243s)!`) — **หมายเหตุ**: เซิร์ฟทดสอบยังใช้ config.yml เก่าที่ไม่มี `gui:` section เลย เท่ากับทดสอบแค่ path `gui.style` default `"inventory"` เท่านั้น (ค่า default จาก Java ไม่ใช่จาก config) **ยังไม่เคยรันโค้ด branch `actionbar` เลยแม้แต่ครั้งเดียว** เพราะต้องมีผู้เล่นจริง sneak+คลิกขวาถึงจะเข้า logic นี้ — ถ้า session หน้าจะ verify ต้องตั้ง `gui.style: "actionbar"` ในเซิร์ฟจริงแล้วให้ผู้เล่นทดสอบคลิก 2 ครั้ง

### อัปเดต v1.10.1 (2026-09-13) — แก้สีราคาในกล่องยืนยันอัพเกรดถูกบังคับเป็นสีขาวเสมอ
ผู้ใช้ส่งภาพหน้าจอ title กล่องยืนยันอัพเกรด ราคาขึ้นสีขาวเสมอถามว่าเปลี่ยนสีไม่ได้เหรอ (เซิร์ฟ/config ของผู้ใช้เองอีกแล้ว ไม่ใช่เซิร์ฟทดสอบ - เห็นจากตัวเลขราคา 1,800,000€ ที่ไม่ตรงกับ config เซิร์ฟทดสอบ)
- **สาเหตุ**: `UpgradeConfirmGui.priceSummary()` hardcode `§f` (ขาว) ไว้หน้าสุดของค่า `{price}` เสมอ (+ `§a` ตอน "ฟรี", `§7` ที่ตัวคั่น) ไม่ว่า template (`title-format`/lore) จะใส่โค้ดสีอะไรไว้ก่อน `{price}` ก็โดน `§f` นี้ทับสีขาวกลับทุกที — เป็นบั๊กเดียวกันแนวคิดกับที่แก้ไปตอน v1.8.0 (สีในชื่อไม่ทำงานเพราะ substitute หลัง translate) แต่รอบนี้เป็นสี hardcode ในค่าที่ยัดเข้า `{price}` เอง ไม่ใช่เรื่อง order ของ formatText
- แก้ `priceSummary()`: เอา `§f` หน้าสุดออกทั้งหมด (ตัวเลขเงิน/ไอเทมไม่มีสีบังคับของตัวเองแล้ว จะสืบทอดสีจาก template ที่อยู่ก่อนหน้า `{price}`) ส่วนข้อความ "ฟรี" กับตัวคั่น "+" ยังคงมีสีของตัวเองแยกต่างหาก แต่ทำให้**ตั้งค่าได้**ผ่าน config ใหม่ `gui.price-free` (default `&aฟรี`) และ `gui.price-separator` (default `&7 + `)
- เปลี่ยน `UpgradeManager.translateColors()` จาก private เป็น public static ให้ `UpgradeConfirmGui` เรียกใช้ colorize ค่า price-free/price-separator ได้ตรง ๆ (รองรับ hex `&#RRGGBB` ด้วยเหมือนที่อื่น)
- อัปเดต README.md + comment ใน config.yml อธิบาย `{price}` ไม่มีสีบังคับแล้ว + field ใหม่ 2 ตัว, เพิ่ม `{rate}` เข้าไปในลิสต์ placeholder ของ comment ใน config.yml ด้วย (ตกหล่นจาก v1.9.0)
- bump เวอร์ชันเป็น 1.10.1
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.10.1 สำเร็จไม่มี error (`Done (38.692s)!`)
- **ยังไม่ได้เข้าเกมจริงยืนยันว่าเปลี่ยนสี `{price}` ผ่าน template ได้จริงไหม** (เซิร์ฟทดสอบไม่มี `gui:` section ให้ทดสอบเหมือนเดิม)
- **หมายเหตุ**: ผู้ใช้ถามคำถามแยกกลางบทสนทนาเรื่อง UI แบบ action-bar/HUD prompt (ภาพหน้าจอ "คลิกเพื่ออัปเกรดเป็น: 16 ในราคา: 3,500,000") ถามว่าเรียกว่าอะไร/ง่ายสำหรับมือถือไหม — ตอบไปว่าน่าจะเป็นปลั๊กอินอื่นบนเซิร์ฟผู้ใช้ (ไม่ใช่ UpgradeMachines ซึ่งใช้ inventory GUI 27 ช่อง) ถ้าผู้ใช้อยากให้เปลี่ยน UI ของปลั๊กอินนี้เป็นสไตล์ action-bar เพื่อรองรับมือถือ ต้องคุยรายละเอียดเพิ่มเป็นงานแยกต่างหาก ยังไม่ได้เริ่มทำอะไร

### อัปเดต v1.10.0 (2026-09-13) — ชื่อกล่อง GUI ของบล็อกเองอัปเดตทันทีตอนอัพเกรด
ผู้ใช้ส่งภาพหน้าจอเปิดกล่อง **Crafter แบบวานิลลา** (ไม่ใช่กล่องยืนยันอัพเกรดของเรา) หลังอัพเกรดสำเร็จเป็น Lv.1 (เห็นข้อความแชท "อัพเกรด คราฟเตอร์ สำเร็จ!") title กล่องยังขึ้น "Crafter" เฉย ๆ ไม่เปลี่ยน แล้วบอกว่า "หลังอัพเกรดถ้าหากไม่ทุบวางใหม่จะขึ้นชื่อแบบเดิม"
- **สาเหตุ**: Furnace/Hopper/Dispenser/Dropper/Crafter ทุกตัว implement `org.bukkit.Nameable` อยู่แล้ว (ผ่าน `Container` → `LockableTileState` → `Nameable` เช็คด้วย `javap` บน paper-api 26.2 ยืนยันแล้ว) ซึ่งเป็นตัวคุม title ของกล่อง GUI วานิลลาของบล็อกนั้น (`CustomName` NBT) — `setLevel()` เดิมไม่เคยแตะ `Nameable` เลย เซ็ตแค่ PDC level เท่านั้น ชื่อกล่องเลยไม่เปลี่ยนตอนอัพเกรด "ในที่" (sneak+คลิกขวา) มันเปลี่ยนได้แค่ตอน**ทุบ**เท่านั้นเพราะเป็น**ผลข้างเคียงของวานิลลาเอง**: ตอนทุบเราสร้างไอเทมที่มี custom displayName แล้ววานิลลามีกลไก "เอาไอเทมที่ตั้งชื่อไปวาง → บล็อกใหม่ได้ CustomName ตามชื่อไอเทม" อยู่แล้ว (เหมือนตั้งชื่อหีบด้วยทั่งแล้ววาง) ปลั๊กอินนี้ไม่เคยรู้เรื่อง mechanic นี้เลย มันเป็นผลพลอยได้ล้วน ๆ
- แก้ `UpgradeManager.setLevel()`: เพิ่ม `if (tile instanceof Nameable nameable) nameable.setCustomName(...)` เซ็ต/เคลียร์ชื่อบล็อกไปพร้อมกับ PDC level ทุกครั้งที่เรียก (ครอบคลุมทั้งตอนกดยืนยันอัพเกรดใน GUI และตอนวางไอเทมที่มีระดับติดมา — `onPlace` ก็เรียก `setLevel()` เหมือนกัน)
- เพิ่ม `UpgradeManager.getItemDisplayName(type, material, level)` (public) ดึง logic ประกอบชื่อจาก `item-name-format` ออกมาเป็น method กลาง ใช้ร่วมกันทั้ง `setLevel()` (ตั้งชื่อบล็อก) และ `UpgradeItemPersistenceListener.createUpgradedItem()` (ตั้งชื่อไอเทมตอนดรอป) — ชื่อบล็อกกับชื่อไอเทมจึงเป็นฟอร์แมตเดียวกันเป๊ะ ไม่ต้องคำนวณซ้ำ 2 ที่
- อัปเดต README.md เพิ่มหัวข้อย่อย "ชื่อกล่อง GUI ของบล็อกเอง"
- bump เวอร์ชันเป็น 1.10.0
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.10.0 สำเร็จไม่มี error (`Done (33.924s)!`)
- **ยังไม่ได้เข้าเกมจริงยืนยันว่า title กล่องเปลี่ยนทันทีจริงไหมหลังแก้** — ผู้ใช้ควรลองอัพเกรดบล็อกแล้วเปิดกล่องดูโดยไม่ต้องทุบวางใหม่

### อัปเดต v1.9.0 (2026-09-13) — เพิ่ม `{rate}` เป็น placeholder ใช้ได้ทุกที่
ผู้ใช้ส่งภาพหน้าจอ item tooltip โชว์ `Hopper I → {rate}` (ตัวหนังสือ `{rate}` ไม่ถูกแทนที่เลย) — พบว่า `{rate}` เดิมทำงานแค่**ภายใน** `effect-format` เท่านั้น (ใช้ประกอบเป็น `{effect}`) แต่ `formatText()` ที่ใช้กับ `item-name-format`/`gui.*` ทั้งหมดไม่รู้จัก `{rate}` เลย ทำให้เอาไปใส่ตรง ๆ ใน template อื่นแล้วโชว์เป็นตัวอักษรดิบ (จากภาพ ผู้ใช้มีเซิร์ฟ/config ของตัวเองแยกต่างหากที่ตั้งชื่อเครื่องเป็นภาษาอังกฤษ ไม่ใช่เซิร์ฟทดสอบที่ session นี้ใช้ - `config.yml` บนเซิร์ฟทดสอบยังเป็นแบบเก่าไม่มี `gui:` section เหมือนเดิม)
- แยก logic คำนวณตัวเลข rate ออกจาก `describeEffect()` เป็น method ใหม่ `UpgradeManager.getRate(MachineType, level)` (public) แล้วให้ `describeEffect()` เรียกใช้แทน
- เพิ่ม `.replace("{rate}", String.valueOf(getRate(type, target)))` เข้าไปใน `formatText()` เหมือน placeholder อื่น ๆ ทำให้ `{rate}` ใช้ได้ทุกที่ที่ `formatText`/`formatTextList` ถูกเรียก (title/info-item/confirm-button/cancel-button/item-name-format/item-lore-format) ไม่ใช่แค่ใน `effect-format` อีกต่อไป
- อัปเดต README.md เพิ่มแถว `{rate}` ในตาราง placeholder อธิบายว่าต่างจาก `{effect}` ยังไง (ตัวเลขดิบ ไม่มีข้อความรอบข้าง)
- bump เวอร์ชันเป็น 1.9.0 (placeholder ใหม่)
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.9.0 สำเร็จไม่มี error (`Done (34.956s)!`)
- **ยังไม่ได้เข้าเกมจริงยืนยันว่า `{rate}` ใน `item-name-format` แสดงตัวเลขถูกต้องไหม** (เซิร์ฟทดสอบไม่มี `gui:` section ให้ทดสอบ ผู้ใช้ต้องลองในเซิร์ฟของตัวเองที่ตั้ง `gui.item-name-format` ไว้)

### อัปเดต v1.8.1 (2026-09-13) — แก้ชื่อไอเทมไม่ติดตอนทุบในโหมด Creative
ผู้ใช้รายงานว่า: วางบล็อกใหม่ → อัพเกรดเป็น Lv.1 → ทุบ → ไอเทมที่ได้ยังเป็นชื่อวานิลลาอยู่ ต้อง "ทุบวางใหม่" ถึงจะเปลี่ยนชื่อ ถามด้วย AskUserQuestion 2 ข้อ (ลำดับขั้นตอนเป๊ะ ๆ + gamemode) ผู้ใช้ตอบว่าทดสอบใน **Creative** และ "หลังอัพเกรดต้องทุบไอเทมที่อัพเกรดก่อนชื่อไอเทมจึงจะเปลี่ยน"
- **สาเหตุ**: วานิลลา Creative mode ทุบบล็อกด้วยการคลิกซ้ายปกติ (ไม่ใช่ pick block) จะ**ไม่ดรอปไอเทมเลย** (`BlockBreakEvent#isDropItems() == false`) — โค้ดเดิมใน `UpgradeItemPersistenceListener.onBreak()` เช็ค `if (!event.isDropItems()) return;` เป็นด่านแรกเลย ทำให้ตอน Creative โค้ดเปลี่ยนชื่อ/ฝังระดับไม่ทำงานเลยตั้งแต่ v1.2.0 (ระดับอัพเกรดหายไปเงียบ ๆ ไม่มีไอเทมออกมาเลยด้วยซ้ำจากมุมมองปลั๊กอินนี้ - ไอเทมที่ผู้ใช้เห็นได้มาจากทางอื่น เช่น pick block ที่ปลั๊กอินนี้ไม่มีทางดักได้)
- แก้ให้แยกเป็น 2 กรณีแทนการ return เฉย ๆ: ถ้า `isDropItems()==true` (survival ปกติ) ทำเหมือนเดิม (ดรอปไอเทมของเราเองแทนของวานิลลา) แต่ถ้า `isDropItems()==false` **และ** ผู้เล่นอยู่ gamemode Creative → มอบไอเทมที่ตั้งชื่อ/ฝังระดับแล้วเข้ากระเป๋าผู้เล่นตรง ๆ แทน (ไม่งั้นระดับจะหายไปเฉย ๆ) — ถ้า `isDropItems()==false` เพราะเหตุผลอื่นใน survival (เช่น ทุบ hopper/dispenser โดยไม่ถือจอบ/pickaxe ที่เหมาะสม → วานิลลาไม่ดรอปอะไรเลยตามปกติ) ยังคง return เฉย ๆ เหมือนเดิม ไม่ให้ของฟรีข้ามกฎเครื่องมือของวานิลลา
- อัปเดต README.md เพิ่มหัวข้อย่อย "โหมด Creative" อธิบายพฤติกรรมใหม่
- bump เวอร์ชันเป็น 1.8.1
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.8.1 สำเร็จไม่มี error (`Done (34.917s)!`)
- **ยังไม่ได้เข้าเกมจริงทดสอบ Creative break ตามที่ผู้ใช้รายงาน** (ต้องมีผู้เล่นจริงในเกม) — ที่ยืนยันคือ enable ไม่มี error เท่านั้น ถ้า session หน้ามีโอกาสควรลองในเกม: วางบล็อก → sneak+คลิกขวาอัพเกรด → ทุบตอนอยู่ Creative → เช็คว่าได้ไอเทมชื่อถูกต้องเข้ากระเป๋าทันทีไหม

### อัปเดต v1.8.0 (2026-09-13) — รองรับโค้ดสี hex (`&#RRGGBB`) + แก้สีในชื่อไม่ทำงาน
ผู้ใช้ส่งภาพหน้าจอ item tooltip ในเกมที่โชว์ข้อความ `&#4D60FFFURNACE II ➤ 16 ➜ I` เป็นตัวอักษรดิบ (โค้ดสีไม่ถูกแปลงเป็นสีจริง) แล้วขอให้รองรับโค้ดสี hex
- เพิ่ม `UpgradeManager.translateColors(String)`: แปลง `&#RRGGBB` (regex `&#([A-Fa-f0-9]{6})`) เป็น legacy hex sequence ของ Minecraft เอง (`§x§R§R§G§G§B§B` ผ่าน `ChatColor.COLOR_CHAR`) ก่อนเรียก `ChatColor.translateAlternateColorCodes('&', ...)` ตามปกติสำหรับโค้ดสีเดี่ยว — **หมายเหตุ**: `org.bukkit.ChatColor.of(String)` ที่ปกติใช้ทำแบบนี้ได้ตรง ๆ **ไม่มีอยู่ใน paper-api 26.2** (เช็คด้วย `javap` แล้วพบว่า `ChatColor` เหลือแค่ enum 16 สี + format ไม่มี `of()`) เลยต้องสร้าง sequence เองแทน
- ระหว่างแก้เจอบั๊กเดิมอีกจุด: `formatText()` เดิมเรียก `translateAlternateColorCodes` บน **template** ก่อน ค่อย `.replace("{name}", ...)` ทีหลัง ทำให้โค้ดสี (ทั้ง `&#hex` และ `&a` ปกติ) ที่ผู้ใช้ใส่ไว้ **ใน `display-name`/`display-names` เอง** ไม่เคยถูกแปลงเลยตั้งแต่ v1.4.0 (เห็นแค่โค้ดสีที่อยู่ใน template รอบนอกเท่านั้นที่ทำงาน) — แก้โดยสลับลำดับเป็น substitute placeholder ทั้งหมดก่อน แล้วค่อย `translateColors()` ทับทั้งสตริงทีเดียวตอนท้าย (ใช้ helper เดียวกับข้างบน) แก้ทั้ง `formatText()` และ `describeEffect()`
- อัปเดต README.md อธิบาย `&#RRGGBB` ใช้ปนกับโค้ดสีปกติได้ และใส่ในค่า `display-name` เองได้ไม่ต้องอยู่ใน template เท่านั้น
- bump เวอร์ชันเป็น 1.8.0 (ฟีเจอร์ใหม่)
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.8.0 สำเร็จไม่มี error (`Done (42.984s)!`)
- **ยังไม่ได้เข้าเกมจริงดูว่า hex color จริง ๆ เรนเดอร์ถูกไหม** (เซิร์ฟทดสอบยังใช้ config.yml เก่าที่ไม่มี field ให้ใส่ hex เลย ไม่ได้แก้ให้เพราะมีค่าที่ผู้ใช้ตั้งเองอยู่ - ดูเหตุผลเดียวกับ v1.7.0) ถ้า session หน้ามีโอกาสควรลองใส่ `&#RRGGBB` ในชื่อ/format จริงแล้วเข้าเกมดู
- **หมายเหตุเรื่อง `run.bat`**: ยังใช้วิธีเรียก `java.exe` ตรง ๆ (ข้าม `run.bat`) เหมือน v1.7.1 เพราะปัญหาเดิมที่ `cmd /c run.bat` ผ่าน Git Bash ยังไม่หาย

### อัปเดต v1.7.1 (2026-09-12) — แก้จุดที่ยังโชว์เลขอารบิกหลงเหลือจาก v1.7.0
ผู้ใช้ขอให้เช็ค/แก้ `{level-roman}` ให้รันเป็นเลขโรมันครบ — ตรวจสอบพบว่า v1.7.0 เปลี่ยนไปใช้เลขโรมันครบเกือบทุกจุดแล้วจริง (`{level-roman}` ใน config.yml/formatText ทำงานถูกต้อง) ยกเว้นจุดเดียวที่ตกหล่น: **`UpgradeToolListener.java`** บรรทัดข้อความ "บล็อกนี้อยู่ที่ระดับสูงสุดแล้ว" (ตอน sneak+คลิกขวาที่บล็อกซึ่งเลเวลสูงสุดแล้ว ก่อนจะเปิด GUI ยืนยัน) ยังใช้ `current`/`max` แบบอารบิกตรง ๆ อยู่ (ข้อความเดียวกันใน `UpgradeConfirmGuiListener.java` แก้เป็นโรมันไปแล้วตั้งแต่ v1.7.0 แต่ไฟล์นี้หลุดไป)
- แก้ให้เรียก `UpgradeManager.toRoman(current)`/`toRoman(max)` เหมือนจุดอื่น ๆ ทั้งหมดแล้ว — grep ทั้งโปรเจกต์อีกรอบ ยืนยันไม่มีจุดไหนเหลือเลขอารบิกแล้ว
- bump เวอร์ชันเป็น 1.7.1 (`pom.xml` + `plugin.yml`)
- ทดสอบจริงบนเซิร์ฟ Purpur 26.2 test แล้ว — โหลด/enable v1.7.1 สำเร็จไม่มี error (`Done (46.898s)!`), ปิดเซิร์ฟหลังทดสอบ (ครั้งนี้ `taskkill` แบบไม่ `/F` ขึ้น error "can only be terminated forcefully" เพราะรัน java.exe ตรง ๆ ไม่ผ่าน `run.bat`/wrapper คอนโซล เลยต้องใช้ `/F` แทน — ถ้า session หน้าเจอแบบเดียวกันให้ใช้ `/F` ได้เลยไม่ต้องพยายามแบบไม่ force ก่อน)
- **หมายเหตุสำคัญเรื่อง `run.bat`**: รอบนี้เรียก `cmd /c run.bat` (ทั้งแบบ cd ก่อนแล้ว `&&`, ทั้งแบบใส่ path เต็ม) ผ่าน Git Bash ไม่สำเร็จเลย ขึ้น `'run.bat' is not recognized as an internal or external command` ทุกครั้ง (เหตุผลไม่ชัดเจน — cwd ที่เห็นจาก `pwd` ถูกต้องแล้วแต่ cmd หาไฟล์ไม่เจอ) แก้ปัญหาโดย **ข้าม `run.bat` ไปเลย เรียก `java.exe` ตรง ๆ** ด้วย flag ชุดเดียวกับที่ `run.bat` ใช้ (ดูใน HANDOFF.md หัวข้อ "เซิร์ฟทดสอบ" ถ้าต้องการ flag เต็ม) — ถ้า session หน้าเจอปัญหาเดียวกัน ให้ข้าม `run.bat` แล้วเรียก java ตรงได้เลย ไม่ต้องเสียเวลา debug cmd/run.bat ซ้ำ
- Push + สร้าง Release `v1.7.1` แล้ว: https://github.com/xGRAFEW/UpgradeMachines/releases/tag/v1.7.1
- **ยังไม่ได้เข้าเกมจริงดูข้อความนี้โดยตรง** (ต้องมีบล็อกที่อัพเกรดถึง max-level แล้วลอง sneak+คลิกขวาซ้ำ) — ที่ยืนยันคือ enable ไม่มี error เท่านั้น เหมือนหลาย ๆ รอบก่อนหน้า

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
