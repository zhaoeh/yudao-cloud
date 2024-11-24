package cn.iocoder.yudao.framework.env.config;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.collection.SetUtils;
import cn.iocoder.yudao.framework.env.core.util.EnvUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Set;

import static cn.iocoder.yudao.framework.env.core.util.EnvUtils.HOST_NAME_VALUE;

/**
 * 多环境的 {@link EnvEnvironmentPostProcessor} 实现类
 * 将 yudao.env.tag 设置到 nacos 等组件对应的 tag 配置项，当且仅当它们不存在时
 *
 * @author 芋道源码
 */
public class EnvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * 初始化一堆标签，使用set集合收集，不允许重复
     */
    private static final Set<String> TARGET_TAG_KEYS = SetUtils.asSet(
            "spring.cloud.nacos.discovery.metadata.tag" // Nacos 注册中心
            // MQ TODO
    );

    /**
     * 这个方法在spring容器启动前进行回调执行，此时Environment对象刚被创建完毕，ApplicationContext对象还没有进行实例化
     *
     * @param environment
     * @param application
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 0. 设置 ${HOST_NAME} 兜底的环境变量
        String hostNameKey = StrUtil.subBetween(HOST_NAME_VALUE, "{", "}");
        if (!environment.containsProperty(hostNameKey)) {
            environment.getSystemProperties().put(hostNameKey, EnvUtils.getHostName());
        }

        // 1.1 如果没有 yudao.env.tag 配置项，则不进行配置项的修改
        String tag = EnvUtils.getTag(environment);
        if (StrUtil.isEmpty(tag)) {
            return;
        }
        // 1.2 需要修改的配置项
        for (String targetTagKey : TARGET_TAG_KEYS) {
            String targetTagValue = environment.getProperty(targetTagKey);
            if (StrUtil.isNotEmpty(targetTagValue)) {
                continue;
            }
            environment.getSystemProperties().put(targetTagKey, tag);
        }
    }

}
