```java
@Service
@RequiredArgsConstructor
public class MaterialService {
    private final StringRedisTemplate redisTemplate;
    private final MaterialRepository repository;

    @Cacheable(value = "foundation:material", key = "#id")
    public Material getMaterial(MaterialId id) {
        return repository.findById(id);
    }

    @CacheEvict(value = "foundation:material", key = "#material.id")
    public void updateMaterial(Material material) {
        repository.save(material);
    }
}