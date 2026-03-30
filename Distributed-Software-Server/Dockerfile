# 第一阶段：构建阶段
FROM docker.xuanyuan.run/maven:latest AS build
WORKDIR /app

# 复制项目文件
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段，这里使用国产镜像
FROM docker.xuanyuan.run/library/eclipse-temurin:latest
WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=build /app/target/*.jar app.jar

# 设置时区（兼容多种基础镜像）
RUN if command -v apk > /dev/null 2>&1; then \
        apk add --no-cache tzdata && \
        cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
        echo "Asia/Shanghai" > /etc/timezone; \
    elif command -v apt-get > /dev/null 2>&1; then \
        apt-get update && \
        DEBIAN_FRONTEND=noninteractive apt-get install -y tzdata && \
        ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
        echo "Asia/Shanghai" > /etc/timezone && \
        rm -rf /var/lib/apt/lists/*; \
    fi

# 暴露端口
EXPOSE 8087

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
