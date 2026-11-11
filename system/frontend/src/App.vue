<template>
  <!-- 根组件：只放路由出口，页面切换全部交给 vue-router -->
  <router-view />
</template>

<style>
/* 宁波金黄主题：琥珀金稳重风，不做高亮刺眼的亮黄 */
:root {
  --el-color-primary: #b8860b;
  --el-color-primary-dark-2: #8a6408;
  --el-color-primary-light-3: #c99e46;
  --el-color-primary-light-5: #d9ba7d;
  --el-color-primary-light-7: #e9d6b3;
  --el-color-primary-light-8: #f2e6cc;
  --el-color-primary-light-9: #faf3e3;
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  background: #faf7ef;
  color: #2d2a22;
}
</style>
