<template>
  <!-- 根组件：只放路由出口，页面切换全部交给 vue-router -->
  <router-view />
</template>

<style>
/* 墨绿主题：与 bank-system 的银行蓝形成明显区分 */
:root {
  --el-color-primary: #0f6b4f;
  --el-color-primary-dark-2: #0b5540;
  --el-color-primary-light-3: #3e8e73;
  --el-color-primary-light-5: #71b09a;
  --el-color-primary-light-7: #a4d1c1;
  --el-color-primary-light-8: #c8e4d9;
  --el-color-primary-light-9: #e8f3ee;
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  background: #f4f7f5;
  color: #2b2b28;
}
</style>
