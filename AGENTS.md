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
- **远端**：`origin` = <https://github.com/QLNPLUS/no-experience>（公开仓库），统一叫 `origin`，推送用 `git push -u origin <分支名>`；不要留下没有 upstream 的游离分支。**GitHub 默认分支是 `forge-1.20.1`**，不是 `main`——`release` 事件只会运行默认分支上的 workflow，默认分支换了就必须回头核对 `.github/workflows/publish-curseforge.yml`。
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
- launcher JVM **同样要对**：实测 PATH 上的 Oracle `javapath` 会把 launcher 抬到 JDK 25，Gradle 8.8 连不起来。两条 Gradle 8.8 分支跑 `gradlew` 前必须让 `JAVA_HOME` 指向 JDK 21，`neoforge-1.26.1.2` 指向 JDK 25。CI 里由 `actions/setup-java` 保证这一点。
- 症状对照：`Unsupported class file major version 69` = 拿 JDK 25 跑了旧 Gradle；`No matching toolchains found` = 本机缺对应 JDK。
- 不要在 `gradle.properties` 里写死 `org.gradle.java.home` 的绝对路径，换机器或换 CI 会直接失效。

## 构建网络（本机环境）

本机的 Java 不读取 Windows 系统代理（WinINET 里配的是 `127.0.0.1:7897`），直连 `libraries.minecraft.net`、`resources.download.minecraft.net`、`maven.minecraftforge.net` 会超时。**需要联网的构建步骤**（首次 setup、`runServer` 首次下载资源与语言文件）必须显式把代理传给 JVM：

```powershell
$env:JAVA_TOOL_OPTIONS='-Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7897 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7897 -Dhttp.nonProxyHosts=localhost|127.0.0.1'
```

缓存暖起来之后 `gradlew build` 不联网也能跑通。**不要把代理写进 `gradle.properties` 提交**——那是本机环境，换机器或 CI 就失效。

## 行为契约（三条分支必须一致）

1. 生物死亡不掉经验球，包括末影龙与凋灵。
2. 经验球无法进入世界；旧存档中的经验球在区块加载时被清除。
3. 任何"给玩家加经验点/加等级"的调用都被拦截。
4. 每 tick 兜底：经验被归零，并在真的发生变化时同步给客户端。
5. 客户端不绘制经验条与等级数字；定位栏、坐骑跳跃条等其他 HUD 不受影响。
6. 负向经验变化（附魔、铁砧扣等级）不拦截——等级本来就是 0，拦它没有意义，还会引入状态不一致。

## Tag 格式与发布流程

`v<版本>-<加载器>-<mc版本>`，前缀统一带 `v`：

```
v1.0.0-forge-1.20.1
v1.0.0-neoforge-1.21.1
v1.0.0-neoforge-26.1.2
```

tag 打在**各自的版本分支**上（`v1.0.0-neoforge-1.21.1` 打在 `neoforge-1.21.1` 上）。三条分支的版本号各自独立，不要求锁步。

发布一次：

1. 在目标分支把 `gradle.properties` 的 `mod_version` 改成本次版本号，并在 `CHANGELOG.md` 顶部补一个同名的 `## <版本>` 小节。
2. 提交、推送分支。
3. 打 tag 并推送：`git tag v1.0.0-forge-1.20.1; git push origin v1.0.0-forge-1.20.1`。
4. 在 GitHub 上针对该 tag 创建 Release 并发布（Published，不是 Draft）——这一步才触发发布 workflow。

**tag 里的版本必须等于 `gradle.properties` 的 `mod_version`。** workflow 会断言 `build/libs/no_experience-<loader>-<mcversion>-<版本>.jar` 存在，对不上直接失败，不会传错文件。

## CI 契约

`.github/workflows/publish-curseforge.yml`，**三条分支内容逐字节相同**。默认分支上的那一份负责响应 `release` 事件（GitHub 只在默认分支上运行 release workflow），workflow 自己从 tag 判断该构建哪条分支：

| tag | 构建的分支 | CurseForge loader | 编译目标 | 构建 JVM |
|---|---|---|---|---|
| `v<版本>-forge-1.20.1` | `forge-1.20.1` | `forge` | 17 | 21 |
| `v<版本>-neoforge-1.21.1` | `neoforge-1.21.1` | `neoforge` | 21 | 21 |
| `v<版本>-neoforge-26.1.2` | `neoforge-1.26.1.2` | `neoforge` | 25 | 25 |

- **手动触发**：`workflow_dispatch` 需要 `tag` 输入；`loader` 选 `auto`（按 tag 判断）、`forge`、`neoforge`、`neoforge-126`（单个任务重试，不会重复上传已经成功的那一个）。`loader=auto` 且 tag 是不带加载器后缀的裸版本号（如 `v1.0.0`）时三个任务全跑，各自构建自己分支的头部提交。
- **仓库配置**：变量 `CURSEFORGE_PROJECT_ID`（数字项目 ID）+ 机密 `CURSEFORGE_TOKEN`（CurseForge API token）。缺任意一个，workflow 在构建前就失败并给出提示。**token 不写进任何文件、日志或聊天。**
- 上传文件名固定为 `no_experience-<loader>-<mcversion>-<版本>.jar`，CurseForge 版本名是 `No Experience <版本> (<加载器> <MC>)`。改名必须同步改 workflow。
- 上传前会断言：jar 存在、归档根有 `pack.mcmeta`、元数据文件没残留 `${...}`；`CHANGELOG.md` 里没有对应版本小节同样直接失败。
- **两个只在 Linux 上暴露、本机永远不会发现的坑，已经踩过一次**（首次 CI 构建三个任务全挂）：
  1. `gradlew` 必须带可执行位，提交模式要是 `100755`；`100644` 时 CI 报 `Permission denied` 并以 **exit 126** 结束。
  2. `gradlew` 必须是 LF 换行；CRLF 的 shebang 在 Linux 上以 **exit 127**（`cannot execute: required file not found`）结束。
  仓库根的 `.gitattributes` 已把 `gradlew` 与 `*.sh` 钉成 LF，但**可执行位不在它的管辖范围内**——动了 wrapper 之后用 `git ls-tree <分支> gradlew` 复查模式，两个值都要对。
- **验证发布结果不要靠"公开文件列表里有没有新文件"**：CurseForge 审核期间文件对公开列表不可见，上传成功也可能几分钟内查不到，此时重试会造出重复文件。用文件总数对比（发布前数量 + 本次上传数量），并把 GitHub Release 的 assets 当作另一半证据。

## 每个分支的发布产物检查（必做）

- `build/libs/*.jar` 根目录存在 `pack.mcmeta`，且格式与目标 MC 版本一致。
- jar 内含 `META-INF/mods.toml`（Forge）或 `META-INF/neoforge.mods.toml`（NeoForge），占位符已展开（不残留 `${...}`）。
- jar 内含 `com/noexperience/` 下的类；Forge 分支的类必须是 reobf 之后的 SRG 名字（`javap` 看到 `m_xxxxx_` 之类的名字才说明 reobf 生效）。
- 记录 jar 路径、大小与 SHA-256。
