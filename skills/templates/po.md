@Data
@TableName("purchase_order")
public class PurchaseOrderPO {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String orderCode;
    private Long supplierId;
    private Integer status;
    private BigDecimal totalAmount;
    private String currency;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long createdBy;
    private Long updatedBy;
    private Boolean isDeleted;
}