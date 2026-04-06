package com.zslin.wechat.message.template;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 消息模板配置
 * <p>
 * 定义每个模板的字段结构，用于验证和文档生成
 * </p>
 *
 * @author 子墨
 * @since 1.0.0
 */
@Data
public class MessageTemplateConfig {

    /**
     * 模板 ID
     */
    private String templateId;

    /**
     * 模板名称（用于业务标识）
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 字段列表（按顺序）
     */
    private List<TemplateField> fields;

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 模板字段
     */
    @Data
    public static class TemplateField {
        /**
         * 字段名（对应微信模板中的字段名）
         */
        private String name;

        /**
         * 字段类型（thing, time, price, address 等）
         */
        private String type;

        /**
         * 字段描述
         */
        private String description;

        /**
         * 是否必填
         */
        private boolean required = true;

        /**
         * 示例值
         */
        private String example;
    }

    /**
     * 获取字段数量
     */
    public int getFieldCount() {
        return fields != null ? fields.size() : 0;
    }

    /**
     * 检查字段是否存在
     */
    public boolean hasField(String fieldName) {
        if (fields == null) {
            return false;
        }
        return fields.stream().anyMatch(f -> f.getName().equals(fieldName));
    }

    /**
     * 获取必填字段列表
     */
    public List<TemplateField> getRequiredFields() {
        if (fields == null) {
            return java.util.Collections.emptyList();
        }
        return fields.stream()
                .filter(TemplateField::isRequired)
                .collect(java.util.stream.Collectors.toList());
    }
}
