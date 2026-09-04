# AGENTS.md - ERP 系统 AI 助手指南 (MySQL + MyBatis-Plus + RocketMQ 版)

## 1. 项目概述
- **项目名称**：企业资源计划系统 (ERP System)
- **核心业务**：为制造型企业提供采购、销售、库存、生产、财务一体化的经营管理平台。
- **核心目标**：实现业财一体化，保证最终一致性，支持多组织、多币种。
- **设计哲学**：领域驱动设计 (DDD) + 事件驱动架构 (EDA) + 最终一致性。

## 2. 限界上下文 (Bounded Context) 与映射
| 上下文 | 职责 | 核心聚合根 |
| :--- | :--- | :--- |
| **采购上下文** | 采购订单、采购入库、采购退货 | `PurchaseOrder` |
| **销售上下文** | 销售订单、销售出库、销售退货 | `SalesOrder` |
| **库存上下文** | 库存台账、库存移动、成本计算 | `InventoryTransaction` |
| **生产上下文** | 生产工单、BOM、工艺路线 | `WorkOrder` |
| **财务上下文** | 总账、应收、应付、固定资产 | `Account`, `Invoice` |
| **基础数据上下文** | 物料、供应商、客户、组织架构 | `Material`, `Supplier` |

**映射关系**：
- 领域事件是上下文间通信的**唯一方式**（异步解耦）。
- 基础数据上下文通过 **Open-Host Service (OHS)** 提供同步查询。

## 3. 技术栈
| 层级 | 组件 | 版本 | 用途 |
| :--- | :--- | :--- | :--- |
| **运行时** | JDK | 17 (LTS) | Java 运行环境 |
| **框架** | Spring Boot | 3.2.4 | 应用框架 |
| | Spring Cloud | 2023.0.1 | 微服务套件 |
| **数据访问** | MySQL | 8.0+ | 关系型数据库 |
| | MyBatis-Plus | 3.5.5 | ORM 框架（增强 MyBatis） |
| | MyBatis-Plus Generator | 3.5.5 | 代码生成器 |
| | Dynamic Datasource | 3.6.1 | 多数据源支持（读写分离） |
| | Flyway | 10.11.0 | 数据库版本迁移 |
| **缓存** | Redis | 7.2.4 | 缓存 + 分布式锁 |
| **消息队列** | RocketMQ | 5.2.0 (客户端 2.3.0) | 领域事件发布/订阅 |
| | Spring Messaging | (内置) | 消息抽象层 |
| **工具库** | Lombok | 1.18.32 | 代码简化 |
| | MapStruct | 1.5.5.Final | DTO ↔ 实体映射 |
| | Hutool | 5.8.27 | 国产工具库 |
| **可观测性** | Apache SkyWalking | 9.7.0 (Java Agent) | 全链路追踪 (Trace) |
| | Micrometer | 1.13.2 | 指标采集 (Metrics) |
| | Logback | 2.0.13 | 日志实现 (Logging) |
| **测试** | JUnit 5 | 5.10.2 | 单元测试 |
| | Mockito | 5.11.0 | Mock 框架 |
| | Testcontainers | 1.19.8 | 集成测试容器化 |

> **版本管理**：所有版本统一在 `pom.xml` 的 `<properties>` 中管理。

## 4. 包结构与分层架构 (DDD + MyBatis-Plus)
**关键约束**：AI 必须在以下包结构内生成代码，严禁跨层直接调用。
com.company.erp.{context}/ # 如 purchase, inventory, sales
├── domain/ # 领域层 (业务核心，不依赖 MyBatis)
│ ├── aggregate/ # 聚合根
│ │ └── PurchaseOrder.java
│ ├── entity/ # 实体 (非聚合根)
│ │ └── PurchaseOrderItem.java
│ ├── valueobject/ # 值对象 (不可变)
│ │ ├── PurchaseOrderStatus.java
│ │ ├── Quantity.java
│ │ ├── Money.java
│ │ └── MaterialId.java
│ ├── event/ # 领域事件
│ │ └── PurchaseOrderReceivedEvent.java
│ ├── service/ # 领域服务 (跨聚合业务)
│ │ └── PurchaseOrderService.java
│ └── repository/ # 仓储接口 (定义在 Domain)
│ └── PurchaseOrderRepository.java
├── application/ # 应用层 (用例编排)
│ ├── service/ # 应用服务 (事务边界)
│ │ ├── PurchaseOrderAppService.java
│ │ └── PurchaseOrderQueryAppService.java
│ ├── command/ # 命令对象 (CQRS)
│ │ ├── CreatePurchaseOrderCommand.java
│ │ └── ReceivePurchaseOrderCommand.java
│ ├── dto/ # 数据传输对象
│ │ └── PurchaseOrderDTO.java
│ └── subscriber/ # 事件订阅者
│ └── InventoryReceivedEventSubscriber.java
├── infrastructure/ # 基础设施层 (技术实现)
│ ├── repository/ # 仓储实现 (MyBatis-Plus Mapper)
│ │ ├── PurchaseOrderMapper.java # MyBatis-Plus Mapper 接口
│ │ └── PurchaseOrderRepositoryImpl.java # 实现 Domain 接口
│ ├── client/ # 客户端接口实现 (客户端接口)
│ │ ├── InventoryClient.java # 客户端接口
│ │ └── InventoryClientImpl.java # 实现客户端接口
│ ├── persistence/ # PO 对象 (MySQL 表映射)
│ │ └── PurchaseOrderPO.java
│ ├── cache/ # Redis 缓存实现
│ │ └── RedisCacheService.java
│ ├── message/ # RocketMQ 实现
│ │ ├── RocketMQEventPublisher.java
│ │ └── RocketMQEventSubscriber.java
│ ├── config/ # 配置类
│ │ ├── MyBatisPlusConfig.java
│ │ └── RocketMQConfig.java
│ └── trace/ # SkyWalking 埋点
│ └── TraceAspect.java # 自定义追踪切面
└── interfaces/ # 接口层 (表现层)
└── rest/ # REST API
│  ├── PurchaseOrderResource.java
│  ├── PurchaseOrderResourceImpl.java
└── PurchaseOrderController.java


## 5. 核心编码规范 (纪律红线)

### 5.1 分层依赖规则
- ✅ **允许**：Interface → Application → Domain ← Infrastructure
- ❌ **禁止**：Domain 层 import 任何 MyBatis-Plus、RocketMQ 的类。
- ❌ **禁止**：Application 层直接调用 `XXXMapper`（必须通过 Domain 层的 Repository 接口）。
- ❌ **禁止**：Controller 直接调用 Repository。

### 5.2 聚合根规则
- 聚合根是外部访问的**唯一入口**。
- 聚合根之间通过 ID 引用（如 `MaterialId`），不持有对方实体对象。
- ❌ **禁止**绕过聚合根直接修改内部实体/值对象。

### 5.3 数据访问规则 (MyBatis-Plus 特定)
- **PO (Persistence Object)**：放在 `infrastructure/persistence`，用于映射 MySQL 表。使用 `@TableName`、`@TableId`。
- **Mapper**：放在 `infrastructure/repository`，继承 `BaseMapper<PO>`。
- **Repository Impl**：实现 `domain/repository` 中的接口，内部注入 Mapper 进行数据操作。
- **分页查询**：使用 MyBatis-Plus 的 `Page` 对象，在 Application 层的 QueryService 中封装。
- **MySQL 特定**：所有表字段使用 `BIGINT` 作为主键（雪花ID），`DATETIME(3)` 记录精确到毫秒的时间。
- ❌ **禁止**：将 PO 对象暴露给 Domain 层或 Application 层。
- ❌ **禁止**：在 Domain 层使用 `LambdaQueryWrapper`。

**示例代码**：
```java
// 1. PO (基础设施层)
@Data
@TableName("purchase_order")
public class PurchaseOrderPO {
    @TableId(type = IdType.ASSIGN_ID) // 雪花算法
    private Long id;
    private String orderCode;
    private Integer status;
    private Long materialId;        // 存储 Material 的 ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

// 2. Mapper (基础设施层)
@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrderPO> {
    // 复杂查询使用 XML 或注解
}

// 3. Repository 接口 (领域层)
public interface PurchaseOrderRepository {
    PurchaseOrder findById(PurchaseOrderId id);
    void save(PurchaseOrder order);
}

// 4. Repository 实现 (基础设施层)
@Repository
@RequiredArgsConstructor
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {
    private final PurchaseOrderMapper mapper;
    private final PurchaseOrderConverter converter; // MapStruct

    @Override
    public PurchaseOrder findById(PurchaseOrderId id) {
        PurchaseOrderPO po = mapper.selectById(id.getValue());
        return converter.toDomain(po);
    }

    @Override
    public void save(PurchaseOrder order) {
        PurchaseOrderPO po = converter.toPO(order);
        if (po.getId() == null) {
            mapper.insert(po);
        } else {
            mapper.updateById(po);
        }
    }
}

### 5.4 缓存规则 (Redis)
-**缓存对象**：基础数据（物料、供应商）、组织架构、权限数据。
-**缓存过期**：基础数据缓存 1 小时，组织架构缓存 30 分钟。
-**缓存Key规范**：{context}:{entity}:{id}，如 foundation:material:123。
-**分布式锁**：使用 Redis 实现，锁名规范 lock:{resource}:{key}，如 lock:inventory:material_123（用于库存扣减）。

###5.5 消息规则 (RocketMQ 特定)
-**领域事件发布**：在应用服务中，事务提交后，通过 RocketMQEventPublisher 发布事件。
-**消息 Tag**：使用 eventType 作为 Tag（如 OrderPaidEvent），便于消费者精准过滤。
-**消息重试**：消费失败时，RocketMQ 默认重试 3 次，3 次后进入死信队列 (DLQ)。
-**幂等性**：所有事件消费者必须保证幂等性（通过 correlationId + Redis 去重）。
-**❌ 禁止**：在事务提交前发送消息（会导致脏读）。
// 应用层发布事件
@Service
@Transactional
@RequiredArgsConstructor
public class OrderApplicationService {
    private final RocketMQEventPublisher publisher;

    public void receiveOrder(ReceivePurchaseOrderCommand command) {
        // 1. 业务逻辑
        PurchaseOrder order = repository.findById(command.getOrderId());
        order.receive(command.getReceivedItems());
        repository.save(order);

        // 2. 事务提交后发布事件
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    PurchaseOrderReceivedEvent event = new PurchaseOrderReceivedEvent(
                        order.getId(), 
                        order.getReceivedItems()
                    );
                    publisher.publish(event);
                }
            }
        );
    }
}

###5.6 可观测性规则 (SkyWalking 特定)
-**所有 HTTP 入口、RocketMQ 消费、MyBatis SQL 执行会自动被 SkyWalking Agent 埋点。**
-**自定义埋点**：在关键业务方法（如成本计算）上使用 @Trace 注解，添加业务标签。
-**日志关联**：在 Logback 配置中添加 traceId 占位符，实现日志与 Trace 关联。

##6. 业务关键规则
-**移动加权平均成本**：每次入库重新计算（采购入库、生产完工入库）。
-**跨上下文最终一致性**：通过 RocketMQ 事件驱动，不引入分布式事务。
-**库存扣减**：销售出库/生产领料时，使用 Redis 分布式锁防止超卖。

##7. MySQL 表设计规范
-**规范**	说明
-**表名**	全小写，下划线分隔，如 purchase_order
-**主键**	id BIGINT，使用 MyBatis-Plus 雪花算法生成
-**时间字段**	create_time DATETIME(3)，update_time DATETIME(3)，自动填充
-**状态字段**	使用 TINYINT 或 SMALLINT，对应枚举的 code 值
-**金额字段**	使用 DECIMAL(20,4)，保证精度
-**字符集	utf8mb4，排序规则 utf8mb4_unicode_ci**
-**索引命名**	idx_{表名}_{字段名}，如 idx_purchase_order_status
##8. 测试策略
-**领域层单元测试**：覆盖率 ≥ 90%，不依赖 Spring 容器。
-**Repository 层测试**：使用 @MybatisPlusTest + Testcontainers 验证 SQL。
-**RocketMQ 集成测试**：使用 Testcontainers + RocketMQ 进行端到端测试。

##9. 执行权限
-**✅ 允许**：代码搜索、文件读取、格式化、编译检查、单元测试。
-**⚠️ 需确认**：添加新依赖、修改 Flyway 脚本、修改 RocketMQ Topic。
-**❌ 禁止**：git push、删除数据、执行生产迁移、修改 SkyWalking Agent 配置。




