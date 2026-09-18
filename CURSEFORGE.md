# No Experience

> **经验，从这个世界上消失了。**
> 怪物不掉经验球，经验球进不了世界，任何途径都涨不了经验，经验条也不再显示——所有人的经验值永远是 **0**。

适合「无成长纯生存」「硬核生存」，以及用任务书（FTB Quests 等）或自定义进度取代经验升级的整合包。装上之后，玩家不再靠刷怪升级，游戏节奏完全由你设计。

---

## 这个模组做了什么

| 原版行为 | 装上 No Experience 之后 |
|---|---|
| 生物死亡掉落经验球 | 什么都不掉。末影龙、凋灵等 Boss 也一样 |
| 经验球落在地上被拾取 | 经验球无法进入世界；旧存档里已经存在的经验球会被清除 |
| 挖矿、烧炼、钓鱼、交易、酿造、经验瓶、`/xp` 指令给经验 | 全部失效，经验值不会增加 |
| 玩家累积经验值与等级 | 永远锁在 0；登录、重生、换维度之后依然是 0 |
| HUD 显示经验条与等级数字 | 不再显示 |

**定位栏（Locator Bar）、坐骑跳跃条等其它 HUD 不受影响**——它们和经验条在界面上挨得很近，但没有被误伤。

## 支持版本

| 加载器 | Minecraft | 需要的 Java |
|---|---|---|
| Forge 47.x | 1.20.1 | 17 |
| NeoForge 21.1.x | 1.21.1 | 21 |
| NeoForge 26.1.x | 26.1.2 | 25 |

选文件时请对准上面这一行：**加载器和 Minecraft 版本都要对得上**，否则游戏启动时会报缺少依赖。

## 安装

1. 先装好对应版本的加载器（Forge 或 NeoForge）。
2. 把对应版本的 jar 放进 `mods/` 文件夹。
3. **客户端和服务端都装上。**

只装客户端也能隐藏经验条，但服务端的掉落与经验获取不会被拦截；只装服务端则经验条依然会画出来。想要完整效果，两边都要装。

## 已知影响（开服前请先读）

- **附魔台与铁砧无法使用。** 它们要消耗经验等级，而等级永远是 0。这是「经验锁死」的必然结果，不是 bug。
- **经验修补（Mending）失效。** 世界里没有经验球可用。
- **依赖经验的模组会受影响。** 任何以经验为货币、为升级条件、或需要消耗等级的模组，都需要自行调整。
- **刷怪塔不再产出经验。** 掉落物照常，只是没有经验球。

## 设计取舍

经验被彻底移除，而不是「减少」或「可配置」——这是刻意的：一个可以随时打开的经验开关会让服务器经济失去意义。模组不提供配置文件，行为在所有玩家之间完全一致。

## 许可与整合包

本项目以 **MIT** 协议发布。整合包可以自由收录（CurseForge、Modrinth、FTB App），也可以自由修改与再发布，保留版权声明即可。

Minecraft 是 Mojang Synergies AB 的商标。本项目与 Mojang、Microsoft 没有隶属关系，也未获得其认可或赞助。

## 链接

- 源码与问题反馈：<https://github.com/QLNPLUS/no-experience>

---
---

# No Experience (English)

> **Experience is gone from this world.**
> Mobs drop no XP orbs, orbs never enter the world, nothing grants experience, and the experience bar is never drawn — every player's experience stays at **0**.

Built for no-progression survival, hardcore packs, and modpacks that replace XP levelling with quests or custom advancement systems.

## What it changes

| Vanilla behaviour | With No Experience |
|---|---|
| Mobs drop XP orbs on death | Nothing drops — including the Ender Dragon and the Wither |
| XP orbs are picked up | Orbs cannot enter the world; orbs already saved in old worlds are removed |
| Mining, smelting, fishing, trading, brewing, bottles o' enchanting and `/xp` grant XP | All of them stop working; experience never increases |
| Players accumulate XP and levels | Locked to 0, and re-locked after every login, respawn and dimension change |
| The HUD draws the experience bar and level number | Not drawn |

**The Locator Bar and the mount jump bar are unaffected** — they share screen space with the experience bar, but they are not hidden.

## Supported versions

| Loader | Minecraft | Java |
|---|---|---|
| Forge 47.x | 1.20.1 | 17 |
| NeoForge 21.1.x | 1.21.1 | 21 |
| NeoForge 26.1.x | 26.1.2 | 25 |

Pick the file matching **both** your loader and your Minecraft version.

## Installation

1. Install the matching loader (Forge or NeoForge).
2. Drop the jar into your `mods/` folder.
3. **Install it on both the client and the server.**

Client-only hides the experience bar but does not stop XP from dropping or being gained. Server-only locks experience but still draws the bar.

## Known effects

- **Enchanting tables and anvils stop working.** They consume experience levels, and levels are always 0. This is a direct consequence of locking XP, not a bug.
- **Mending does nothing.** There are no XP orbs to repair with.
- **Mods that depend on experience are affected.** Anything using XP as currency, as a levelling requirement, or as a level cost needs its own adjustment.
- **Mob farms no longer produce XP.** Drops are unchanged; only the orbs are gone.

## Design notes

Experience is removed, not reduced or made configurable. That is deliberate: a toggleable XP switch would defeat the point on a server. There is no config file, and behaviour is identical for every player.

## License and modpacks

Released under the **MIT** license. You may include it in modpacks (CurseForge, Modrinth, FTB App), modify it, and redistribute it, as long as the copyright notice is kept.

Minecraft is a trademark of Mojang Synergies AB. This project is not affiliated with, endorsed by, or sponsored by Mojang or Microsoft.

## Links

- Source and issue tracker: <https://github.com/QLNPLUS/no-experience>
