<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 64 64"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <!-- 圆环：取意古钱币 -->
    <circle cx="32" cy="32" r="26" fill="none" stroke="currentColor" stroke-width="6.5" />
    <!-- 麦秆：竖线，上下与圆环相连 -->
    <rect x="30.25" y="9" width="3.5" height="46" fill="currentColor" />
    <!-- 麦穗：U 形两臂 + 底部弧 -->
    <path d="M23 13 H27.5 V24 A4.5 3.5 0 0 0 36.5 24 V13 H41 V24 A9 7.5 0 0 1 23 24 V13 Z" fill="currentColor" />
    <!-- 穗节：横杠 -->
    <rect x="21.5" y="37" width="21" height="4.5" fill="currentColor" />
  </svg>
</template>

<script setup>
// 品牌图标组件：手绘 SVG 复刻农行「麦穗 + 圆环」标志
// 唯一的 prop：size 控制图标边长（数字或字符串，默认 32px）
// 颜色跟随父级的 CSS color（currentColor），深底浅底都能放
defineProps({
  size: { type: [Number, String], default: 32 },
})
</script>
