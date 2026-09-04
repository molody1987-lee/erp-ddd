// 查询物料的成本历史
public class InventoryQueryService {
    public List<CostHistoryDTO> getMaterialCostHistory(MaterialId materialId, Period period) {
        // 查询该物料在该时间段内所有入库记录的加权平均成本变化
    }
}