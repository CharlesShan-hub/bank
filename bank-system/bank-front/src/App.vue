<template>
  <router-view />
</template>

<style>
/* 全局基础样式（稳重银行蓝主题） */
:root {
  --el-color-primary: #14589c;
  --el-color-primary-dark-2: #0e4579;
  --el-color-primary-light-3: #5a8aba;
  --el-color-primary-light-5: #89abcd;
  --el-color-primary-light-7: #b8cde3;
  --el-color-primary-light-8: #d0deeb;
  --el-color-primary-light-9: #e7eef5;
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  height: 100%;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  background: #f2f5f9;
  color: #303133;
}
</style>
