
---

## 🧩 Skill 4：ERP Redis 缓存与锁规范

```yaml
---
name: erp-redis-guide
description: |
  ERP 系统的 Redis 缓存与分布式锁使用规范。
  适用场景：当需要为基础数据添加缓存、实现分布式锁（如库存扣减、唯一流水号生成）、
  或设计缓存失效策略时加载此 Skill。
---

# ERP Redis 缓存与分布式锁规范

## 1. 缓存使用场景

| 数据类型 | 缓存内容 | 过期时间 | Key 前缀 |
| :--- | :--- | :--- | :--- |
| 基础数据 | 物料、供应商、客户、组织架构 | 1 小时 | `foundation:material:`, `foundation:supplier:` |
| 权限数据 | 用户权限、角色权限 | 30 分钟 | `auth:user:`, `auth:role:` |
| 业务数据 | 最新库存余额 | 5 分钟 | `inventory:balance:` |

## 2. 缓存 Key 规范
{context}:{entity}:{id}
**示例**：
- `foundation:material:12345`
- `inventory:balance:material_12345`
- `auth:user:1001`

## 3. 缓存操作规范
参考templates/cache.md
##4. 分布式锁规范
###4.1 锁名规范
lock:{resource}:{key}
示例：
lock:inventory:material_12345（库存扣减锁）
lock:order:sequence（订单号生成锁）

###4.2 锁实现示例
参考templates/lock.md
###4.3 锁使用检查清单
□ 锁名是否遵循 lock:{resource}:{key} 规范？
□ 是否设置了合理的超时时间（通常 5~10 秒）？
□ 是否在 finally 块中释放锁？
□ 是否处理了锁获取失败的情况（抛出明确提示）？






