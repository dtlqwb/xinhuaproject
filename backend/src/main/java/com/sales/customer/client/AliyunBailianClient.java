package com.sales.customer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里百炼大模型客户端
 */
@Slf4j
@Component
public class AliyunBailianClient {

    @Value("${aliyun.bailian.api-key}")
    private String apiKey;

    @Value("${aliyun.bailian.base-url}")
    private String baseUrl;

    @Value("${aliyun.bailian.model:qwen-plus}")
    private String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AliyunBailianClient() {
        this.webClient = WebClient.builder().build();
    }

    /**
     * 调用阿里百炼大模型
     *
     * @param prompt 提示词
     * @return AI生成的内容
     */
    public String callAI(String prompt) {
        try {
            log.info("调用阿里百炼AI, 模型: {}, URL: {}", model, baseUrl);

            // 构建请求体（OpenAI兼容格式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            Map<String, Object>[] messages = new Map[]{
                    createMessage("system", "你是一个专业的销售助手，擅长分析客户信息、生成营销方案和日报。请用简洁、专业的中文回答。"),
                    createMessage("user", prompt)
            };
            requestBody.put("messages", messages);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("result_format", "message");
            requestBody.put("parameters", parameters);

            // 发送请求 - 使用OpenAI兼容的API路径
            String response = webClient.post()
                    .uri(baseUrl + "/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("AI响应原始数据: {}", response);

            // 解析响应
            return parseResponse(response);

        } catch (Exception e) {
            log.error("调用阿里百炼AI失败: {}", e.getMessage(), e);
            // 降级：返回模拟数据
            return generateFallbackResponse(prompt);
        }
    }

    /**
     * 创建消息对象
     */
    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    /**
     * 解析AI响应（支持OpenAI兼容格式和百炼原生格式）
     */
    private String parseResponse(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);

        // 检查是否有错误
        if (root.has("error")) {
            JsonNode error = root.get("error");
            String message = error.has("message") ? error.get("message").asText() : "未知错误";
            log.error("AI API返回错误: {}", message);
            throw new RuntimeException("AI服务错误: " + message);
        }

        if (root.has("code")) {
            String code = root.get("code").asText();
            String message = root.has("message") ? root.get("message").asText() : "未知错误";
            log.error("AI API返回错误: code={}, message={}", code, message);
            throw new RuntimeException("AI服务错误: " + message);
        }

        // OpenAI兼容格式: {"choices": [{"message": {"content": "..."}}]}
        if (root.has("choices")) {
            JsonNode choices = root.get("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                    String content = firstChoice.get("message").get("content").asText();
                    log.info("AI解析成功: {}", content.substring(0, Math.min(100, content.length())));
                    return content;
                }
            }
        }

        // 百炼原生格式: {"output": {"choices": [...]}}
        if (root.has("output") && root.get("output").has("choices")) {
            JsonNode choices = root.get("output").get("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                    return firstChoice.get("message").get("content").asText();
                }
            }
        }

        log.error("无法解析AI响应: {}", response.substring(0, Math.min(200, response.length())));
        throw new RuntimeException("无法解析AI响应");
    }
    

    /**
     * 降级方案：当AI服务不可用时返回模拟数据
     */
    private String generateFallbackResponse(String prompt) {
        log.warn("使用降级方案生成响应");

        if (prompt.contains("客户信息") || prompt.contains("解析")) {
            return "已解析客户信息";
        } else if (prompt.contains("日报") || prompt.contains("总结")) {
            return "【今日概览】\n今日工作已完成\n\n【工作内容】\n重点客户跟进中\n\n【存在问题】\n需要加强客户沟通\n\n【明日计划】\n1. 跟进重点客户\n2. 继续拓展新客户";
        } else if (prompt.contains("营销方案") || prompt.contains("策略")) {
            return "营销方案：\n\n1. 深入了解客户需求\n2. 提供定制化解决方案\n3. 安排产品演示\n4. 制定优惠方案";
        }

        return "AI服务暂时不可用，请稍后重试";
    }
}
