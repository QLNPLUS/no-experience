# AGENTS.md — No Experience 多版本仓库约定

**本文件在每条分支上都有一份，且内容必须逐字节相同。** linked worktree 的 `.git` 是个文件，同样被当作项目根标记，所以在某个版本工作树里工作时，项目根就是那个工作树本身，放在项目文件夹根或只提交到主分支都读不到。

## 分支矩阵

| 分支 | worktree 路径 | 加载器 | MC | JDK | Gradle | 构建插件 |
|---|---|---|---|---|---|---|
| `forge-1.20.1` | `D:\projects\no_experience\forge-1.20.1` | Forge 47.4.6 | 1.20.1 | 17 | 8.8 | ForgeGradle `[6.0,6.2)` + `reobfJar` |
| `neoforge-1.21.1` | `D:\projects\no_experience\neoforge-1.21.1` | NeoForge 21.1.248 | 1.21.1 | 21 | 8.8 | ModDevGradle 2.0.141 |
| `neoforge-1.26.1.2` | `D:\projects\no_experience\neoforge-1.26.1.2` | NeoForge 26.1.2.107 | 26.1.2 | 25 | 9.2.1 | ModDevGradle 2.0.146 |

- **主工作树**（持有 `.git` 目录）：`D:\projects\no_experience\forge-1.20.1`，分支 `forge-1.20.1`。另外两个是 linked worktree（`.git` 是文件）。
- **目录名 = 本地分支名 = 远程分支名**，三者必须一致。分支名沿用 Minecraft 完整版本串写法 `1.26.1.2`，jar 名与 tag 用真实版本串 `26.1.2`。
- **远端**：目前只有本地仓库，尚未配置 remote。接入 GitHub 后统一叫 `origin`，推送用 `git push -u origin <分支名>`；不要留下没有 upstream 的游离分支。
- **漂移起点**：三条分支的共同祖先是初始提交 `26da7f5`。之后每条分支各自有一次平台适配提交，因此第一次跨分支 cherry-pick 预计会冲突——那是已知的平台差异被暴露，不是 cherry-pick 的缺陷。

## 迁移纪律（强制）

1. 跨版本改动**只能** `git cherry-pick -x <sha>`，**禁止手工重写**到别的分支。
2. 改动先在**一个**版本上落地并**提交**（未提交的改动无法 cherry-pick）。
3. **用户的测试确认就是迁移触发点**：用户说"测试通过了 / 可以了 / 同步到其他版本 / 另外两个版本也加上"，立即对其余分支执行 cherry-pick，不必等他点名 `cherry-pick`。
4. 迁移前逐分支判定适用性，并把结论明确说出来：
   - **适用**：与加载器无关的逻辑（例如"再拦一条经验获取途径"）→ cherry-pick；
   - **不适用**：平台相关（事件类名、注册注解、元数据文件、HUD 层名）→ 跳过并说明原因；
   - **需适配**：API 改名或换位置 → 先 cherry-pick，再显式处理冲突。
5. 缺陷修复**双向流动**：在非主分支上定位并修复的 bug，先 cherry-pick 回 `forge-1.20.1`，再流向其他分支。

## 代码落点约定

- `src/main/java/com/noexperience/ExperienceLock.java`：游戏逻辑（拦截经验、tick 兜底锁 0）。三条分支**语义相同**，只差事件类与注册注解。
- `src/main/java/com/noexperience/client/ClientExperienceHud.java`：客户端隐藏经验条。三条分支**实现差异最大**，因为 HUD 层结构不同。
- `src/main/java/com/noexperience/NoExperience.java`：`@Mod` 入口，几乎逐字节相同。
- 平台差异全部集中在上述三个文件里，不要为了"保持一致"把平台调用硬塞进共享逻辑。

## 已知平台鸿沟（不要试图消除）

| 关注点 | `forge-1.20.1` | `neoforge-1.21.1` | `neoforge-1.26.1.2` |
|---|---|---|---|
| 注册注解 | `@Mod.EventBusSubscriber(modid=..., value=Dist.CLIENT)`（`net.minecraftforge.fml.common.Mod`） | `@EventBusSubscriber(modid=..., value=Dist.CLIENT)`（`net.neoforged.fml.common`） | 同 1.21.1 |
| 玩家 tick 事件 | `TickEvent.PlayerTickEvent` + `TickEvent.Phase.END` | `PlayerTickEvent.Post` | `PlayerTickEvent.Post` |
| 经验条 HUD | 单个 overlay：`VanillaGuiOverlay.EXPERIENCE_BAR`（进度条与等级数字在同一个 overlay 内） | 两个层：`VanillaGuiLayers.EXPERIENCE_BAR` + `EXPERIENCE_LEVEL` | 没有独立的经验条层：经验条属于 `CONTEXTUAL_INFO_BAR` / `CONTEXTUAL_INFO_BAR_BACKGROUND`，等级数字是 `EXPERIENCE_LEVEL`；必须复刻 `Gui#nextContextualInfoState` 的优先级判断，否则会把定位栏或跳跃条一起隐藏 |
| 客户端渲染事件 | `RenderGuiOverlayEvent.Pre`，比较 `NamedGuiOverlay.id()` | `RenderGuiLayerEvent.Pre`，比较 `ResourceLocation` | `RenderGuiLayerEvent.Pre`，比较 `Identifier`（`ResourceLocation` 在这一代改名为 `Identifier`） |
| 元数据文件 | `META-INF/mods.toml`，依赖用 `mandatory=true` | `META-INF/neoforge.mods.toml`，依赖用 `type="required"` | 同 1.21.1 |
| `pack.mcmeta` | `pack_format: 15` | `pack_format: 34` | `min_format` / `max_format`: 84（新的 pack 版本方案） |
| `ClientboundSetExperiencePacket` 构造参数顺序 | `(progress, level, total)` | `(progress, level, total)` | **`(progress, total, level)`**（参数顺序在这一代变了，虽然这里传的都是 0，但不要照抄） |
| 经验字段 | `Player.experienceLevel` / `experienceProgress` / `totalExperience` 都是 public 字段 | 同左 | 同左 |

## JDK

| 分支 | 编译目标 | 构建 daemon | 本机路径 |
|---|---|---|---|
| `forge-1.20.1` | Java 17 | JDK 21（Gradle 8.8 跑不了 JDK 25） | `C:\Program Files\Java\jdk-17`、`C:\Program Files\Java\jdk-21` |
| `neoforge-1.21.1` | Java 21 | JDK 21 | 同上 |
| `neoforge-1.26.1.2` | Java 25 | JDK 25 | `C:\Program Files\Java\jdk-25.0.4.1` |

- daemon 由各分支的 `gradle/gradle-daemon-jvm.properties` 指定，编译目标由 `java.toolchain` / `options.release` 指定，**两者都要对**。
- 症状对照：`Unsupported class file major version 69` = 拿 JDK 25 跑了旧 Gradle；`No matching toolchains found` = 本机缺对应 JDK。
- 不要在 `gradle.properties` 里写死 `org.gradle.java.home` 的绝对路径，换机器或换 CI 会直接失效。

## 行为契约（三条分支必须一致）

1. 生物死亡不掉经验球，包括末影龙与凋灵。
2. 经验球无法进入世界；旧存档中的经验球在区块加载时被清除。
3. 任何"给玩家加经验点/加等级"的调用都被拦截。
4. 每 tick 兜底：经验被归零，并在真的发生变化时同步给客户端。
5. 客户端不绘制经验条与等级数字；定位栏、坐骑跳跃条等其他 HUD 不受影响。
6. 负向经验变化（附魔、铁砧扣等级）不拦截——等级本来就是 0，拦它没有意义，还会引入状态不一致。

## Tag 格式

`v<版本>-<加载器>-<mc版本>`，前缀统一带 `v`：

```
v1.0.0-forge-1.20.1
v1.0.0-neoforge-1.21.1
v1.0.0-neoforge-26.1.2
```

## CI 契约

目前仓库没有 GitHub Actions workflow。若以后添加：分支名 `forge-1.20.1` / `neoforge-1.21.1` / `neoforge-1.26.1.2`、jar 名 `no_experience-<loader>-<mcversion>-<version>.jar`、tag 格式都会被 workflow 引用；改名必须同步改 workflow。

## 每个分支的发布产物检查（必做）

- `build/libs/*.jar` 根目录存在 `pack.mcmeta`，且格式与目标 MC 版本一致。
- jar 内含 `META-INF/mods.toml`（Forge）或 `META-INF/neoforge.mods.toml`（NeoForge），占位符已展开（不残留 `${...}`）。
- jar 内含 `com/noexperience/` 下的类；Forge 分支的类必须是 reobf 之后的 SRG 名字（`javap` 看到 `m_xxxxx_` 之类的名字才说明 reobf 生效）。
- 记录 jar 路径、大小与 SHA-256。
