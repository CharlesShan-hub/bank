<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 64 64"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <!-- 圆环：取意古钱币外缘 -->
    <circle cx="32" cy="32" r="26" fill="none" stroke="currentColor" stroke-width="6" />
    <!-- 中字竖笔：上下两端与圆环内缘相接 -->
    <rect x="29.25" y="9.5" width="5.5" height="45" fill="currentColor" />
    <!-- 中字方框：竖笔从中穿过，整体构成「中」字 -->
    <rect x="21" y="23" width="22" height="18" fill="none" stroke="currentColor" stroke-width="5" />
  </svg>
</template>

<script setup>
// 品牌图标组件：几何化重绘中国银行「中字圆环」标志
// 唯一的 prop：size 控制图标边长（数字或字符串，默认 32px）
// 颜色跟随父级的 CSS color（currentColor），深底浅底都能放
defineProps({
  size: { type: [Number, String], default: 32 },
})
</script>
