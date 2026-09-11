package cc.hongz.ztools.response.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.io.IOException;
import java.util.List;

/**
 * 统一响应的 Jackson null 序列化规则。
 * <p>
 * 保证响应里不出现 {@code null}：String → 空串、Boolean → false、枚举 → 第一个枚举值、其他对象 → 空对象。
 * 使用方（如 Spring Boot 应用）通过 {@link #nullToDefaultModifier()} 获取 modifier 注册到 ObjectMapper。
 * <p>
 * 这是统一响应（ztools-response）的通用能力，所有对接三方的模块（授权/支付/短信等）复用同一套规则。
 *
 * @author hongz
 */
public final class NullToDefaultSupport {

    /**
     * 工具类，禁止实例化。
     */
    private NullToDefaultSupport() {
    }

    /**
     * 获取 null 转默认值的 BeanSerializerModifier。
     *
     * @return BeanSerializerModifier
     */
    public static BeanSerializerModifier nullToDefaultModifier() {
        return new NullToDefaultModifier();
    }

    /**
     * null 转默认值：按字段类型把 null 转成非 null 的默认值。
     */
    static class NullToDefaultModifier extends BeanSerializerModifier {

        /** 空串序列化器。 */
        private static final JsonSerializer<Object> EMPTY_STRING = new JsonSerializer<Object>() {
            @Override
            public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider serializers)
                throws IOException {
                gen.writeString("");
            }
        };

        /** false 序列化器。 */
        private static final JsonSerializer<Object> FALSE = new JsonSerializer<Object>() {
            @Override
            public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider serializers)
                throws IOException {
                gen.writeBoolean(false);
            }
        };

        /** 空对象序列化器。 */
        private static final JsonSerializer<Object> EMPTY_OBJECT = new JsonSerializer<Object>() {
            @Override
            public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider serializers)
                throws IOException {
                gen.writeStartObject();
                gen.writeEndObject();
            }
        };

        @Override
        public List<BeanPropertyWriter> changeProperties(final SerializationConfig config,
            final BeanDescription beanDesc, final List<BeanPropertyWriter> beanProperties) {
            for (BeanPropertyWriter writer : beanProperties) {
                Class<?> raw = writer.getType().getRawClass();
                if (raw == String.class) {
                    writer.assignNullSerializer(EMPTY_STRING);
                } else if (raw == Boolean.class) {
                    writer.assignNullSerializer(FALSE);
                } else if (raw.isEnum()) {
                    writer.assignNullSerializer(new EnumDefaultSerializer(raw));
                } else if (!raw.isPrimitive()) {
                    writer.assignNullSerializer(EMPTY_OBJECT);
                }
            }
            return beanProperties;
        }
    }

    /**
     * 枚举默认值序列化器：输出第一个枚举值的 name。
     */
    static class EnumDefaultSerializer extends JsonSerializer<Object> {

        /** 枚举常量。 */
        private final Enum<?>[] constants;

        /**
         * 构造枚举默认值序列化器。
         *
         * @param enumClass 枚举类型
         */
        EnumDefaultSerializer(final Class<?> enumClass) {
            this.constants = (Enum<?>[]) enumClass.getEnumConstants();
        }

        @Override
        public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
            if (constants.length > 0) {
                gen.writeString(constants[0].name());
            } else {
                gen.writeString("");
            }
        }
    }
}
