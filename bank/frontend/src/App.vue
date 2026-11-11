<template>
  <!-- 根组件：只放路由出口，页面切换全部交给 vue-router -->
  <router-view />
</template>

<style>
/* 中国红主题：深红稳重风，与银行蓝、农行绿形成明显区分 */
:root {
  --el-color-primary: #b01e28;
  --el-color-primary-dark-2: #8c1620;
  --el-color-primary-light-3: #c74a52;
  --el-color-primary-light-5: #d87e84;
  --el-color-primary-light-7: #e9adb1;
  --el-color-primary-light-8: #f2cdd0;
  --el-color-primary-light-9: #fae8e9;
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  background: #f8f4f3;
  color: #2b2828;
}
</style>
