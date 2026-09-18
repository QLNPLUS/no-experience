# No Experience

一个把经验（XP）从游戏里彻底移除的 Minecraft 模组：怪物不再掉经验球，玩家再也无法获得经验，所有人的经验值被锁定为 0，客户端也不再显示经验条。

同一个仓库里有三个版本分支，代码各自独立构建、锁步发布。

## 功能

| 功能 | 说明 |
|---|---|
| 移除经验掉落 | 所有生物（含末影龙、凋灵、玩家）死亡时不再掉落经验球 |
| 移除经验球 | 任何途径生成的经验球都无法进入世界；旧存档里已存在的经验球在区块加载时被清除 |
| 移除经验获取 | 挖矿、烧炼、钓鱼、交易、酿造、经验瓶、`/xp` 指令等一切给玩家加经验的途径全部被拦截 |
| 锁定经验为 0 | 每 tick 兜底检查，把 `experienceLevel` / `experienceProgress` / `totalExperience` 归零并同步给客户端 |
| 隐藏经验条 | 客户端不再绘制经验条与等级数字 |

## 支持版本

| 分支 | 加载器 | Minecraft | 需要的 Java |
|---|---|---|---|
| `forge-1.20.1` | Forge 47.x | 1.20.1 | 17 |
| `neoforge-1.21.1` | NeoForge 21.1.x | 1.21.1 | 21 |
| `neoforge-1.26.1.2` | NeoForge 26.1.x | 26.1.2 | 25 |

安装：把对应分支构建出的 jar 放进 `mods/` 目录。服务端与客户端都建议安装——服务端的部分负责真正锁住经验，客户端的部分负责隐藏经验条。

## 已知行为

- **附魔台与铁砧将无法使用**：它们消耗经验等级，而等级永远是 0。这是"经验锁死"的必然结果，不是 bug。如果你希望这些功能继续可用，需要另行调整（目前不在本模组范围内）。
- **经验修补（Mending）失效**：没有经验球可用。
- **定位栏（Locator Bar）与坐骑跳跃条不受影响**：26.1.2 里它们和经验条共用同一条"情境信息条"，本模组只屏蔽经验那一种状态。

## 构建

每个版本分支用自己的 Gradle wrapper 构建，互不干扰：

```powershell
# Forge 1.20.1（主分支）
cd D:\projects\no_experience\forge-1.20.1
.\gradlew.bat build          # 产物：build\libs\no_experience-forge-1.20.1-1.0.0.jar

# NeoForge 1.21.1
cd D:\projects\no_experience\neoforge-1.21.1
.\gradlew.bat build          # 产物：build\libs\no_experience-neoforge-1.21.1-1.0.0.jar

# NeoForge 26.1.2
cd D:\projects\no_experience\neoforge-1.26.1.2
.\gradlew.bat build          # 产物：build\libs\no_experience-neoforge-26.1.2-1.0.0.jar
```

每个分支的 `gradle/gradle-daemon-jvm.properties` 已经指定了构建所需的 JDK（21 / 21 / 25），Gradle 会自己寻找本机已安装的对应 JDK，因此从任意终端直接运行 `gradlew.bat` 即可。若本机缺少对应 JDK，请先安装，或临时设置 `JAVA_HOME`。

跨版本改动一律用 `git cherry-pick -x`，不要手工重写；分支矩阵与迁移纪律见 `AGENTS.md`。

## 下载

| 渠道 | 地址 |
|---|---|
| CurseForge | <https://www.curseforge.com/minecraft/mc-mods/no-experience> |
| GitHub Releases | <https://github.com/QLNPLUS/no-experience/releases> |

GitHub 的 tag 格式为 `v<版本>-<加载器>-<mc版本>`（例如 `v1.0.0-forge-1.20.1`），每个 tag 对应一条版本分支的构建产物；CurseForge 上是同样的三个文件，按加载器与 MC 版本区分。

## 许可

MIT，详见 `LICENSE`。整合包可以自由收录，保留版权声明即可。

Minecraft 是 Mojang Synergies AB 的商标，本项目与 Mojang / Microsoft 无隶属关系。
