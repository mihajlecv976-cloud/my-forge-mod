# 1.20.1 content gate

These identifiers occur in the supplied Shine resource payload but are not present in vanilla Minecraft 1.20.1. They must never be registered as fake vanilla blocks/items/biomes just to satisfy the port. Any code that touches them must use safe lookup/feature gating.

- `minecraft:copper_lantern` — **disable on 1.20.1**
- `minecraft:firefly` — **disable on 1.20.1**
- `minecraft:firefly_bush` — **disable on 1.20.1**
- `minecraft:pale_garden` — **disable on 1.20.1**
- `minecraft:pale_oak_leaves` — **disable on 1.20.1**
- `minecraft:pale_oak_log` — **disable on 1.20.1**
- `minecraft:resin_clump` — **disable on 1.20.1**

Cherry Grove is retained because it is present in 1.20.1.
