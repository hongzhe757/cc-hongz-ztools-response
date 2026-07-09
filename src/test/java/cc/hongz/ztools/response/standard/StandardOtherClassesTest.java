package cc.hongz.ztools.response.standard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * standard 包剩余响应类的单元测试.
 * <p>
 * 测试 GraphQLResponse、GrpcResponse、JsonApiResponse、ProblemDetail。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("Standard 包剩余响应类测试")
class StandardOtherClassesTest {

    // ==================== ProblemDetail 测试 ====================

    @Test
    @DisplayName("ProblemDetail.of(status, title, detail) - 创建简单错误详情")
    void testProblemDetail_Of() {
        ProblemDetail problem = ProblemDetail.of(400, "Bad Request", "参数无效");

        assertEquals(400, problem.getStatus());
        assertEquals("Bad Request", problem.getTitle());
        assertEquals("参数无效", problem.getDetail());
        assertNull(problem.getType());
        assertNull(problem.getInstance());
    }

    @Test
    @DisplayName("ProblemDetail.of(type, status, title, detail) - 创建带类型的错误详情")
    void testProblemDetail_OfType() {
        String type = "https://example.com/errors/validation";
        ProblemDetail problem = ProblemDetail.of(type, 422, "Unprocessable Entity", "验证失败");

        assertEquals(type, problem.getType());
        assertEquals(422, problem.getStatus());
        assertEquals("Unprocessable Entity", problem.getTitle());
        assertEquals("验证失败", problem.getDetail());
    }

    @Test
    @DisplayName("ProblemDetail.badRequest(detail) - 创建 400 错误")
    void testProblemDetail_BadRequest() {
        ProblemDetail problem = ProblemDetail.badRequest("请求参数格式错误");

        assertEquals(400, problem.getStatus());
        assertEquals("Bad Request", problem.getTitle());
        assertEquals("请求参数格式错误", problem.getDetail());
    }

    @Test
    @DisplayName("ProblemDetail.notFound(detail) - 创建 404 错误")
    void testProblemDetail_NotFound() {
        ProblemDetail problem = ProblemDetail.notFound("用户不存在");

        assertEquals(404, problem.getStatus());
        assertEquals("Not Found", problem.getTitle());
        assertEquals("用户不存在", problem.getDetail());
    }

    @Test
    @DisplayName("ProblemDetail.internalError(detail) - 创建 500 错误")
    void testProblemDetail_InternalError() {
        ProblemDetail problem = ProblemDetail.internalError("数据库连接失败");

        assertEquals(500, problem.getStatus());
        assertEquals("Internal Server Error", problem.getTitle());
        assertEquals("数据库连接失败", problem.getDetail());
    }

    @Test
    @DisplayName("ProblemDetail Builder 模式")
    void testProblemDetail_Builder() {
        Map<String, Object> extensions = Map.of("traceId", "abc-123");
        List<ProblemDetail.ValidationError> errors = Arrays.asList(
                new ProblemDetail.ValidationError("email", "邮箱格式不正确", "invalid-email"),
                new ProblemDetail.ValidationError("age", "年龄必须大于0", -1)
        );

        ProblemDetail problem = ProblemDetail.builder()
                .type("https://example.com/errors/validation")
                .title("Validation Failed")
                .status(422)
                .detail("请求验证失败")
                .instance("/api/users")
                .extensions(extensions)
                .errors(errors)
                .build();

        assertEquals("https://example.com/errors/validation", problem.getType());
        assertEquals("Validation Failed", problem.getTitle());
        assertEquals(422, problem.getStatus());
        assertEquals("请求验证失败", problem.getDetail());
        assertEquals("/api/users", problem.getInstance());
        assertNotNull(problem.getExtensions());
        assertEquals(2, problem.getErrors().size());
    }

    @Test
    @DisplayName("ProblemDetail.ValidationError - 验证错误项")
    void testProblemDetail_ValidationError() {
        ProblemDetail.ValidationError error = new ProblemDetail.ValidationError("username", "用户名已存在", "testuser");

        assertEquals("username", error.getField());
        assertEquals("用户名已存在", error.getMessage());
        assertEquals("testuser", error.getRejectedValue());
    }

    @Test
    @DisplayName("ProblemDetail Getter/Setter")
    void testProblemDetail_GettersSetters() {
        ProblemDetail problem = new ProblemDetail();
        problem.setType("https://example.com/error");
        problem.setTitle("Error");
        problem.setStatus(500);
        problem.setDetail("Something went wrong");
        problem.setInstance("/api/test");
        problem.setExtensions(Map.of("key", "value"));
        problem.setErrors(List.of());

        assertEquals("https://example.com/error", problem.getType());
        assertEquals("Error", problem.getTitle());
        assertEquals(500, problem.getStatus());
        assertEquals("Something went wrong", problem.getDetail());
        assertEquals("/api/test", problem.getInstance());
        assertNotNull(problem.getExtensions());
        assertNotNull(problem.getErrors());
    }

    // ==================== GraphQLResponse 测试 ====================

    @Test
    @DisplayName("GraphQLResponse.success(data) - 创建成功响应")
    void testGraphQLResponse_Success() {
        Map<String, Object> data = Map.of("user", Map.of("id", 1, "name", "张三"));
        GraphQLResponse<Map<String, Object>> response = GraphQLResponse.success(data);

        assertNotNull(response);
        assertEquals(data, response.getData());
        assertNull(response.getErrors());
        assertNull(response.getExtensions());
    }

    @Test
    @DisplayName("GraphQLResponse.success(data, extensions) - 带扩展的成功响应")
    void testGraphQLResponse_SuccessWithExtensions() {
        String data = "查询结果";
        Map<String, Object> extensions = Map.of("tracing", Map.of("duration", 100));

        GraphQLResponse<String> response = GraphQLResponse.success(data, extensions);

        assertEquals(data, response.getData());
        assertNotNull(response.getExtensions());
        assertEquals(100, ((Map<?, ?>) response.getExtensions().get("tracing")).get("duration"));
    }

    @Test
    @DisplayName("GraphQLResponse.error(errors) - 创建错误响应")
    void testGraphQLResponse_Error() {
        GraphQLResponse.GraphQLError error = new GraphQLResponse.GraphQLError(
                "Field 'user' not found",
                List.of(new GraphQLResponse.Location(1, 5)),
                List.of("query", "user"),
                null
        );

        GraphQLResponse<Object> response = GraphQLResponse.error(List.of(error));

        assertNull(response.getData());
        assertNotNull(response.getErrors());
        assertEquals(1, response.getErrors().size());
        assertEquals("Field 'user' not found", response.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("GraphQLResponse.error(message) - 创建单个错误响应")
    void testGraphQLResponse_ErrorSingleMessage() {
        GraphQLResponse<Object> response = GraphQLResponse.error("语法错误");

        assertNull(response.getData());
        assertNotNull(response.getErrors());
        assertEquals(1, response.getErrors().size());
        assertEquals("语法错误", response.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("GraphQLResponse.partial(data, errors) - 部分错误响应")
    void testGraphQLResponse_Partial() {
        String data = "部分数据";
        GraphQLResponse.GraphQLError error = new GraphQLResponse.GraphQLError("部分字段解析失败", null, null, null);

        GraphQLResponse<String> response = GraphQLResponse.partial(data, List.of(error));

        assertEquals(data, response.getData());
        assertNotNull(response.getErrors());
        assertEquals(1, response.getErrors().size());
    }

    @Test
    @DisplayName("GraphQLResponse.GraphQLError - 错误对象")
    void testGraphQLResponse_GraphQLError() {
        GraphQLResponse.Location location = new GraphQLResponse.Location(10, 20);
        GraphQLResponse.GraphQLError error = new GraphQLResponse.GraphQLError(
                "Validation failed",
                List.of(location),
                List.of("mutation", "createUser"),
                Map.of("code", "VALIDATION_ERROR")
        );

        assertEquals("Validation failed", error.getMessage());
        assertNotNull(error.getLocations());
        assertEquals(10, error.getLocations().get(0).getLine());
        assertEquals(20, error.getLocations().get(0).getColumn());
        assertNotNull(error.getPath());
        assertNotNull(error.getExtensions());
    }

    @Test
    @DisplayName("GraphQLResponse.Location - 位置对象")
    void testGraphQLResponse_Location() {
        GraphQLResponse.Location location = new GraphQLResponse.Location(5, 15);

        assertEquals(5, location.getLine());
        assertEquals(15, location.getColumn());
    }

    @Test
    @DisplayName("GraphQLResponse Builder 模式")
    void testGraphQLResponse_Builder() {
        GraphQLResponse<String> response = GraphQLResponse.<String>builder()
                .data("result")
                .errors(List.of())
                .extensions(Map.of("cache", true))
                .build();

        assertEquals("result", response.getData());
        assertNotNull(response.getErrors());
        assertTrue((Boolean) response.getExtensions().get("cache"));
    }

    // ==================== GrpcResponse 测试 ====================

    @Test
    @DisplayName("GrpcResponse.success(data) - 创建成功响应")
    void testGrpcResponse_Success() {
        String data = "gRPC 数据";
        GrpcResponse<String> response = GrpcResponse.success(data);

        assertTrue(response.isSuccess());
        assertEquals(GrpcResponse.STATUS_OK, response.getCode());
        assertEquals("OK", response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("GrpcResponse.success(code, data) - 自定义状态码的成功响应")
    void testGrpcResponse_SuccessWithCode() {
        String data = "数据";
        GrpcResponse<String> response = GrpcResponse.success(GrpcResponse.STATUS_OK, data);

        assertTrue(response.isSuccess());
        assertEquals(GrpcResponse.STATUS_OK, response.getCode());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("GrpcResponse.error(code, message) - 创建错误响应")
    void testGrpcResponse_Error() {
        GrpcResponse<Void> response = GrpcResponse.error(GrpcResponse.STATUS_NOT_FOUND, "资源未找到");

        assertFalse(response.isSuccess());
        assertEquals(GrpcResponse.STATUS_NOT_FOUND, response.getCode());
        assertEquals("资源未找到", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("GrpcResponse.error(code, message, details) - 带详情的错误响应")
    void testGrpcResponse_ErrorWithDetails() {
        GrpcResponse.ErrorDetail detail = new GrpcResponse.ErrorDetail(
                "google.rpc.BadRequest",
                Map.of("field", "email", "reason", "invalid format")
        );

        GrpcResponse<Void> response = GrpcResponse.error(
                GrpcResponse.STATUS_INVALID_ARGUMENT,
                "参数无效",
                List.of(detail)
        );

        assertFalse(response.isSuccess());
        assertEquals(GrpcResponse.STATUS_INVALID_ARGUMENT, response.getCode());
        assertNotNull(response.getDetails());
        assertEquals(1, response.getDetails().size());
        assertEquals("google.rpc.BadRequest", response.getDetails().get(0).getType());
    }

    @Test
    @DisplayName("GrpcResponse.isSuccess() - 判断是否成功")
    void testGrpcResponse_IsSuccess() {
        assertTrue(GrpcResponse.success("data").isSuccess());
        assertFalse(GrpcResponse.error(GrpcResponse.STATUS_INTERNAL, "error").isSuccess());
        assertFalse(GrpcResponse.error(GrpcResponse.STATUS_UNAVAILABLE, "unavailable").isSuccess());
    }

    @Test
    @DisplayName("GrpcResponse 状态码常量")
    void testGrpcResponse_StatusConstants() {
        assertEquals(0, GrpcResponse.STATUS_OK);
        assertEquals(1, GrpcResponse.STATUS_CANCELLED);
        assertEquals(2, GrpcResponse.STATUS_UNKNOWN);
        assertEquals(3, GrpcResponse.STATUS_INVALID_ARGUMENT);
        assertEquals(4, GrpcResponse.STATUS_DEADLINE_EXCEEDED);
        assertEquals(5, GrpcResponse.STATUS_NOT_FOUND);
        assertEquals(6, GrpcResponse.STATUS_ALREADY_EXISTS);
        assertEquals(7, GrpcResponse.STATUS_PERMISSION_DENIED);
        assertEquals(8, GrpcResponse.STATUS_RESOURCE_EXHAUSTED);
        assertEquals(9, GrpcResponse.STATUS_FAILED_PRECONDITION);
        assertEquals(10, GrpcResponse.STATUS_ABORTED);
        assertEquals(11, GrpcResponse.STATUS_OUT_OF_RANGE);
        assertEquals(12, GrpcResponse.STATUS_UNIMPLEMENTED);
        assertEquals(13, GrpcResponse.STATUS_INTERNAL);
        assertEquals(14, GrpcResponse.STATUS_UNAVAILABLE);
        assertEquals(15, GrpcResponse.STATUS_DATA_LOSS);
        assertEquals(16, GrpcResponse.STATUS_UNAUTHENTICATED);
    }

    @Test
    @DisplayName("GrpcResponse.ErrorDetail - 错误详情")
    void testGrpcResponse_ErrorDetail() {
        GrpcResponse.ErrorDetail detail = new GrpcResponse.ErrorDetail(
                "type.googleapis.com/google.rpc.ErrorInfo",
                Map.of("reason", "RATE_LIMITED", "domain", "example.com")
        );

        assertEquals("type.googleapis.com/google.rpc.ErrorInfo", detail.getType());
        assertNotNull(detail.getValue());
        assertEquals("RATE_LIMITED", detail.getValue().get("reason"));
    }

    @Test
    @DisplayName("GrpcResponse Builder 模式")
    void testGrpcResponse_Builder() {
        GrpcResponse<String> response = GrpcResponse.<String>builder()
                .code(GrpcResponse.STATUS_OK)
                .message("Success")
                .data("response-data")
                .details(List.of())
                .build();

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals("response-data", response.getData());
    }

    // ==================== JsonApiResponse 测试 ====================

    @Test
    @DisplayName("JsonApiResponse.ofResource(resource) - 创建单个资源响应")
    void testJsonApiResponse_OfResource() {
        JsonApiResponse.ResourceObject resource = new JsonApiResponse.ResourceObject(
                "users",
                "123",
                Map.of("name", "张三", "email", "zhangsan@example.com"),
                null,
                null
        );

        JsonApiResponse<Object> response = JsonApiResponse.ofResource(resource);

        assertNotNull(response.getData());
        assertNull(response.getErrors());
        assertTrue(response.getData() instanceof JsonApiResponse.ResourceObject);
    }

    @Test
    @DisplayName("JsonApiResponse.ofCollection(resources) - 创建资源集合响应")
    void testJsonApiResponse_OfCollection() {
        JsonApiResponse.ResourceObject resource1 = new JsonApiResponse.ResourceObject("users", "1", Map.of("name", "user1"), null, null);
        JsonApiResponse.ResourceObject resource2 = new JsonApiResponse.ResourceObject("users", "2", Map.of("name", "user2"), null, null);

        JsonApiResponse<Object> response = JsonApiResponse.ofCollection(Arrays.asList(resource1, resource2));

        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof List);
        @SuppressWarnings("unchecked")
        List<JsonApiResponse.ResourceObject> resources = (List<JsonApiResponse.ResourceObject>) response.getData();
        assertEquals(2, resources.size());
    }

    @Test
    @DisplayName("JsonApiResponse.ofErrors(errors) - 创建错误响应")
    void testJsonApiResponse_OfErrors() {
        JsonApiResponse.ErrorObject error = new JsonApiResponse.ErrorObject(
                "400",
                "VALIDATION_ERROR",
                "Validation Failed",
                "Email format is invalid",
                new JsonApiResponse.ErrorSource("/data/attributes/email", null),
                null
        );

        JsonApiResponse<Object> response = JsonApiResponse.ofErrors(List.of(error));

        assertNull(response.getData());
        assertNotNull(response.getErrors());
        assertEquals(1, response.getErrors().size());
        assertEquals("400", response.getErrors().get(0).getStatus());
        assertEquals("VALIDATION_ERROR", response.getErrors().get(0).getCode());
    }

    @Test
    @DisplayName("JsonApiResponse.ResourceObject - 资源对象")
    void testJsonApiResponse_ResourceObject() {
        JsonApiResponse.Links links = new JsonApiResponse.Links("/users/123", null, null, null, null, null);
        JsonApiResponse.ResourceObject resource = new JsonApiResponse.ResourceObject(
                "articles",
                "456",
                Map.of("title", "文章标题", "content", "文章内容"),
                Map.of("author", new JsonApiResponse.Relationship(
                        new JsonApiResponse.ResourceObject("users", "789", null, null, null),
                        null
                )),
                links
        );

        assertEquals("articles", resource.getType());
        assertEquals("456", resource.getId());
        assertNotNull(resource.getAttributes());
        assertNotNull(resource.getRelationships());
        assertNotNull(resource.getLinks());
        assertEquals("/users/123", resource.getLinks().getSelf());
    }

    @Test
    @DisplayName("JsonApiResponse.ErrorObject - 错误对象")
    void testJsonApiResponse_ErrorObject() {
        JsonApiResponse.ErrorSource source = new JsonApiResponse.ErrorSource("/data/attributes/password", "password");
        JsonApiResponse.ErrorObject error = new JsonApiResponse.ErrorObject(
                "422",
                "WEAK_PASSWORD",
                "Weak Password",
                "Password must be at least 8 characters",
                source,
                Map.of("minLength", 8)
        );

        assertEquals("422", error.getStatus());
        assertEquals("WEAK_PASSWORD", error.getCode());
        assertEquals("Weak Password", error.getTitle());
        assertEquals("Password must be at least 8 characters", error.getDetail());
        assertNotNull(error.getSource());
        assertEquals("/data/attributes/password", error.getSource().getPointer());
        assertEquals("password", error.getSource().getParameter());
        assertNotNull(error.getMeta());
    }

    @Test
    @DisplayName("JsonApiResponse.Links - 链接对象")
    void testJsonApiResponse_Links() {
        JsonApiResponse.Links links = new JsonApiResponse.Links(
                "/api/users",
                "/api/users/related",
                "/api/users?page=2",
                "/api/users?page=1",
                "/api/users?page=1",
                "/api/users?page=10"
        );

        assertEquals("/api/users", links.getSelf());
        assertEquals("/api/users/related", links.getRelated());
        assertEquals("/api/users?page=2", links.getNext());
        assertEquals("/api/users?page=1", links.getPrev());
        assertEquals("/api/users?page=1", links.getFirst());
        assertEquals("/api/users?page=10", links.getLast());
    }

    @Test
    @DisplayName("JsonApiResponse Builder 模式")
    void testJsonApiResponse_Builder() {
        JsonApiResponse.Links links = new JsonApiResponse.Links("/api/articles", null, null, null, null, null);
        JsonApiResponse<Object> response = JsonApiResponse.builder()
                .data(List.of())
                .meta(Map.of("total", 100))
                .links(links)
                .included(List.of())
                .build();

        assertNotNull(response.getData());
        assertNotNull(response.getMeta());
        assertEquals(100, response.getMeta().get("total"));
        assertNotNull(response.getLinks());
        assertNotNull(response.getIncluded());
    }
}
