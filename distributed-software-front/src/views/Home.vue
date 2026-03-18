<template>
  <div class="home-container">
    <el-card class="home-card">
      <template #header>
        <div class="card-header">
          <h2>系统首页 (空白页)</h2>
        </div>
      </template>
      <div class="content">
        <p>欢迎登录分布式软件系统！</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.home-card {
  width: 500px;
  text-align: center;
}

.content {
  padding: 20px 0;
}
</style>

