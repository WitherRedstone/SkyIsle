# 天空岛（Sky Isle）

[English](#english) | [中文](#中文)

---

# **English**

## Introduction
Sky Isle transforms all three dimensions into sky island worlds. Each dimension (Overworld, Nether, The End) becomes a void world with a customizable starting platform generated from NBT structure templates.

## Features
- **Three-dimensional sky islands**: Overworld, Nether, and The End are all void worlds with floating islands
- **Customizable structure templates**: Use your own NBT structure files as starting platforms
- **Configurable spawn dimension**: Choose to spawn in Overworld, Nether, or The End (outer islands)
- **Structure generation control**: Toggle vanilla structure generation on/off independently
- **Nether fortress control**: Option to generate only Blaze rooms in Nether fortresses
- **End spike control**: Toggle obsidian spikes on the End main island
- **Default world type**: Automatically select Sky Isle when creating a new world
- **Smart spawn positioning**: Players spawn on the highest block of the platform center, never stuck inside blocks

## Structure Templates
Place custom structure files (`.nbt`) in the following directories. The mod automatically creates these directories and copies default templates on first launch.

- `config/sky_isle/structures/overworld/` — Overworld platform structures
- `config/sky_isle/structures/the_nether/` — Nether platform structures
- `config/sky_isle/structures/the_end/` — End platform structures

### Built-in Overworld Templates
- **overworld_oak**: 5×3×5 grass platform with an oak tree; chest under tree contains [1×sugar cane / bamboo / cactus / ice / lava]
- **overworld_acacia**: 5×3×5 grass platform with an acacia tree; chest under tree contains [1×sugar cane / bamboo / cactus / ice / lava]
- **overworld_bamboo**: 5×3×5 grass platform with bamboo; no chest

### Built-in Nether Templates
- **the_nether_default**: Platform with a Nether portal, crimson nylium and warped nylium on both sides
- **the_nether_respawn**: Respawn platform used when spawn dimension is set to the_nether

### Built-in End Templates
- **the_end_respawn**: Respawn platform used when spawn dimension is set to the_end (placed on outer islands)

## Configuration

### Common Config
- **enableStructureGeneration** (default: `true`) — Enable vanilla structure generation (villages, fortresses, end cities, etc.)
- **defaultWorldType** (default: `true`) — Set Sky Isle as the default world type when creating a new world

### Spawn Dimension
- **respawnDimension** (default: `overworld`) — Player spawn dimension: `overworld`, `the_nether`, or `the_end`
- **netherRespawnStructureName** (default: `the_nether_respawn`) — Structure file name (without .nbt) for nether respawn platform
- **endRespawnStructureName** (default: `the_end_respawn`) — Structure file name (without .nbt) for end respawn platform

### Overworld Structure
- **overworldStructureName** (default: `overworld_oak`) — Structure file name (without .nbt)
- **x** (default: `0`) — Platform X coordinate
- **y** (default: `64`) — Platform Y coordinate
- **z** (default: `0`) — Platform Z coordinate

### Nether Structure
- **netherStructureName** (default: `the_nether_default`) — Structure file name (without .nbt)
- **x** (default: `0`) — Platform X coordinate
- **y** (default: `64`) — Platform Y coordinate
- **z** (default: `0`) — Platform Z coordinate
- **blazeRoomOnly** (default: `true`) — Generate only Blaze rooms in Nether fortresses

### End Structure
- **x** (default: `2500`) — Platform X coordinate (1000+ recommended for outer islands)
- **y** (default: `64`) — Platform Y coordinate
- **z** (default: `2500`) — Platform Z coordinate
- **enableEndSpikes** (default: `true`) — Generate obsidian spikes on the End main island

---

# **中文**

## 简介
Sky Isle 将所有三个维度都改为空岛世界。每个维度（主世界、下界、末地）都变为虚空世界，并从 NBT 结构模板生成可自定义的起始平台。

## 功能
- **三维度空岛**：主世界、下界、末地均为虚空浮岛世界
- **自定义结构模板**：使用自己的 NBT 结构文件作为起始平台
- **可配置出生维度**：选择在主世界、下界或末地（外岛）出生
- **结构生成控制**：独立开关原版结构生成
- **下界要塞控制**：可设置下界要塞仅生成烈焰人刷怪房
- **末地黑曜石柱控制**：可开关末地主岛黑曜石柱生成
- **默认世界类型**：创建新世界时自动选择 Sky Isle
- **智能出生定位**：玩家出生在平台中心最高方块上方，不会卡在方块内

## 结构模板
将自定义结构文件（`.nbt`）放入以下目录。模组首次启动时会自动创建这些目录并复制默认模板。

- `config/sky_isle/structures/overworld/` — 主世界平台结构
- `config/sky_isle/structures/the_nether/` — 下界平台结构
- `config/sky_isle/structures/the_end/` — 末地平台结构

### 主世界内置模板
* **overworld_oak**：5×3×5 草方块平台，自带橡树，树下方箱子里有 [1×甘蔗 / 竹子 / 仙人掌 / 冰 / 岩浆]
* **overworld_acacia**：5×3×5 草方块平台，自带金合欢树，树下方箱子里有 [1×甘蔗 / 竹子 / 仙人掌 / 冰 / 岩浆]
* **overworld_bamboo**：5×3×5 草方块平台，自带一个竹子，无箱子

### 下界内置模板
* **the_nether_default**：自带一个地狱门，门两边是两个绯红菌岩和诡异菌岩
* **the_nether_respawn**：出生维度设为下界时使用的重生平台

### 末地内置模板
* **the_end_respawn**：出生维度设为末地时使用的重生平台（放置在外岛）

## 配置

### 通用配置
- **enableStructureGeneration**（默认：`true`）— 是否启用原版结构生成（村庄、要塞、末地城等）
- **defaultWorldType**（默认：`true`）— 是否将 Sky Isle 设为默认世界类型

### 出生维度
- **respawnDimension**（默认：`overworld`）— 玩家出生维度：`overworld`、`the_nether` 或 `the_end`
- **netherRespawnStructureName**（默认：`the_nether_respawn`）— 下界重生结构文件名（不含 .nbt 后缀）
- **endRespawnStructureName**（默认：`the_end_respawn`）— 末地重生结构文件名（不含 .nbt 后缀）

### 主世界结构
- **overworldStructureName**（默认：`overworld_oak`）— 结构文件名（不含 .nbt 后缀）
- **x**（默认：`0`）— 平台 X 坐标
- **y**（默认：`64`）— 平台 Y 坐标
- **z**（默认：`0`）— 平台 Z 坐标

### 下界结构
- **netherStructureName**（默认：`the_nether_default`）— 结构文件名（不含 .nbt 后缀）
- **x**（默认：`0`）— 平台 X 坐标
- **y**（默认：`64`）— 平台 Y 坐标
- **z**（默认：`0`）— 平台 Z 坐标
- **blazeRoomOnly**（默认：`true`）— 是否让下界要塞只生成烈焰人刷怪房

### 末地结构
- **x**（默认：`2500`）— 平台 X 坐标（外岛建议 1000 以上）
- **y**（默认：`64`）— 平台 Y 坐标
- **z**（默认：`2500`）— 平台 Z 坐标
- **enableEndSpikes**（默认：`true`）— 主岛是否生成末地黑曜石柱