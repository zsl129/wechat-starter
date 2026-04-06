package com.zslin.wechat.message.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息模板管理器
 * <p>
 * 管理所有消息模板的配置，支持动态添加和查询
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Component
public class MessageTemplateManager {

    private static final Logger log = LoggerFactory.getLogger(MessageTemplateManager.class);

    /**
     * 模板缓存（模板 ID -> 配置）
     */
    private final Map<String, MessageTemplateConfig> templateCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化默认模板配置
        initDefaultTemplates();
        log.info("消息模板管理器初始化完成，已加载 {} 个模板", templateCache.size());
    }

    /**
     * 初始化默认模板
     * <p>
     * 这里添加一些常见的订阅消息模板示例
     * 实际使用时可以根据需要添加更多
     * </p>
     */
    private void initDefaultTemplates() {
        // 模板 1: 订单通知
        addTemplate(createOrderNotificationTemplate());

        // 模板 2: 支付成功通知
        addTemplate(createPaymentSuccessTemplate());

        // 模板 3: 物流通知
        addTemplate(createLogisticsNotificationTemplate());

        // 模板 4: 预约提醒
        addTemplate(createAppointmentReminderTemplate());
    }

    /**
     * 添加模板配置
     *
     * @param config 模板配置
     * @return 是否添加成功
     */
    public boolean addTemplate(MessageTemplateConfig config) {
        if (config == null || config.getTemplateId() == null) {
            log.warn("无效的模板配置：{}", config);
            return false;
        }

        templateCache.put(config.getTemplateId(), config);
        log.info("添加消息模板：{} ({})", config.getName(), config.getTemplateId());
        return true;
    }

    /**
     * 获取模板配置
     *
     * @param templateId 模板 ID
     * @return 模板配置，如果不存在返回 null
     */
    public MessageTemplateConfig getTemplate(String templateId) {
        return templateCache.get(templateId);
    }

    /**
     * 检查模板是否存在
     *
     * @param templateId 模板 ID
     * @return true-存在，false-不存在
     */
    public boolean hasTemplate(String templateId) {
        return templateCache.containsKey(templateId);
    }

    /**
     * 获取所有启用的模板 ID 列表
     *
     * @return 模板 ID 列表
     */
    public List<String> getActiveTemplateIds() {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, MessageTemplateConfig> entry : templateCache.entrySet()) {
            if (entry.getValue().isEnabled()) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * 获取所有模板配置
     *
     * @return 模板配置列表
     */
    public List<MessageTemplateConfig> getAllTemplates() {
        return new ArrayList<>(templateCache.values());
    }

    /**
     * 删除模板
     *
     * @param templateId 模板 ID
     * @return 是否删除成功
     */
    public boolean removeTemplate(String templateId) {
        if (templateCache.remove(templateId) != null) {
            log.info("删除消息模板：{}", templateId);
            return true;
        }
        return false;
    }

    /**
     * 禁用模板
     *
     * @param templateId 模板 ID
     */
    public void disableTemplate(String templateId) {
        MessageTemplateConfig config = templateCache.get(templateId);
        if (config != null) {
            config.setEnabled(false);
            log.info("禁用消息模板：{}", templateId);
        }
    }

    /**
     * 启用模板
     *
     * @param templateId 模板 ID
     */
    public void enableTemplate(String templateId) {
        MessageTemplateConfig config = templateCache.get(templateId);
        if (config != null) {
            config.setEnabled(true);
            log.info("启用消息模板：{}", templateId);
        }
    }

    /**
     * 验证消息数据是否符合模板要求
     *
     * @param templateId 模板 ID
     * @param data       消息数据
     * @return 验证结果（true-通过，false-不通过）
     */
    public boolean validateData(String templateId, Map<String, Object> data) {
        MessageTemplateConfig config = getTemplate(templateId);
        if (config == null) {
            log.warn("模板不存在：{}", templateId);
            return false;
        }

        if (config.getFields() == null || config.getFields().isEmpty()) {
            // 没有定义字段，跳过验证
            return true;
        }

        // 检查必填字段
        for (MessageTemplateConfig.TemplateField field : config.getRequiredFields()) {
            if (!data.containsKey(field.getName())) {
                log.error("缺少必填字段：templateId={}, field={}", templateId, field.getName());
                return false;
            }
        }

        return true;
    }

    // ==================== 预设模板 ====================

    /**
     * 创建订单通知模板
     */
    private MessageTemplateConfig createOrderNotificationTemplate() {
        MessageTemplateConfig config = new MessageTemplateConfig();
        config.setTemplateId("ORDER_NOTIFY");
        config.setName("订单通知");
        config.setDescription("订单状态变更通知");
        config.setEnabled(true);

        List<MessageTemplateConfig.TemplateField> fields = new ArrayList<>();

        fields.add(createField("thing1", "订单编号", "ORDER202312010001", true));
        fields.add(createField("thing2", "订单状态", "已发货", true));
        fields.add(createField("time1", "下单时间", "2023-12-01 10:30", true));
        fields.add(createField("price1", "订单金额", "¥199.00", true));
        fields.add(createField("thing3", "备注", "请及时查收", false));

        config.setFields(fields);
        return config;
    }

    /**
     * 创建支付成功模板
     */
    private MessageTemplateConfig createPaymentSuccessTemplate() {
        MessageTemplateConfig config = new MessageTemplateConfig();
        config.setTemplateId("PAYMENT_SUCCESS");
        config.setName("支付成功通知");
        config.setDescription("用户支付成功后发送");
        config.setEnabled(true);

        List<MessageTemplateConfig.TemplateField> fields = new ArrayList<>();

        fields.add(createField("thing1", "商品名称", "测试商品", true));
        fields.add(createField("price1", "支付金额", "¥99.00", true));
        fields.add(createField("time1", "支付时间", "2023-12-01 11:00", true));
        fields.add(createField("thing2", "支付方式", "微信支付", true));

        config.setFields(fields);
        return config;
    }

    /**
     * 创建物流通知模板
     */
    private MessageTemplateConfig createLogisticsNotificationTemplate() {
        MessageTemplateConfig config = new MessageTemplateConfig();
        config.setTemplateId("LOGISTICS_NOTIFY");
        config.setName("物流通知");
        config.setDescription("物流状态更新通知");
        config.setEnabled(true);

        List<MessageTemplateConfig.TemplateField> fields = new ArrayList<>();

        fields.add(createField("thing1", "订单编号", "ORDER001", true));
        fields.add(createField("thing2", "物流公司", "顺丰速运", true));
        fields.add(createField("thing3", "运单号", "SF123456789", true));
        fields.add(createField("thing4", "物流状态", "运输中", true));
        fields.add(createField("time1", "更新时间", "2023-12-01 12:00", true));
        fields.add(createField("address1", "当前位置", "北京市朝阳区", true));

        config.setFields(fields);
        return config;
    }

    /**
     * 创建预约提醒模板
     */
    private MessageTemplateConfig createAppointmentReminderTemplate() {
        MessageTemplateConfig config = new MessageTemplateConfig();
        config.setTemplateId("APPOINTMENT_REMINDER");
        config.setName("预约提醒");
        config.setDescription("预约时间临近提醒");
        config.setEnabled(true);

        List<MessageTemplateConfig.TemplateField> fields = new ArrayList<>();

        fields.add(createField("thing1", "预约项目", "美容护肤", true));
        fields.add(createField("time1", "预约时间", "2023-12-02 14:00", true));
        fields.add(createField("thing2", "预约门店", "北京旗舰店", true));
        fields.add(createField("thing3", "联系电话", "010-12345678", true));
        fields.add(createField("thing4", "注意事项", "请提前 10 分钟到达", false));

        config.setFields(fields);
        return config;
    }

    /**
     * 创建字段
     */
    private MessageTemplateConfig.TemplateField createField(
            String name, String description, String example, boolean required) {
        MessageTemplateConfig.TemplateField field = new MessageTemplateConfig.TemplateField();
        field.setName(name);
        field.setDescription(description);
        field.setExample(example);
        field.setRequired(required);
        field.setType("thing"); // 默认为 thing 类型
        return field;
    }
}
