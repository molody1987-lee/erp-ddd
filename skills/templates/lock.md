@Component
@RequiredArgsConstructor
public class RedisDistributedLock {
    private final StringRedisTemplate redisTemplate;

    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        String lockKey = "lock:" + key;
        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, UUID.randomUUID().toString(), timeout, unit);
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key) {
        redisTemplate.delete("lock:" + key);
    }
}

// 使用示例
@Service
public class InventoryApplicationService {
    public void deductInventory(MaterialId materialId, Quantity qty) {
        String lockKey = "inventory:material_" + materialId.getValue();
        if (!lock.tryLock(lockKey, 5, TimeUnit.SECONDS)) {
            throw new BusinessException("库存操作繁忙，请稍后重试");
        }
        try {
            // 业务逻辑
        } finally {
            lock.unlock(lockKey);
        }
    }
}