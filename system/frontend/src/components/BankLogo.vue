<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 64 64"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <mask id="coin-hole">
      <rect width="64" height="64" fill="#fff" />
      <!-- 中央方孔：古钱币标志元素（真镂空，深浅底都自然） -->
      <rect x="25" y="25" width="14" height="14" fill="#000" />
    </mask>
    <!-- 圆形钱身：方孔镂空 -->
    <circle cx="32" cy="32" r="26" fill="currentColor" mask="url(#coin-hole)" />
    <!-- 内圈细线：增加钱币层次感 -->
    <circle cx="32" cy="32" r="19.5" fill="none" stroke="currentColor" stroke-width="2.5" opacity="0.55" />
  </svg>
</template>

<script setup>
// 品牌图标组件：几何化绘制「方孔圆钱」标志（宁波银行金黄主题）
// 唯一的 prop：size 控制图标边长（数字或字符串，默认 32px）
// 颜色跟随父级的 CSS color（currentColor），深底浅底都能放
defineProps({
  size: { type: [Number, String], default: 32 },
})
</script>
